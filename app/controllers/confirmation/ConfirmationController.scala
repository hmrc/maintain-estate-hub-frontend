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

package controllers.confirmation

import com.google.inject.{Inject, Singleton}
import connectors.EstatesConnector
import controllers.actions.Actions
import models.http.Processed
import models.{EstatePerRepIndType, EstatePerRepOrgType, PersonalRepresentativeType}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session
import views.html.confirmation.ConfirmationView

import scala.concurrent.ExecutionContext

@Singleton
class ConfirmationController @Inject()(
                                        override implicit val messagesApi: MessagesApi,
                                        actions: Actions,
                                        val controllerComponents: MessagesControllerComponents,
                                        confirmationView: ConfirmationView,
                                        estatesConnector: EstatesConnector
                                      )(implicit ec: ExecutionContext
) extends FrontendBaseController with I18nSupport with Logging {

  private def personalRepName(personalRepresentative: PersonalRepresentativeType)
                             (implicit request: Request[_]): String = {
    personalRepresentative match {
      case PersonalRepresentativeType(Some(EstatePerRepIndType(name, _, _, _, _, _, _, _)), None) =>
        name.displayName
      case PersonalRepresentativeType(None, Some(EstatePerRepOrgType(name, _, _, _, _, _, _))) =>
        name
      case _ =>
        messagesApi("confirmationPage.personalRepresentative.default")(request.lang)
    }
  }

  def onPageLoad(): Action[AnyContent] = actions.requireTvn.async {
    implicit request =>

      val isAgent = request.user.affinityGroup == Agent

      estatesConnector.getTransformedEstate(request.utr) map {
        case Processed(estate, _) =>
          val name = personalRepName(estate.estate.entities.personalRepresentative)
          Ok(confirmationView(name, request.tvn, isAgent, estate.isClosing))
        case _ =>
          logger.warn(s"[[Session ID: ${Session.id(hc)}] unable to render confirmation")
          Redirect(controllers.routes.EstateStatusController.problemWithService())
      } recover {
        case e =>
          logger.error(s"[Session ID: ${Session.id(hc)}] unable to render confirmation due to ${e.getMessage}")
          Redirect(controllers.routes.EstateStatusController.problemWithService())
      }
  }
}
