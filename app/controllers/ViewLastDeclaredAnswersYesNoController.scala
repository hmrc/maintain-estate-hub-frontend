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

package controllers

import com.google.inject.{Inject, Singleton}
import controllers.actions.Actions
import forms.YesNoFormProvider
import pages.ViewLastDeclaredAnswersYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ViewLastDeclaredAnswersYesNoView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewLastDeclaredAnswersYesNoController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        actions: Actions,
                                                        repository: SessionRepository,
                                                        yesNoFormProvider: YesNoFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: ViewLastDeclaredAnswersYesNoView
                                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("viewLastDeclaredYesNo")

  def onPageLoad(): Action[AnyContent] = actions.authenticatedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(ViewLastDeclaredAnswersYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.utr))
  }

  def onSubmit(): Action[AnyContent] = actions.authenticatedForUtr.async {
    implicit request =>

        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, request.utr))),

          value => {
            for {
              updatedAnswers <- Future.fromTry(
                request.userAnswers.set(ViewLastDeclaredAnswersYesNoPage, value)
              )
              _ <- repository.set(updatedAnswers)
            } yield {
              if (value) {
                Redirect(controllers.print.routes.LastDeclaredAnswersController.onPageLoad())
              } else {
                Redirect(controllers.routes.WhatIsNextController.onPageLoad())
              }
            }
          }
        )
  }

  def checkForUTR(): Action[AnyContent] = actions.verifyUTR {
    implicit request =>

      val preparedForm = request.userAnswers.get(ViewLastDeclaredAnswersYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.utr))
  }

}
