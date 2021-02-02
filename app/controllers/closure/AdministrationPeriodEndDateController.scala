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

package controllers.closure

import java.time.LocalDate

import connectors.EstatesConnector
import controllers.actions.Actions
import forms.DateFormProvider
import javax.inject.Inject
import pages.closure.AdministrationPeriodEndDatePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.closure.AdministrationPeriodEndDateView

import scala.concurrent.{ExecutionContext, Future}


class AdministrationPeriodEndDateController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       sessionRepository: SessionRepository,
                                                       actions: Actions,
                                                       formProvider: DateFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       view: AdministrationPeriodEndDateView,
                                                       connector: EstatesConnector
                                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val prefix: String = "closure.administrationPeriodEndDate"

  def onPageLoad(): Action[AnyContent] = actions.authenticatedForUtr.async {
    implicit request =>

      for {
        startDate <- connector.getDateOfDeath(request.utr)
      } yield {
        val form = formProvider.withConfig(prefix, startDate)

        val preparedForm = request.userAnswers.get(AdministrationPeriodEndDatePage) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm))
      }
  }

  def onSubmit(): Action[AnyContent] = actions.authenticatedForUtr.async {
    implicit request =>

      def render(startDate: LocalDate): Future[Result] = {
        val form = formProvider.withConfig(prefix, startDate)

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors))),

          date =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AdministrationPeriodEndDatePage, date))
              _ <- sessionRepository.set(updatedAnswers)
              _ <- connector.close(request.utr, date)
            } yield Redirect(controllers.closure.routes.ChangePersonalRepDetailsYesNoController.onPageLoad())
        )
      }

      for {
        startDate <- connector.getDateOfDeath(request.utr)
        result <- render(startDate)
      } yield result
  }
}
