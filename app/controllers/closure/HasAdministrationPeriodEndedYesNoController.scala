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

package controllers.closure

import com.google.inject.Inject
import controllers.actions._
import forms.YesNoFormProvider
import pages.closure.HasAdministrationPeriodEndedYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.closure.HasAdministrationPeriodEndedYesNoView

import scala.concurrent.{ExecutionContext, Future}

class HasAdministrationPeriodEndedYesNoController @Inject()(
                                                             override val messagesApi: MessagesApi,
                                                             sessionRepository: SessionRepository,
                                                             actions: Actions,
                                                             yesNoFormProvider: YesNoFormProvider,
                                                             val controllerComponents: MessagesControllerComponents,
                                                             view: HasAdministrationPeriodEndedYesNoView
                                                           )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("closure.hasAdministrationPeriodEnded")

  def onPageLoad(): Action[AnyContent] = actions.authenticatedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(HasAdministrationPeriodEndedYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.authenticatedForUtr.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(HasAdministrationPeriodEndedYesNoPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            if (value) {
              Redirect(controllers.closure.routes.AdministrationPeriodEndDateController.onPageLoad())
            } else {
              Redirect(controllers.closure.routes.AdministrationPeriodEndDateNeededController.onPageLoad())
            }
          }
        }
      )

  }

}
