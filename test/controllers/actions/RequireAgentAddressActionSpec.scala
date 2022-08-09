/*
 * Copyright 2022 HM Revenue & Customs
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
import models.declaration.UKAddress
import models.requests._
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.declaration.AgencyRegisteredAddressPage
import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.Enrolments

import scala.concurrent.Future

class RequireAgentAddressActionSpec extends SpecBase with MockitoSugar with ScalaFutures with EitherValues {

  class Harness() extends RequireAgentAddressActionImpl() {
    def callRefine[A](request: DataRequestWithUTR[A]): Future[Either[Result, AgentRequestWithAddress[A]]] =
      refine(request)
  }

  "Require Agent Address Action" when {

    "there is no address in the cache" must {

      "return problem with the service" in {

        val action = new Harness()

        val user = AgentUser("id", Enrolments(Set()), "arn")
        val request = DataRequestWithUTR(fakeRequest, emptyUserAnswers, user, "utr")

        val futureResult = action.callRefine(request)

        val redirect = Future.successful(futureResult.futureValue.left.value)

        redirectLocation(redirect).value mustBe controllers.routes.EstateStatusController.problemWithService().url
      }
    }

    "user is an organisation credential" must {

      "return unauthorised" in {

        val action = new Harness()

        val userAnswers = emptyUserAnswers

        val user = OrganisationUser("id", Enrolments(Set()))
        val request = DataRequestWithUTR(fakeRequest, userAnswers, user, "utr")

        val futureResult = action.callRefine(request)

        val redirect = Future.successful(futureResult.futureValue.left.value)

        redirectLocation(redirect).value mustBe controllers.routes.UnauthorisedController.onPageLoad.url
      }
    }

    "there is an address in the cache" must {

      "refine the request" in {

        val action = new Harness()

        val userAnswers = emptyUserAnswers
          .set(AgencyRegisteredAddressPage, UKAddress("line1", "line2", postcode = "NE981ZZ")).success.value

        val user = AgentUser("id", Enrolments(Set()), "arn")
        val request = DataRequestWithUTR(fakeRequest, userAnswers, user, "utr")

        val futureResult = action.callRefine(request)

        futureResult.futureValue.right.value mustBe a[AgentRequestWithAddress[_]]
      }
    }
  }
}
