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

import base.SpecBase
import forms.YesNoFormProvider
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{UTRPage, ViewLastDeclaredAnswersYesNoPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ViewLastDeclaredAnswersYesNoView

import scala.concurrent.Future

class ViewLastDeclaredAnswersYesNoControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new YesNoFormProvider()
  val form: Form[Boolean] = formProvider.withPrefix("viewLastDeclaredYesNo")
  val utr: String = "utr"
  lazy val yesNoRoute: String = routes.ViewLastDeclaredAnswersYesNoController.onPageLoad().url

  override val emptyUserAnswers: UserAnswers = super.emptyUserAnswers
    .set(UTRPage, utr).success.value

  "ViewLastDeclaredAnswersYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, yesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ViewLastDeclaredAnswersYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, utr)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(ViewLastDeclaredAnswersYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, yesNoRoute)

      val view = application.injector.instanceOf[ViewLastDeclaredAnswersYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), utr)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when YES is submitted" in {

      val mockRepository = mock[SessionRepository]

      when(mockRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, yesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.print.routes.LastDeclaredAnswersController.onPageLoad().url

      application.stop()
    }

    "redirect to the next page when NO is submitted" in {

      val mockRepository = mock[SessionRepository]

      when(mockRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, yesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.WhatIsNextController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, yesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ViewLastDeclaredAnswersYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, utr)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, yesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect " in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.ViewLastDeclaredAnswersYesNoController.checkForUTR().url)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }
  }
}
