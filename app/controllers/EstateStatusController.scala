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

package controllers

import connectors.EstatesConnector
import controllers.actions.Actions
import javax.inject.Inject
import models.GetEstate
import models.http._
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html._

import scala.concurrent.{ExecutionContext, Future}

class EstateStatusController @Inject()(
                                        val controllerComponents: MessagesControllerComponents,
                                        actions: Actions,
                                        connector: EstatesConnector,
                                        utrDoesNotMatchRecordsView: UtrDoesNotMatchRecordsView,
                                        inProcessingView: InProcessingView,
                                        closedView: ClosedView,
                                        problemWithServiceView: ProblemWithServiceView,
                                        accountNotLinkedView: AccountNotLinkedView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val fakeUtr: String = "1234567890"

  def onPageLoad(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      connector.getEstate(fakeUtr) flatMap {
        case Processed(estate, _) =>
          Logger.info(s"[EstateStatusController] $fakeUtr estate is in a processed state")
          authenticateForUtrAndExtract(fakeUtr, estate)
        case Processing =>
          Logger.info(s"[EstateStatusController] $fakeUtr unable to retrieve estate due it being in processing")
          Future.successful(Redirect(controllers.routes.EstateStatusController.inProcessing()))
        case Closed =>
          Logger.info(s"[EstateStatusController] $fakeUtr unable to retrieve estate due it being closed")
          Future.successful(Redirect(controllers.routes.EstateStatusController.closed()))
        case UtrNotFound =>
          Logger.info(s"[EstateStatusController] $fakeUtr unable to retrieve estate due to UTR not being found")
          Future.successful(Redirect(controllers.routes.EstateStatusController.utrDoesNotMatchRecords()))
        case _ =>
          Logger.warn(s"[EstateStatusController] $fakeUtr unable to retrieve estate due to an error")
          Future.successful(Redirect(controllers.routes.EstateStatusController.problemWithService()))
      }
  }

  private def authenticateForUtrAndExtract(utr: String, estate: GetEstate): Future[Result] = {
    ???
  }

  def inProcessing(): Action[AnyContent] = actions.authWithData {
    implicit request =>
      Ok(inProcessingView(fakeUtr))
  }

  def closed(): Action[AnyContent] = actions.authWithData {
    implicit request =>
      Ok(closedView(fakeUtr))
  }

  def utrDoesNotMatchRecords(): Action[AnyContent] = actions.authWithData {
    implicit request =>
      Ok(utrDoesNotMatchRecordsView())
  }

  def problemWithService(): Action[AnyContent] = actions.authWithData {
    implicit request =>
      Ok(problemWithServiceView())
  }

  def accountNotLinked(): Action[AnyContent] = actions.authWithData {
    implicit request =>
      Ok(accountNotLinkedView(fakeUtr))
  }
}
