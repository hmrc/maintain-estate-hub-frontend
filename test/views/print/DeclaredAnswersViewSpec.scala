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

package views.print

import play.api.Application
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.print.DeclaredAnswersView

class DeclaredAnswersViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder().build()

  val view: DeclaredAnswersView = application.injector.instanceOf[DeclaredAnswersView]

  "Declared view" when {

    val prefix: String = "declared"

    "an individual" must {

      val applyView: HtmlFormat.Appendable =
        view.apply("TVN", "27 August 2020", None, Nil, Nil, prefix)(fakeRequest, messages)

      behave like normalPage(
        applyView,
        prefix,
        "informationFirstRegistered"
      )

      behave like pageWithBackLink(applyView)

      behave like pageWithAPrintButton(applyView)

      "have content" in {
        val doc = asDocument(applyView)

        doc.text() must include("Declaration reference number is: TVN")
        doc.text() must include("The estate’s declaration was sent on 27 August 2020")
      }
    }

    "an agent" must {

      val applyView: HtmlFormat.Appendable =
        view.apply("TVN", "27 August 2020", Some("crn"), Nil, Nil, prefix)(fakeRequest, messages)

      behave like normalPage(
        applyView,
        prefix,
        "informationFirstRegistered"
      )

      behave like pageWithBackLink(applyView)

      behave like pageWithAPrintButton(applyView)

      "have content" in {
        val doc = asDocument(applyView)

        doc.text() must include("Declaration reference number is: TVN")
        doc.text() must include("The estate’s declaration was sent on 27 August 2020")
        doc.text() must include("Client reference number: crn")
      }
    }
  }

  "Final declared view" must {

    val prefix: String = "declared.final"

    val applyView: HtmlFormat.Appendable =
      view.apply("TVN", "27 August 2020", None, Nil, Nil, prefix)(fakeRequest, messages)

    "have content" in {
      val doc = asDocument(applyView)

      doc.text() must include("Final declared copy of the estate’s registration")
    }
  }
}
