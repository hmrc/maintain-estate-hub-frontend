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

package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import models.FakeUser
import models.requests.AgentUser
import org.mockito.Matchers.any
import org.mockito.Mockito._
import play.api.Application
import play.api.mvc.Results.Redirect
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.Helpers._
import services.{EstateAuthenticationService, FakeAllowedEstateAuthenticationService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}

import scala.concurrent.Future

class IdentifierActionSpec extends SpecBase {

  type RetrievalType = Option[String] ~ Option[AffinityGroup] ~ Enrolments

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  val bodyParsers: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]

  private val noEnrollment = Enrolments(Set())
  private val agentEnrolment = Enrolments(Set(Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "SomeVal")), "Activated", None)))

  class Harness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }

  class ThrowingHarness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => throw new IllegalStateException("Thrown by test") }
  }

  private def authRetrievals(affinityGroup: AffinityGroup, enrolment: Enrolments): Future[Some[String] ~ Some[AffinityGroup] ~ Enrolments] =
    Future.successful(new ~(new ~(Some("id"), Some(affinityGroup)), enrolment))

  private def actionToTest(application: Application,
                           authService: EstateAuthenticationService) = {
    new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers, authService)
  }

  "invoking an AuthenticatedIdentifier" when {

    "an Agent user hasn't enrolled an Agent Services Account" must {

      "redirect the user to the create agent services page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Agent, noEnrollment))

        val mockAuthService = mock[EstateAuthenticationService]
        when (mockAuthService.authenticateAgent()(any())).thenReturn(Future.successful(Left(Redirect("test-redirect-url"))))

        val action = actionToTest(application, mockAuthService)

        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("test-redirect-url")
        application.stop()
      }
    }

    "Agent user has correct enrolled in Agent Services Account" must {
      "allow user to continue" in {

        val application = applicationBuilderForUser(userAnswers = None, FakeUser.agent(agentEnrolment)).build()

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Agent, agentEnrolment))

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe OK
        application.stop()
      }
    }

    "Org user with no enrolments" must {
      "allow user to continue" in {

        val application = applicationBuilder(userAnswers = None).build()

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Organisation, agentEnrolment))

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe OK
        application.stop()
      }
    }

    "Individual user" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Individual, noEnrollment))

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)

        application.stop()
      }
    }

    "the user hasn't logged in" must {
      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed MissingBearerToken())

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
        application.stop()
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed BearerTokenExpired())

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
        application.stop()
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed InsufficientEnrolments())

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed InsufficientConfidenceLevel())

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedAuthProvider())

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedAffinityGroup())

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedCredentialRole())

        val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

        val action = actionToTest(application, fakeAuthService)
        val controller = new Harness(action)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }
  }

  "the invoked block throws an exception" must {
    "propagate the exception" in {

      val application = applicationBuilder(userAnswers = None).build()

      when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
        .thenReturn(authRetrievals(AffinityGroup.Organisation, agentEnrolment))

      val fakeAuthService = injector.instanceOf[FakeAllowedEstateAuthenticationService]

      val action = actionToTest(application, fakeAuthService)
      val controller = new ThrowingHarness(action)
      val result = controller.onPageLoad()(fakeRequest)

      assertThrows[IllegalStateException](status(result))
      application.stop()
    }
  }
}

