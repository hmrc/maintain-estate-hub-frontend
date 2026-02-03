/*
 * Copyright 2026 HM Revenue & Customs
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
import pages.UTRPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{AgentNotAuthorisedView, OldAgentNotAuthorisedView}

class AgentNotAuthorisedControllerSpec extends SpecBase {

  "Agent Not Authorised Controller" must {

    "return OK and the correct view for a GET  when primary enrolment check enabled" in {

      val fakeUtr: String = "0987654321"

      val application = applicationBuilder(
        userAnswers = Some(emptyUserAnswers.set(UTRPage, fakeUtr).success.value)
      ).configure(Map("microservice.services.features.primaryEnrolmentCheck.enabled" -> true)).build()

      val request = FakeRequest(GET, routes.AgentNotAuthorisedController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OldAgentNotAuthorisedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeUtr)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET  when primary enrolment check not enabled" in {

      val fakeUtr: String = "0987654321"

      val application = applicationBuilder(
        userAnswers = Some(emptyUserAnswers.set(UTRPage, fakeUtr).success.value)
      ).configure(Map("microservice.services.features.primaryEnrolmentCheck.enabled" -> false)).build()

      val request = FakeRequest(GET, routes.AgentNotAuthorisedController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentNotAuthorisedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeUtr)(request, messages).toString

      application.stop()
    }

    "redirect to UTR controller if no UTR in user answers" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.AgentNotAuthorisedController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.UTRController.onPageLoad().url

      application.stop()
    }
  }

}
