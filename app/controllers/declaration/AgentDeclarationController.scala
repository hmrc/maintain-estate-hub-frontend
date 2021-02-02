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

package controllers.declaration

import java.time.LocalDateTime

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import controllers.actions._
import forms.declaration.AgentDeclarationFormProvider
import models.declaration.{AgentDeclaration, TVN}
import pages._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DeclarationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.declaration.AgentDeclarationView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentDeclarationController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            sessionRepository: SessionRepository,
                                            actions: Actions,
                                            formProvider: AgentDeclarationFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: AgentDeclarationView,
                                            service: DeclarationService,
                                            appConfig: FrontendAppConfig
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[AgentDeclaration] = formProvider()

  def onPageLoad(): Action[AnyContent] = actions.requireAgent {
    implicit request =>

      val preparedForm = request.userAnswers.get(AgentDeclarationPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, appConfig.declarationEmailEnabled))
  }

  def onSubmit(): Action[AnyContent] = actions.requireAgent.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, appConfig.declarationEmailEnabled))),

        declaration => {
          service.declare(request, declaration) flatMap {
            case TVN(tvn) =>
              for {
                updatedAnswers <- Future.fromTry(
                  request.userAnswers
                    .set(AgentDeclarationPage, declaration)
                    .flatMap(_.set(SubmissionDatePage, LocalDateTime.now))
                    .flatMap(_.set(TVNPage, tvn))
                )
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(controllers.confirmation.routes.ConfirmationController.onPageLoad())
            case _ =>
              Future.successful(Redirect(controllers.declaration.routes.ProblemDeclaringController.onPageLoad()))
          }
        }
      )
  }
}
