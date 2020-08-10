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

import play.api.Application
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.MaintainThisEstateView

class MaintainThisEstateViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder().build()

  val view: MaintainThisEstateView = application.injector.instanceOf[MaintainThisEstateView]

  val fakeUtr: String = "1234567890"
  val continueUrl: String = "redirect"

  val applyView: HtmlFormat.Appendable = view.apply(fakeUtr, continueUrl)(fakeRequest, messages)

  "Maintain This Estate view" must {

    behave like pageWithBackLink(applyView)

    behave like pageWithSubHeading(applyView, fakeUtr)

    behave like normalPageTitleWithCaption(
      applyView,
      "maintainThisEstate",
      fakeUtr,
      "p1.a", "p1.b", "p2", "p3", "p4", "p4.a"
    )

    behave like pageWithContinueButton(applyView)
  }
}
