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

import java.time.LocalDateTime

import base.SpecBase
import models.requests.{DataRequestWithUTR, OrganisationUser, TvnRequest}
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.{SubmissionDatePage, TVNPage}
import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.Enrolments

import scala.concurrent.Future

class RequireTvnActionSpec extends SpecBase with MockitoSugar with ScalaFutures with EitherValues {

  class Harness() extends RequireTvnActionImpl() {
    def callRefine[A](request: DataRequestWithUTR[A]): Future[Either[Result, TvnRequest[A]]] = refine(request)
  }

  "Require Tvn Action" when {

    "there is no tvn in the cache" must {

      "return problem with the service" in {

        val action = new Harness()

        val user = OrganisationUser("id", Enrolments(Set()))
        val request = DataRequestWithUTR(fakeRequest, emptyUserAnswers, user, "utr")

        val futureResult = action.callRefine(request)

        val redirect = Future.successful(futureResult.futureValue.left.value)

        redirectLocation(redirect).value mustBe controllers.routes.EstateStatusController.problemWithService().url
      }
    }

    "there is a tvn and submission date in the cache" must {

      "refine the request" in {

        val action = new Harness()

        val userAnswers = emptyUserAnswers
          .set(TVNPage, "tvn").success.value
          .set(SubmissionDatePage, LocalDateTime.of(2010, 10, 5, 3, 10)).success.value

        val user = OrganisationUser("id", Enrolments(Set()))
        val request = DataRequestWithUTR(fakeRequest, userAnswers, user, "utr")

        val futureResult = action.callRefine(request)

        futureResult.futureValue.right.value mustBe a[TvnRequest[_]]
      }
    }
  }
}
