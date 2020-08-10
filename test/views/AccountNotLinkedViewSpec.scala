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

package views

import views.behaviours.ViewBehaviours
import views.html.{AccountNotLinkedView, InProcessingView}

class AccountNotLinkedViewSpec extends ViewBehaviours {

  private val fakeUtr: String = "1234567890"

  "Account Not Linked view" must {

    val application = applicationBuilder().build()

    val view = application.injector.instanceOf[AccountNotLinkedView]

    val applyView = view.apply(fakeUtr)(fakeRequest, messages)

    behave like pageWithBackLink(applyView)

    behave like pageWithSubHeading(applyView, s"This estate’s UTR: $fakeUtr")

    behave like normalPageTitleWithCaption(applyView,
      "accountNotLinked",
      fakeUtr,
      "p1", "p2", "p2.link"
    )

    behave like pageWithSignOutButton(applyView)

  }
}
