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

import connectors.{EstatesConnector, EstatesStoreConnector}
import controllers.actions.Actions
import javax.inject.Inject
import models.http._
import models.requests.DataRequest
import models.{GetEstate, NormalMode}
import pages.UTRPage
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
                                        estateStoreConnector: EstatesStoreConnector,
                                        utrDoesNotMatchRecordsView: UtrDoesNotMatchRecordsView,
                                        inProcessingView: InProcessingView,
                                        closedView: ClosedView,
                                        lockedView: LockedView,
                                        problemWithServiceView: ProblemWithServiceView,
                                        accountNotLinkedView: AccountNotLinkedView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      enforceUtr() { utr =>
        estateStoreConnector.get(utr).flatMap {
          case Some(lock) if lock.estateLocked =>
            Logger.info(s"[EstateStatusController] $utr user has failed IV 3 times, locked out for 30 minutes")
            Future.successful(Redirect(controllers.routes.EstateStatusController.locked()))
          case _ =>
            Logger.info(s"[EstateStatusController] $utr user has not been locked out from IV")
            tryToPlayback(utr)
        }
      }
  }

  def inProcessing(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(inProcessingView(utr)))
      }
  }

  def closed(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(closedView(utr)))
      }
  }

  def utrDoesNotMatchRecords(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { _ =>
        Future.successful(Ok(utrDoesNotMatchRecordsView()))
      }
  }

  def problemWithService(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { _ =>
        Future.successful(Ok(problemWithServiceView()))
      }
  }

  def accountNotLinked(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(accountNotLinkedView(utr)))
      }
  }

  def locked(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(lockedView(utr)))
      }
  }

  private def tryToPlayback(utr: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    connector.getEstate(utr) flatMap {
      case Processed(estate, _) =>
        Logger.info(s"[EstateStatusController] $utr estate is in a processed state")
        authenticateForUtrAndExtract(utr, estate)
      case Processing =>
        Logger.info(s"[EstateStatusController] $utr unable to retrieve estate due it being in processing")
        Future.successful(Redirect(controllers.routes.EstateStatusController.inProcessing()))
      case Closed =>
        Logger.info(s"[EstateStatusController] $utr unable to retrieve estate due it being closed")
        Future.successful(Redirect(controllers.routes.EstateStatusController.closed()))
      case UtrNotFound =>
        Logger.info(s"[EstateStatusController] $utr unable to retrieve estate due to UTR not being found")
        Future.successful(Redirect(controllers.routes.EstateStatusController.utrDoesNotMatchRecords()))
      case _ =>
        Logger.warn(s"[EstateStatusController] $utr unable to retrieve estate due to an error")
        Future.successful(Redirect(controllers.routes.EstateStatusController.problemWithService()))
    }
  }
  
  private def enforceUtr()(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(UTRPage) match {
      case None =>
        Logger.info(s"[EstateStatusController] no UTR in user answers, redirecting to ask for it")
        Future.successful(Redirect(routes.UTRController.onPageLoad(NormalMode)))
      case Some(utr) =>
        Logger.info(s"[EstateStatusController] checking status of estate for $utr")
        block(utr)
    }
  }

  private def authenticateForUtrAndExtract(utr: String, estate: GetEstate): Future[Result] = {
    ???
  }
}
