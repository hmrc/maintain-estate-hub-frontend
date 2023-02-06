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

package views

import play.api.Application
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.EstateNotClaimedView

class EstateNotClaimedViewSpec extends ViewBehaviours {

  private val fakeUtr: String = "1234567890"

  private val application: Application = applicationBuilder().build()

  private val view: EstateNotClaimedView = application.injector.instanceOf[EstateNotClaimedView]

  private val applyView: HtmlFormat.Appendable = view.apply(utr = fakeUtr)(fakeRequest, messages)

  "Estate not claimed view" when {

    behave like pageWithSubHeading(applyView, fakeUtr)

    behave like expectedHref(applyView, "register-for-gg-account", "http://localhost:9949/auth-login-stub/gg-sign-in?continue=http://localhost:8822/register-an-estate")

    behave like normalPageTitleWithCaption(applyView,
      "estateNotClaimed",
      fakeUtr,
      "p1", "b1.heading", "b1.p1", "b1.p2", "b1.p2.link", "b1.p3", "b1.p4", "b2.heading", "b2.p1", "b2.p2", "b2.p3", "b2.p3.link", "b2.p4", "b2.p4.link"
    )
  }
}
