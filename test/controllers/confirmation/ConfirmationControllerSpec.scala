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

package controllers.confirmation

import java.time.{LocalDate, LocalDateTime}

import base.{FakeData, SpecBase}
import connectors.EstatesConnector
import models.http.Processed
import models.requests.AgentUser
import models.{PersonalRepresentativeType, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.{SubmissionDatePage, TVNPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import views.html.confirmation.ConfirmationView

import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBase {

  private val fakeTvn = "XCTVN0000004912"

  private val playbackAnswers: UserAnswers = emptyUserAnswers
    .set(TVNPage, fakeTvn).success.value
    .set(SubmissionDatePage, LocalDateTime.of(2010, 10, 5, 3, 10)).success.value

  "Confirmation Controller" must {

    "return OK and the correct view for a onPageLoad when TVN is available" in {

      lazy val data = FakeData.fakeGetEstateWithPersonalRep(
        PersonalRepresentativeType(
          estatePerRepInd = Some(FakeData.personalRepresentativeIndividualNino),
          estatePerRepOrg = None
        ),
        correspondenceAddress = FakeData.correspondenceAddressUk
      )

      when(fakeConnector.getTransformedEstate(any())(any(), any()))
        .thenReturn(Future.successful(Processed(data, "formBundleNo")))

      val application = applicationBuilder(userAnswers = Some(playbackAnswers))
        .overrides(
          bind[EstatesConnector].to(fakeConnector)
        )
        .build()

      val request = FakeRequest(GET, controllers.confirmation.routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view("Adam Conder", fakeTvn, isAgent = false, isClosing = false)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for an agent user closing the estate" in {

      lazy val data = FakeData.fakeGetEstateWithPersonalRep(
        PersonalRepresentativeType(
          estatePerRepInd = Some(FakeData.personalRepresentativeIndividualNino),
          estatePerRepOrg = None
        ),
        correspondenceAddress = FakeData.correspondenceAddressUk,
        trustEndDate = Some(LocalDate.parse("2020-08-06"))
      )

      when(fakeConnector.getTransformedEstate(any())(any(), any()))
        .thenReturn(Future.successful(Processed(data, "formBundleNo")))

      val application = applicationBuilderForUser(
        userAnswers = Some(playbackAnswers),
        user = AgentUser("id", Enrolments(Set()), "arn"),
        affinityGroup = AffinityGroup.Agent
      ).overrides(
          bind[EstatesConnector].to(fakeConnector)
      ).build()

      val request = FakeRequest(GET, controllers.confirmation.routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view("Adam Conder", fakeTvn, isAgent = true, isClosing = true)(request, messages).toString

      application.stop()
    }
  }

}
