/*
 * Copyright 2020 HM Revenue & Customs
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
import config.FrontendAppConfig
import connectors.EstatesConnector
import forms.WhatIsNextFormProvider
import models.WhatIsNext
import models.WhatIsNext.CloseEstate
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when, times}
import org.scalatestplus.mockito.MockitoSugar
import pages.{UTRPage, WhatIsNextPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import views.html.WhatIsNextView

import scala.concurrent.Future

class WhatIsNextControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new WhatIsNextFormProvider()
  val form: Form[WhatIsNext] = formProvider()

  lazy val onPageLoad: String = routes.WhatIsNextController.onPageLoad().url

  lazy val onSubmit: Call = routes.WhatIsNextController.onSubmit()

  val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  when(fakeConnector.clearTransformations(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

  "WhatIsNext Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(UTRPage, "1234567892").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, onPageLoad)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatIsNextView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(UTRPage, "0987654321").success.value
        .set(WhatIsNextPage, WhatIsNext.MakeChanges).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, onPageLoad)

      val view = application.injector.instanceOf[WhatIsNextView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(WhatIsNext.MakeChanges))(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired if no data" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, onPageLoad)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to add new personal rep journey in maintain personal rep service when user selects 'DeclareNewPersonalRep'" in {

      val utr = "0987654321"

      val userAnswers = emptyUserAnswers
        .set(UTRPage, utr).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), utr = utr)
        .overrides(bind[EstatesConnector].toInstance(fakeConnector))
        .build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", "declare"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe frontendAppConfig.addNewPersonalRepUrl(utr)

      application.stop()
    }

    "redirect to amend existing personal rep journey in maintain personal rep service when user selects 'MakeChanges'" in {

      val utr = "0987654321"

      val userAnswers = emptyUserAnswers
        .set(UTRPage, utr).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), utr = utr)
        .overrides(bind[EstatesConnector].toInstance(fakeConnector))
        .build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", "make-changes"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe frontendAppConfig.amendExistingPersonalRepUrl(utr)

      application.stop()
    }

    "redirect to has administration period ended when user selects 'CloseEstate'" in {

      val utr = "0987654321"

      val userAnswers = emptyUserAnswers
        .set(UTRPage, utr).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[EstatesConnector].toInstance(fakeConnector))
        .build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", "close-estate"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe controllers.closure.routes.HasAdministrationPeriodEndedYesNoController.onPageLoad().url

      application.stop()
    }

    "clear transformations if user changes selection" in {

      reset(fakeConnector)
      when(fakeConnector.clearTransformations(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val utr = "0987654321"

      val userAnswers = emptyUserAnswers
        .set(UTRPage, utr).success.value
        .set(WhatIsNextPage, CloseEstate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[EstatesConnector].toInstance(fakeConnector)
        )
        .build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", WhatIsNext.MakeChanges.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      verify(fakeConnector, times(1)).clearTransformations(any())(any(), any())

      application.stop()
    }

    "clear transformations if no previous selection" in {

      reset(fakeConnector)
      when(fakeConnector.clearTransformations(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val utr = "0987654321"

      val userAnswers = emptyUserAnswers
        .set(UTRPage, utr).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[EstatesConnector].toInstance(fakeConnector)
        )
        .build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", WhatIsNext.MakeChanges.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      verify(fakeConnector, times(1)).clearTransformations(any())(any(), any())

      application.stop()
    }

    "not clear transformations if user makes same selection" in {

      reset(fakeConnector)
      when(fakeConnector.clearTransformations(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val utr = "0987654321"

      val userAnswers = emptyUserAnswers
        .set(UTRPage, utr).success.value
        .set(WhatIsNextPage, CloseEstate).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[EstatesConnector].toInstance(fakeConnector)
        )
        .build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", WhatIsNext.CloseEstate.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      verify(fakeConnector, times(0)).clearTransformations(any())(any(), any())

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(UTRPage, "1234567892").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, onSubmit.url)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[WhatIsNextView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }


  }
}
