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

package controllers

import controllers.actions.Actions
import javax.inject.Inject
import pages.UTRPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html._

class EstateNotClaimedController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  actions: Actions,
  view: EstateNotClaimedView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.authWithData { implicit request =>
    request.userAnswers.get(UTRPage) match {
      case Some(utr) =>
        Ok(view(utr))
      case None      =>
        Redirect(routes.UTRController.onPageLoad())
    }
  }

}
