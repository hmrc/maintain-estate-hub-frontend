/*
 * Copyright 2021 HM Revenue & Customs
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
import pages.UTRPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session
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
                                      )(implicit ec: ExecutionContext
) extends FrontendBaseController with I18nSupport with Logging {

  def checkStatus(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      enforceUtr() { utr =>
        estateStoreConnector.get(utr).flatMap {
          case Some(lock) if lock.estateLocked =>
            logger.info(s"[Session ID: ${Session.id(hc)}][UTR: $utr]  $utr user has failed IV 3 times, locked out for 30 minutes")
            Future.successful(Redirect(controllers.routes.EstateStatusController.locked()))
          case _ =>
            logger.info(s"[Session ID: ${Session.id(hc)}][UTR: $utr] $utr user has not been locked out from IV")
            tryToPlayback(utr)
        }
      }
  }

  def inProcessing(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(inProcessingView(utr, isAgentUser)))
      }
  }

  def closed(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { utr =>
        Future.successful(Ok(closedView(utr, isAgentUser)))
      }
  }

  def utrDoesNotMatchRecords(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { _ =>
        Future.successful(Ok(utrDoesNotMatchRecordsView(isAgentUser)))
      }
  }

  def problemWithService(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      enforceUtr() { _ =>
        Future.successful(Ok(problemWithServiceView(isAgentUser)))
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

  private def isAgentUser(implicit request: DataRequest[AnyContent]): Boolean = {
    request.user.affinityGroup == Agent
  }

  private def tryToPlayback(utr: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    logger.info(s"[Session ID: ${Session.id(hc)}][UTR: $utr] getting estate for $utr")
    connector.getEstate(utr) flatMap {
      case Processed(estate, _) =>
        logger.info(s"[Session ID: ${Session.id(hc)}][UTR: $utr] $utr estate is in a processed state")
        Future.successful(Redirect(controllers.routes.ViewLastDeclaredAnswersYesNoController.onPageLoad()))
      case Processing =>
        logger.info(s"[Session ID: ${Session.id(hc)}][UTR: $utr] $utr unable to retrieve estate due it being in processing")
        Future.successful(Redirect(controllers.routes.EstateStatusController.inProcessing()))
      case Closed =>
        logger.info(s"[Session ID: ${Session.id(hc)}][UTR: $utr] $utr unable to retrieve estate due it being closed")
        Future.successful(Redirect(controllers.routes.EstateStatusController.closed()))
      case UtrNotFound =>
        logger.info(s"[Session ID: ${Session.id(hc)}][UTR: $utr] $utr unable to retrieve estate due to UTR not being found")
        Future.successful(Redirect(controllers.routes.EstateStatusController.utrDoesNotMatchRecords()))
      case _ =>
        logger.warn(s"[Session ID: ${Session.id(hc)}][UTR: $utr] $utr unable to retrieve estate due to an error")
        Future.successful(Redirect(controllers.routes.EstateStatusController.problemWithService()))
    }
  }
  
  private def enforceUtr()(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(UTRPage) match {
      case None =>
        logger.info(s"[Session ID: ${Session.id(hc)}] no UTR in user answers, redirecting to ask for it")
        Future.successful(Redirect(routes.UTRController.onPageLoad()))
      case Some(utr) =>
        logger.info(s"[Session ID: ${Session.id(hc)}][UTR: $utr] checking status of estate for $utr")
        block(utr)
    }
  }
}
