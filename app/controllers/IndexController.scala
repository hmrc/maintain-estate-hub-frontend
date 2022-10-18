/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.actions.Actions
import javax.inject.Inject
import models.UserAnswers
import pages.UTRPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 actions: Actions,
                                 repository: SessionRepository
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad: Action[AnyContent] = actions.authWithSession.async {
    implicit request =>

      request.user.enrolments.enrolments
        .find(_.key equals "HMRC-TERS-ORG")
        .flatMap(_.identifiers.find(_.key equals "SAUTR"))
        .map(_.value)
        .fold {
          logger.info(s"[Session ID: ${Session.id(hc)}] user ${request.user.affinityGroup} is not enrolled, redirect to ask for UTR")
          Future.successful(Redirect(controllers.routes.UTRController.onPageLoad()))
        } {
          utr =>
            for {
              _ <- repository.resetCache(request.user.internalId)
              newSessionWithUtr <- Future.fromTry {
                UserAnswers.startNewSession(request.user.internalId).set(UTRPage, utr)
              }
              _ <- repository.set(newSessionWithUtr)
            } yield {
              logger.info(
                s"[Session ID: ${Session.id(hc)}][UTR: $utr] $utr organisation user is enrolled, storing UTR in user answers, checking status of estate"
              )
              Redirect(controllers.routes.EstateStatusController.checkStatus())
            }
        }
  }
}
