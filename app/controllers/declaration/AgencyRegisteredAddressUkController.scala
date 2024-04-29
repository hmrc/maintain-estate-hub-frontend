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
import forms.declaration.UKAddressFormProvider
import models.declaration.UKAddress
import pages.declaration.AgencyRegisteredAddressUkPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.declaration.AgencyRegisteredAddressUkView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgencyRegisteredAddressUkController @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     sessionRepository: SessionRepository,
                                                     actions: Actions,
                                                     formProvider: UKAddressFormProvider,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: AgencyRegisteredAddressUkView
                                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[UKAddress] = formProvider()

  def onPageLoad(): Action[AnyContent] = actions.authenticatedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(AgencyRegisteredAddressUkPage) match {
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
            updatedAnswers <- Future.fromTry(
              request.userAnswers
                .set(AgencyRegisteredAddressUkPage, value)
            )
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(controllers.declaration.routes.AgentDeclarationController.onPageLoad())
        }
      )

  }

}
