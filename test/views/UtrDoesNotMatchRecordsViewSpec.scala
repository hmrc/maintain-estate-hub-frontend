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

package views

import views.behaviours.ViewBehaviours
import views.html.UtrDoesNotMatchRecordsView

class UtrDoesNotMatchRecordsViewSpec extends ViewBehaviours {

  "Utr Does Not Match Records view" when {

    "agent user" must {

      val application = applicationBuilder().build()

      val view = application.injector.instanceOf[UtrDoesNotMatchRecordsView]

      val applyView = view.apply(isAgent = true)(fakeRequest, messages)

      behave like pageWithBackLink(applyView)

      behave like normalPage(applyView,
        "utrDoesNotMatchRecords",
        "p1", "p2", "p3", "p3.link", "p4", "p4.link", "p5", "p5.link"
      )
    }

    "non-agent user" must {

      val application = applicationBuilder().build()

      val view = application.injector.instanceOf[UtrDoesNotMatchRecordsView]

      val applyView = view.apply(isAgent = false)(fakeRequest, messages)

      behave like pageWithBackLink(applyView)

      behave like normalPage(applyView,
        "utrDoesNotMatchRecords",
        "p1", "p2", "p3", "p3.link", "p4", "p4.link"
      )
    }
  }
}
