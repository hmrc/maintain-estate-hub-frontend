/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.declaration

import com.google.inject.{Inject, Singleton}
import controllers.actions._
import forms.declaration.InternationalAddressFormProvider
import models.declaration.InternationalAddress
import pages.declaration.AgencyRegisteredAddressInternationalPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.declaration.AgencyRegisteredAddressInternationalView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgencyRegisteredAddressInternationalController @Inject()(
                                                                override val messagesApi: MessagesApi,
                                                                sessionRepository: SessionRepository,
                                                                actions: Actions,
                                                                formProvider: InternationalAddressFormProvider,
                                                                countryOptions: CountryOptionsNonUK,
                                                                val controllerComponents: MessagesControllerComponents,
                                                                view: AgencyRegisteredAddressInternationalView
                                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[InternationalAddress] = formProvider()

  def onPageLoad(): Action[AnyContent] = actions.authenticatedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(AgencyRegisteredAddressInternationalPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options()))
  }

  def onSubmit(): Action[AnyContent] = actions.authenticatedForUtr.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options()))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(
              request.userAnswers
                .set(AgencyRegisteredAddressInternationalPage, value)
            )
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(controllers.declaration.routes.AgentDeclarationController.onPageLoad())
        }
      )

  }

}
