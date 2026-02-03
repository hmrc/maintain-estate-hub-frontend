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

package controllers.closure

import base.SpecBase
import forms.YesNoFormProvider
import models.UserAnswers
import models.requests.{AgentUser, OrganisationUser}
import pages.UTRPage
import pages.closure.ChangePersonalRepDetailsYesNoPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import views.html.closure.ChangePersonalRepDetailsYesNoView

class ChangePersonalRepDetailsYesNoControllerSpec extends SpecBase {

  private val formProvider            = new YesNoFormProvider()
  private val form: Form[Boolean]     = formProvider.withPrefix("closure.changePersonalRepDetailsYesNo")
  private lazy val yesNoRoute: String = routes.ChangePersonalRepDetailsYesNoController.onPageLoad().url
  private val utr: String             = "utr"
  private val validAnswer: Boolean    = true

  override def emptyUserAnswers: UserAnswers = super.emptyUserAnswers.set(UTRPage, utr).success.value

  "ChangePersonalRepDetailsYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, yesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ChangePersonalRepDetailsYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(ChangePersonalRepDetailsYesNoPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, yesNoRoute)

      val view = application.injector.instanceOf[ChangePersonalRepDetailsYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(request, messages).toString

      application.stop()
    }

    "redirect to the next page when YES is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, yesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.amendExistingPersonalRepUrl(utr)

      application.stop()
    }

    "redirect to the next page when NO is submitted" when {

      "agent user" in {

        val application = applicationBuilderForUser(
          userAnswers = Some(emptyUserAnswers),
          user = AgentUser("id", Enrolments(Set()), "arn"),
          affinityGroup = AffinityGroup.Agent
        ).build()

        val request = FakeRequest(POST, yesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.declaration.routes.AgencyRegisteredAddressUkYesNoController
          .onPageLoad()
          .url

        application.stop()
      }

      "non-agent user" in {

        val application = applicationBuilderForUser(
          userAnswers = Some(emptyUserAnswers),
          user = OrganisationUser("id", Enrolments(Set())),
          affinityGroup = AffinityGroup.Organisation
        ).build()

        val request = FakeRequest(POST, yesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.declaration.routes.IndividualDeclarationController
          .onPageLoad()
          .url

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, yesNoRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ChangePersonalRepDetailsYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, yesNoRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

}
