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
import models.http._
import models.requests.{AgentUser, OrganisationUser}
import models.{EstateLock, GetEstate, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.UTRPage
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsPath, JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{EstateAuthenticationService, FakeAllowedEstateAuthenticationService, FakeDeniedEstateAuthenticationService, FakeFailingEstateAuthenticationService}
import uk.gov.hmrc.auth.core.Enrolments
import views.html._

import scala.concurrent.Future
import scala.io.Source

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

  trait AgentSetup extends LocalSetup {
    override lazy val application: Application =
      applicationBuilderForUser(
        userAnswers = Some(userAnswers),
        user = AgentUser("id", Enrolments(Set()), "arn")
      ).overrides(
        bind[EstatesConnector].to(fakeConnector),
        bind[EstatesStoreConnector].to(fakeEstateStoreConnector)
      ).build()
  }

  trait NonAgentSetup extends LocalSetup {
    override lazy val application: Application =
      applicationBuilderForUser(
        userAnswers = Some(userAnswers),
        user = OrganisationUser("id", Enrolments(Set()))
      ).overrides(
        bind[EstatesConnector].to(fakeConnector),
        bind[EstatesStoreConnector].to(fakeEstateStoreConnector)
      ).build()
  }

  "Estate Status Controller" must {

    "return OK and the correct view for GET" when {

      "../status/closed" when {

        "agent user" in new AgentSetup {

          override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.closed().url)

          val view: ClosedView = application.injector.instanceOf[ClosedView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(fakeUtr, isAgent = true)(fakeRequest, messages).toString

          application.stop()
        }

        "non-agent user" in new NonAgentSetup {

          override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.closed().url)

          val view: ClosedView = application.injector.instanceOf[ClosedView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(fakeUtr, isAgent = false)(fakeRequest, messages).toString

          application.stop()
        }
      }

      "../status/processing" when {

        "agent user" in new AgentSetup {

          override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.inProcessing().url)

          val view: InProcessingView = application.injector.instanceOf[InProcessingView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(fakeUtr, isAgent = true)(fakeRequest, messages).toString

          application.stop()
        }

        "non-agent user" in new NonAgentSetup {

          override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.inProcessing().url)

          val view: InProcessingView = application.injector.instanceOf[InProcessingView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(fakeUtr, isAgent = false)(fakeRequest, messages).toString

          application.stop()
        }
      }

      "../status/locked" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.locked().url)

        val view: LockedView = application.injector.instanceOf[LockedView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeUtr)(fakeRequest, messages).toString

        application.stop()
      }

      "../status/not-found" when {

        "agent user" in new AgentSetup {

          override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.utrDoesNotMatchRecords().url)

          val view: UtrDoesNotMatchRecordsView = application.injector.instanceOf[UtrDoesNotMatchRecordsView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(isAgent = true)(fakeRequest, messages).toString

          application.stop()
        }

        "non-agent user" in new NonAgentSetup {

          override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.utrDoesNotMatchRecords().url)

          val view: UtrDoesNotMatchRecordsView = application.injector.instanceOf[UtrDoesNotMatchRecordsView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(isAgent = false)(fakeRequest, messages).toString

          application.stop()
        }
      }

      "../status/sorry-there-has-been-a-problem" when {

        "agent user" in new AgentSetup {

          override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.problemWithService().url)

          val view: ProblemWithServiceView = application.injector.instanceOf[ProblemWithServiceView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(isAgent = true)(fakeRequest, messages).toString

          application.stop()
        }

        "non-agent user" in new NonAgentSetup {

          override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.problemWithService().url)

          val view: ProblemWithServiceView = application.injector.instanceOf[ProblemWithServiceView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(isAgent = false)(fakeRequest, messages).toString

          application.stop()
        }
      }

      "../status/already-claimed" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.accountNotLinked().url)

        val view: AccountNotLinkedView = application.injector.instanceOf[AccountNotLinkedView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeUtr)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "redirect to the correct route for GET ../status/onPageLoad" when {

      val utr: String = "1234567890"

      "a Closed status is received from the estates connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.checkStatus().url)

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(Closed))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/closed"

        application.stop()
      }

      "a Processing status is received from the estates connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.checkStatus().url)

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(Processing))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/processing"

        application.stop()
      }

      "a Locked status is received from the estate store connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.checkStatus().url)

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = true))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/locked"

        application.stop()
      }

      "a NotFound status is received from the estates connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.checkStatus().url)

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(UtrNotFound))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/not-found"

        application.stop()
      }

      "a ServiceUnavailable status is received from the estates connector" in new LocalSetup {

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.checkStatus().url)

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(EstatesServiceUnavailable))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-an-estate/status/sorry-there-has-been-a-problem"

        application.stop()
      }

      "a Processed status is received from the estates connector" in new LocalSetup {

        val payload: String = Source.fromFile(getClass.getResource("/display-estate.json").getPath).mkString
        val json: JsValue = Json.parse(payload)

        val estate: GetEstate = json.transform(
          (JsPath \ 'getEstate).json.pick
        ).get.as[GetEstate]

        override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.checkStatus().url)

        override lazy val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
          bind[EstatesConnector].to(fakeConnector),
          bind[EstatesStoreConnector].to(fakeEstateStoreConnector)
        ).build()

        when(fakeConnector.getEstate(any[String])(any(), any())).thenReturn(Future.successful(Processed(estate, "1")))

        when(fakeEstateStoreConnector.get(any[String])(any(), any()))
          .thenReturn(Future.successful(Some(EstateLock(utr, managedByAgent = false, estateLocked = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.print.routes.LastDeclaredAnswersController.onPageLoad().url

        application.stop()
      }
    }

    "redirect to UTR page when no UTR found" in new LocalSetup {

      override val builder: GuiceApplicationBuilder = applicationBuilder(userAnswers = Some(emptyUserAnswers))

      override def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.EstateStatusController.checkStatus().url)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.UTRController.onPageLoad.url

      application.stop()
    }
  }
}
