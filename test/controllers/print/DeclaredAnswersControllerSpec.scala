/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.print

import java.time.{LocalDate, LocalDateTime}

import base.{FakeData, SpecBase}
import connectors.EstatesConnector
import models.PersonalRepresentativeType
import models.http.{Processed, SorryThereHasBeenAProblem}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.{SubmissionDatePage, TVNPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import printers.PrintHelper
import views.html.print.DeclaredAnswersView

import scala.concurrent.Future

class DeclaredAnswersControllerSpec extends SpecBase {

  lazy val mockEstatesConnector: EstatesConnector = mock[EstatesConnector]
  lazy val mockPrintHelper: PrintHelper = mock[PrintHelper]

  when(mockPrintHelper.personalRepresentative(any())(any())).thenReturn(Nil)
  when(mockPrintHelper.estateName(any())(any())).thenReturn(Nil)
  when(mockPrintHelper.administrationPeriod(any())(any())).thenReturn(Nil)
  when(mockPrintHelper.deceasedPerson(any())(any())).thenReturn(Nil)

  "DeclaredController" when {

    val userAnswers = emptyUserAnswers
      .set(TVNPage, "tvn").success.value
      .set(SubmissionDatePage, LocalDateTime.of(2010, 10, 5, 3, 10)).success.value

    "not closing" must {

      "return OK and the correct view for a GET" in {

        lazy val data = FakeData.fakeGetEstateWithPersonalRep(
          PersonalRepresentativeType(
            estatePerRepInd = Some(FakeData.personalRepresentativeIndividualNino),
            estatePerRepOrg = None
          ),
          correspondenceAddress = FakeData.correspondenceAddressUk
        )

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[EstatesConnector].toInstance(mockEstatesConnector),
            bind[PrintHelper].toInstance(mockPrintHelper)
          ).build()

        when(mockEstatesConnector.getTransformedEstate(any())(any(), any())).thenReturn(Future.successful(Processed(data, "formBundleNo")))

        val request = FakeRequest(GET, controllers.print.routes.DeclaredAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DeclaredAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view("tvn", "5 October 2010", None, Nil, Nil, "declared")(request, messages).toString

        application.stop()
      }
    }

    "closing" must {

      "return OK and the correct view for a GET" in {

        lazy val data = FakeData.fakeGetEstateWithPersonalRep(
          PersonalRepresentativeType(
            estatePerRepInd = Some(FakeData.personalRepresentativeIndividualNino),
            estatePerRepOrg = None
          ),
          correspondenceAddress = FakeData.correspondenceAddressUk,
          trustEndDate = Some(LocalDate.parse("2020-08-06"))
        )

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[EstatesConnector].toInstance(mockEstatesConnector),
            bind[PrintHelper].toInstance(mockPrintHelper)
          ).build()

        when(mockEstatesConnector.getTransformedEstate(any())(any(), any())).thenReturn(Future.successful(Processed(data, "formBundleNo")))

        val request = FakeRequest(GET, controllers.print.routes.DeclaredAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DeclaredAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view("tvn", "5 October 2010", None, Nil, Nil, "declared.final")(request, messages).toString

        application.stop()
      }
    }

    "unable to retrieve estate" must {

      "redirect to problem with the service" in {

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[EstatesConnector].toInstance(mockEstatesConnector),
            bind[PrintHelper].toInstance(mockPrintHelper)
          ).build()

        when(mockEstatesConnector.getTransformedEstate(any())(any(), any())).thenReturn(Future.successful(SorryThereHasBeenAProblem))

        val request = FakeRequest(GET, controllers.print.routes.DeclaredAnswersController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe controllers.routes.EstateStatusController.problemWithService().url

        application.stop()
      }
    }
  }
}
