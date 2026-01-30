/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.testOnlyDoNotUseInAppConf

import com.google.inject.Inject
import config.FrontendAppConfig
import play.api.Logging
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

class TestUserConnector @Inject() (http: HttpClientV2, config: FrontendAppConfig) {

  private val dataUrl: String = s"${config.enrolmentStoreProxyUrl}/enrolment-store-stub/data"

  object InsertedReads {

    implicit lazy val httpReads: HttpReads[Unit] =
      (method: String, url: String, response: HttpResponse) =>
        // Ignore the response from enrolment-store-stub
        ()

  }

  def insert(user: JsValue)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {
    val headers = Seq(
      ("content-type", "application/json")
    )
    http.post(url"$dataUrl").setHeader(headers: _*).withBody(user).execute[Unit](InsertedReads.httpReads, ec)
  }

  def delete()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    http.delete(url"$dataUrl").execute[HttpResponse]

}

class EnrolmentStoreStubController @Inject() (
  connector: TestUserConnector,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with Logging {

  def insertTestUserIntoEnrolmentStore(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.info(s"[Session ID: ${Session.id(hc)}] inserting test user: ${request.body}")
    connector.insert(request.body).map(_ => Ok)
  }

  def flush(): Action[AnyContent] = Action.async { implicit request =>
    logger.info(s"[Session ID: ${Session.id(hc)}] flushing test users from enrolment-store")
    connector.delete().map(_ => Ok)
  }

}
