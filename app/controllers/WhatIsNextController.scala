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
import config.FrontendAppConfig
import connectors.EstatesConnector
import controllers.actions.Actions
import forms.WhatIsNextFormProvider
import models.WhatIsNext._
import models.{Enumerable, WhatIsNext}
import pages.WhatIsNextPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.WhatIsNextView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhatIsNextController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      repository: SessionRepository,
                                      actions: Actions,
                                      formProvider: WhatIsNextFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: WhatIsNextView,
                                      config: FrontendAppConfig,
                                      connector: EstatesConnector
                                    )(implicit ec: ExecutionContext)

  extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form: Form[WhatIsNext] = formProvider()

  def onPageLoad(): Action[AnyContent] = actions.authenticatedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatIsNextPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.authenticatedForUtr.async {
    implicit request =>

      def clearTransforms(value: WhatIsNext): Future[HttpResponse] = {
        if (request.userAnswers.get(WhatIsNextPage).contains(value)) {
          Future.successful(HttpResponse(OK, "Selection unchanged. No need to clear transforms."))
        } else {
          connector.clearTransformations(request.utr)
        }
      }

      def redirectUrl(value: WhatIsNext): String = value match {
        case DeclareNewPersonalRep => config.addNewPersonalRepUrl(request.utr)
        case MakeChanges => config.amendExistingPersonalRepUrl(request.utr)
        case CloseEstate => controllers.closure.routes.HasAdministrationPeriodEndedYesNoController.onPageLoad().url
      }

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {

          for {
            _ <- clearTransforms(value)
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsNextPage, value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(redirectUrl(value))
        }
      )
  }
}
