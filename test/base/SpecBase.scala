/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package base

import config.FrontendAppConfig
import controllers.actions._
import models.UserAnswers
import models.requests.User
import org.scalatest.TryValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.Json
import play.api.mvc.PlayBodyParsers
import play.api.test.FakeRequest
import utils.countryOptions.{CountryOptions, CountryOptionsNonUK}

import scala.concurrent.ExecutionContext

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite with TryValues with Mocked with ScalaFutures {

  val userAnswersId = "id"

  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest = FakeRequest("", "")

  implicit def executionContext = injector.instanceOf[ExecutionContext]

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  private def applicationBuilderInterface(userAnswers: Option[UserAnswers] = None,
                                          fakeIdentifierAction: IdentifierAction,
                                          utr: String = "utr"
                                         ) : GuiceApplicationBuilder = {
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to(fakeIdentifierAction),
        bind[UTRAuthenticationAction].toInstance(new FakeUTRAuthenticationAction(utr)),
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )
  }

  protected def applicationBuilderForUser(userAnswers: Option[UserAnswers] = None,
                                          user: User): GuiceApplicationBuilder = {
    val parsers = injector.instanceOf[PlayBodyParsers]
    val fakeIdentifierAction = new FakeUserIdentifierAction(parsers)(user)

    applicationBuilderInterface(userAnswers, fakeIdentifierAction)
  }

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None,
                                   utr: String = "utr"
                                  ): GuiceApplicationBuilder = {
    val fakeIdentifierAction = injector.instanceOf[FakeOrganisationIdentifierAction]
    applicationBuilderInterface(userAnswers, fakeIdentifierAction, utr)
  }
}
