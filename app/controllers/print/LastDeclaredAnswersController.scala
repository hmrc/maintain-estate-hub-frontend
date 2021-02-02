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

package controllers.print

import connectors.EstatesConnector
import controllers.actions.Actions
import javax.inject.Inject
import models.http.Processed
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import printers.PrintHelper
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session
import views.html.print.LastDeclaredAnswersView

import scala.concurrent.ExecutionContext

class LastDeclaredAnswersController  @Inject()(
                                                override val messagesApi: MessagesApi,
                                                actions: Actions,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: LastDeclaredAnswersView,
                                                print: PrintHelper,
                                                estatesConnector: EstatesConnector
                                              )(implicit ec: ExecutionContext
) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] = actions.authenticatedForUtr.async {
    implicit request =>

      estatesConnector.getEstate(request.utr) map {
        case Processed(estate, _) =>

          val personalRep = print.personalRepresentative(estate)
          val estateName = print.estateName(estate)
          val deceasedPerson = print.deceasedPerson(estate)

          Ok(view(
            personalRepresentative = personalRep,
            estateNameAndDeceasedPerson = estateName ++ deceasedPerson
          ))
        case estate =>
          logger.warn(s"[Session ID: ${Session.id(hc)}] unable to render last declared answers due to estate being in state: $estate")
          Redirect(controllers.routes.EstateStatusController.problemWithService())
      } recover {
        case e =>
          logger.error(s"[Session ID: ${Session.id(hc)}] unable to render last declared answers due to ${e.getMessage}")
          Redirect(controllers.routes.EstateStatusController.problemWithService())
      }

  }

}