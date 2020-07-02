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
import connectors.{EstatesConnector, EstatesStoreConnector}
import models.{EstateLock, NormalMode, UserAnswers}
import models.http._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.UTRPage
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{ClosedView, InProcessingView, LockedView, ProblemWithServiceView, UtrDoesNotMatchRecordsView}

import scala.concurrent.Future

class EstateStatusControllerSpec extends SpecBase with BeforeAndAfterEach {

  trait BaseSetup {

    val builder: GuiceApplicationBuilder

    val fakeUtr = "1234567890"

    val userAnswers: UserAnswers = emptyUserAnswers.set(UTRPage, fakeUtr).success.value

    def request: FakeRequest[AnyContentAsEmpty.type]

    def result: Future[Result] = route(application, request).value

    lazy val application: Application = builder.overrides(
      bind[EstatesConnector].to(fakeConnector),
      bind[EstatesStoreConnector].to(fakeEstateStoreConnector)
    ).build()
  }

  trait LocalSetup extends BaseSetup {
    override val builder: GuiceApplicationBuilder = applicationBuilder(userAnswers = Some(userAnswers))
  }

  "Estate Status Controller" must {

    "return OK and the correct view for GET ../status/closed" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.closed().url)

      val view: ClosedView = application.injector.instanceOf[ClosedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeUtr)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for GET ../status/locked" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.locked().url)

      val view: LockedView = application.injector.instanceOf[LockedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeUtr)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for GET ../status/in-processing" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.inProcessing().url)

      val view: InProcessingView = application.injector.instanceOf[InProcessingView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeUtr)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for GET ../status/utr-does-not-match-records" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.utrDoesNotMatchRecords().url)

      val view: UtrDoesNotMatchRecordsView = application.injector.instanceOf[UtrDoesNotMatchRecordsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for GET ../status/problem-with-service" in new LocalSetup {

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.problemWithService().url)

      val view: ProblemWithServiceView = application.injector.instanceOf[ProblemWithServiceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the correct route for GET ../status/onPageLoad" when {

      val utr = "1234567890"

      "a Closed status is received from the estate connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.onPageLoad().url)

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(Closed))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/closed"

        application.stop()
      }

      "a Locked status is received from the estate store connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.onPageLoad().url)

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = true))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/locked"

        application.stop()
      }
      "a Processing status is received from the estate connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.onPageLoad().url)

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(Processing))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/in-processing"

        application.stop()
      }

      "a NotFound status is received from the estate connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.onPageLoad().url)

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(UtrNotFound))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/utr-does-not-match-records"

        application.stop()
      }

      "a ServiceUnavailable status is received from the estate connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.onPageLoad().url)

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(EstatesServiceUnavailable))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/problem-with-service"

        application.stop()
      }
    }

    "redirect to UTR page when no UTR found" in new LocalSetup {

      override val builder: GuiceApplicationBuilder = applicationBuilder(userAnswers = Some(emptyUserAnswers))

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.onPageLoad().url)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.UTRController.onPageLoad(NormalMode).url

      application.stop()
    }
  }
}
