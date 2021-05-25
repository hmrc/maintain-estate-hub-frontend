/*
 * Copyright 2021 HM Revenue & Customs
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
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest, OrganisationUser}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}

import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with MockitoSugar with ScalaFutures {

  class Harness(sessionRepository: SessionRepository) extends DataRetrievalActionImpl(sessionRepository) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "set userAnswers to 'None' in the request" in {

        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get("id")) thenReturn Future(None)
        val action = new Harness(sessionRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, AffinityGroup.Organisation, OrganisationUser("id", Enrolments(Set()))))

        whenReady(futureResult) { result =>
          result.userAnswers.isEmpty mustBe true
        }
      }
    }

    "there is data in the cache" must {

      "build a userAnswers object and add it to the request" in {

        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get("id")) thenReturn Future(Some(new UserAnswers("id")))
        val action = new Harness(sessionRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, AffinityGroup.Organisation, OrganisationUser("id", Enrolments(Set()))))

        whenReady(futureResult) { result =>
          result.userAnswers.isDefined mustBe true
        }
      }
    }
  }
}
