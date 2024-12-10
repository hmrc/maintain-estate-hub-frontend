/*
 * Copyright 2024 HM Revenue & Customs
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
import views.html.UnauthorisedView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class UnauthorisedViewSpec extends ViewBehaviours {

  "Unauthorised view" must {

    val application = applicationBuilder().build()

    val view = application.injector.instanceOf[UnauthorisedView]

    val applyView = view.apply()(fakeRequest, messages)

    val doc: Document = Jsoup.parse(applyView.toString())

    "contain the expected heading" in {
      assert(doc.select("h1").text == messages("unauthorised.heading"))
    }

    "contain the correct paragraphs" in {
      val paragraphs = doc.select("p").eachText()
      assert(paragraphs.contains(messages("unauthorised.p1")))
      assert(paragraphs.contains(messages("unauthorised.p2")))
      assert(paragraphs.contains(messages("unauthorised.p3")))
    }

    "render the bullet list correctly" in {
      val bulletPoints = doc.select("ul li").eachText()
      assert(bulletPoints.contains(messages("unauthorised.bullet1")))
      assert(bulletPoints.contains(messages("unauthorised.bullet2")))
    }

    behave like normalPage(applyView, "unauthorised")
  }
}
