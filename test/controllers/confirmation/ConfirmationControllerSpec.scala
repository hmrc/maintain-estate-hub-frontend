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

package controllers.confirmation

import java.time.LocalDateTime

import base.{FakeData, SpecBase}
import connectors.EstatesConnector
import models.PersonalRepresentativeType
import models.http.Processed
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.{SubmissionDatePage, TVNPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.confirmation.ConfirmationView
import play.api.inject.bind

import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBase {

  "Confirmation Controller" must {

    "return OK and the correct view for a onPageLoad when TVN is available" in {

      val fakeTvn = "XCTVN0000004912"

      val playbackAnswers = emptyUserAnswers
        .set(TVNPage, fakeTvn).success.value
        .set(SubmissionDatePage, LocalDateTime.of(2010, 10, 5, 3, 10)).success.value

      val mockConnector = mock[EstatesConnector]

      lazy val data = FakeData.fakeGetEstateWithPersonalRep(
        PersonalRepresentativeType(
          estatePerRepInd = Some(FakeData.personalRepresentativeIndividualNino),
          estatePerRepOrg = None
        ),
        correspondenceAddress = FakeData.correspondenceAddressUk
      )

      when(mockConnector.getTransformedEstate(any())(any(), any()))
        .thenReturn(Future.successful(Processed(data, "formBundleNo")))

      val application = applicationBuilder(userAnswers = Some(playbackAnswers))
        .overrides(
          bind[EstatesConnector].to(mockConnector)
        )
        .build()

      val request = FakeRequest(GET, controllers.confirmation.routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view("Adam Conder", fakeTvn, isAgent = false, "#")(fakeRequest, messages).toString

      application.stop()
    }
  }

}
