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

package services

import base.SpecBase
import connectors.EstatesConnector
import models.NameType
import models.declaration.{IndividualDeclaration, InternalServerError, TVN}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{EitherValues, RecoverMethods}
import play.api.inject.bind
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class DeclarationServiceSpec extends SpecBase with ScalaFutures with EitherValues with RecoverMethods {

  private val utr = "0987654321"
  private val mockConnector: EstatesConnector = mock[EstatesConnector]

  private val declaration: IndividualDeclaration = IndividualDeclaration(
    name = NameType("First", None, "Last"),
    email = None
  )

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "Declaration service" when {

    "declaration" must {

      "return TVN response when no errors" in {

        when(mockConnector.declare(any[String], any[JsValue])(any(), any()))
          .thenReturn(Future.successful(TVN("123456")))

        val app = applicationBuilder()
          .overrides(bind[EstatesConnector].toInstance(mockConnector))
          .build()

        val service = app.injector.instanceOf[DeclarationService]

        whenReady(service.declare(utr, declaration)) {
          result =>
            result mustBe TVN("123456")
        }
      }

      "return InternalServerError when errors" in {

        when(mockConnector.declare(any[String], any[JsValue])(any(), any()))
          .thenReturn(Future.successful(InternalServerError))

        val app = applicationBuilder()
          .overrides(bind[EstatesConnector].toInstance(mockConnector))
          .build()

        val service = app.injector.instanceOf[DeclarationService]

        whenReady(service.declare(utr, declaration)) {
          result =>
            result mustBe InternalServerError
        }
      }

    }
  }
}
