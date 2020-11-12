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
import config.FrontendAppConfig
import controllers.actions._
import forms.YesNoFormProvider
import pages.closure.ChangePersonalRepDetailsYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.closure.ChangePersonalRepDetailsYesNoView

import scala.concurrent.{ExecutionContext, Future}

class ChangePersonalRepDetailsYesNoController @Inject()(
                                                         override val messagesApi: MessagesApi,
                                                         sessionRepository: SessionRepository,
                                                         actions: Actions,
                                                         yesNoFormProvider: YesNoFormProvider,
                                                         val controllerComponents: MessagesControllerComponents,
                                                         view: ChangePersonalRepDetailsYesNoView,
                                                         config: FrontendAppConfig
                                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("closure.changePersonalRepDetailsYesNo")

  def onPageLoad(): Action[AnyContent] = actions.authenticatedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(ChangePersonalRepDetailsYesNoPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ChangePersonalRepDetailsYesNoPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(
              (value, request.user.affinityGroup) match {
                case (true, _) => config.amendExistingPersonalRepUrl(request.utr)
                case (false, Agent) => controllers.declaration.routes.AgencyRegisteredAddressUkYesNoController.onPageLoad().url
                case _ => controllers.declaration.routes.IndividualDeclarationController.onPageLoad().url
              }
            )
          }
        }
      )

  }

}
