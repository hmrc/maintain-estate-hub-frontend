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

package controllers.print

import connectors.EstatesConnector
import controllers.actions.Actions
import javax.inject.Inject
import models.http.Processed
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import printers.PrintHelper
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.print.DeclaredAnswersView

import scala.concurrent.ExecutionContext

class DeclaredAnswersController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                actions: Actions,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: DeclaredAnswersView,
                                                print: PrintHelper,
                                                estatesConnector: EstatesConnector
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad() = actions.requireTvn.async {
    implicit request =>

      estatesConnector.getTransformedEstate(request.utr) map {
        case Processed(estate, _) =>

          val personalRep = print.personalRepresentative(estate)
          val estateName = print.estateName(estate)

          Ok(view(personalRep, estateName))
        case estate =>
          Logger.warn(s"[DeclaredAnswersController] unable to render declared answers due to estate being in state: $estate")
          Redirect(controllers.routes.EstateStatusController.problemWithService())
      } recover {
        case e =>
          Logger.error(s"[DeclaredAnswersController] unable to render declared answers due to ${e.getMessage}")
          Redirect(controllers.routes.EstateStatusController.problemWithService())
      }

  }

}