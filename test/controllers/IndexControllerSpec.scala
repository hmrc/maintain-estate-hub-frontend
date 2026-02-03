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
import models.FakeUser
import models.requests.AgentUser
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, EnrolmentIdentifier, Enrolments}

class IndexControllerSpec extends SpecBase {

  lazy val onPageLoad: String = routes.IndexController.onPageLoad().url

  val utr: String = "1234567892"

  "Index Controller" must {

    "redirect to UTR controller when user is not enrolled (agent)" in {

      val application = applicationBuilderForUser(
        userAnswers = Some(emptyUserAnswers),
        user = AgentUser("id", Enrolments(Set()), "arn"),
        affinityGroup = AffinityGroup.Agent
      ).build()

      val request = FakeRequest(GET, onPageLoad)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe controllers.routes.UTRController.onPageLoad().url

      application.stop()
    }

    "redirect to status controller when user is a returning user who is enrolled" in {

      val application = applicationBuilderForUser(
        userAnswers = Some(emptyUserAnswers),
        user = FakeUser.organisation(
          Enrolments(
            Set(
              Enrolment(
                key = "HMRC-TERS-ORG",
                identifiers = Seq(EnrolmentIdentifier(key = "SAUTR", value = utr)),
                state = "Activated"
              )
            )
          )
        ),
        affinityGroup = AffinityGroup.Organisation
      ).build()

      val request = FakeRequest(GET, onPageLoad)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe controllers.routes.EstateStatusController.checkStatus().url

      application.stop()
    }

  }

}
