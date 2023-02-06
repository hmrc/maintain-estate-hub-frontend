/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import connectors.EstatesConnector
import forms.DateFormProvider
import models.UserAnswers
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.UTRPage
import pages.closure.AdministrationPeriodEndDatePage
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import views.html.closure.AdministrationPeriodEndDateView

import scala.concurrent.Future

class AdministrationPeriodEndDateControllerSpec extends SpecBase {

  private val formProvider = new DateFormProvider()
  private val startDate: LocalDate = LocalDate.parse("2000-01-01")
  private val form: Form[LocalDate] = formProvider.withConfig("closure.administrationPeriodEndDate", startDate)
  private lazy val dateRoute: String = routes.AdministrationPeriodEndDateController.onPageLoad().url
  private val utr: String = "utr"
  private val validAnswer: LocalDate = LocalDate.parse("2020-01-01")

  private def applicationBuilder(userAnswers: UserAnswers): Application = {

    when(fakeConnector.getDateOfDeath(any())(any(), any())).thenReturn(Future.successful(startDate))
    when(fakeConnector.close(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

    super.applicationBuilder(Some(userAnswers))
    .overrides(
      bind[EstatesConnector].toInstance(fakeConnector)
    ).build()
  }

  override def emptyUserAnswers: UserAnswers = super.emptyUserAnswers.set(UTRPage, utr).success.value

  "AdministrationPeriodEndDate Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(emptyUserAnswers)

      val request = FakeRequest(GET, dateRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AdministrationPeriodEndDateView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(AdministrationPeriodEndDatePage, validAnswer).success.value

      val application = applicationBuilder(userAnswers)

      val request = FakeRequest(GET, dateRoute)

      val view = application.injector.instanceOf[AdministrationPeriodEndDateView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(emptyUserAnswers)

      val request = FakeRequest(POST, dateRoute)
        .withFormUrlEncodedBody(
          "value.day" -> validAnswer.getDayOfMonth.toString,
          "value.month" -> validAnswer.getMonthValue.toString,
          "value.year" -> validAnswer.getYear.toString
        )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.closure.routes.ChangePersonalRepDetailsYesNoController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(emptyUserAnswers)

      val request = FakeRequest(POST, dateRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AdministrationPeriodEndDateView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, dateRoute)
        .withFormUrlEncodedBody(
          "value.day" -> validAnswer.getDayOfMonth.toString,
          "value.month" -> validAnswer.getMonthValue.toString,
          "value.year" -> validAnswer.getYear.toString
        )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
