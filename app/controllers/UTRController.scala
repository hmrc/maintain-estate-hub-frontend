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

import controllers.actions._
import forms.UTRFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import pages.UTRPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.UTRView

import scala.concurrent.{ExecutionContext, Future}

class UTRController @Inject()(
                               override val messagesApi: MessagesApi,
                               sessionRepository: SessionRepository,
                               actions: Actions,
                               formProvider: UTRFormProvider,
                               val controllerComponents: MessagesControllerComponents,
                               view: UTRView
                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = actions.authWithSession.async {
    implicit request =>
      Future.successful(Ok(view(form)))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithSession.async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),
        utr => {
          for {
            _ <- sessionRepository.resetCache(request.user.internalId)
            newSessionWithUtr <- Future.fromTry {
              UserAnswers.startNewSession(request.user.internalId).set(UTRPage, utr)
            }
            _ <- sessionRepository.set(newSessionWithUtr)
          } yield Redirect(controllers.routes.EstateStatusController.checkStatus())
        }
      )
  }

}
