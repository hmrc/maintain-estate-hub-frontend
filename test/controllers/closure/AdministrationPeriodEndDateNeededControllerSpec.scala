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

package controllers.closure

import base.SpecBase
import models.UserAnswers
import models.WhatIsNext.CloseEstate
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify
import org.scalatestplus.mockito.MockitoSugar
import pages.closure.HasAdministrationPeriodEndedYesNoPage
import pages.{UTRPage, WhatIsNextPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.closure.AdministrationPeriodEndDateNeededView

class AdministrationPeriodEndDateNeededControllerSpec extends SpecBase with MockitoSugar {

  val utr: String = "1234567890"
  lazy val administrationPeriodEndDateNeededRoute: String = routes.AdministrationPeriodEndDateNeededController.onPageLoad().url

  override val emptyUserAnswers: UserAnswers = super.emptyUserAnswers.set(UTRPage, utr).success.value

  "AdministrationPeriodEndDateNeeded Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, administrationPeriodEndDateNeededRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AdministrationPeriodEndDateNeededView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }

    "do cleanup and redirect to WhatIsNextController for a POST" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(WhatIsNextPage, CloseEstate).success.value
        .set(HasAdministrationPeriodEndedYesNoPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(fakeRepository))
        .build()

      val request = FakeRequest(POST, administrationPeriodEndDateNeededRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.WhatIsNextController.onPageLoad().url

      val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(fakeRepository).set(uaCaptor.capture)
      uaCaptor.getValue.get(WhatIsNextPage) mustNot be(defined)
      uaCaptor.getValue.get(HasAdministrationPeriodEndedYesNoPage) mustNot be(defined)

      application.stop()
    }
  }
}
