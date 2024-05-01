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

package views.confirmation

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.confirmation.ConfirmationView

class ConfirmationViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "confirmationPage"
  val tvn = "XC TVN 000 000 4912"

  private def confirmationPage(view: HtmlFormat.Appendable) : Unit = {

    "assert content" in {
      val doc = asDocument(view)

      assertContainsText(doc, s"Declaration received")
      assertContainsText(doc, s"Your reference is:")
      assertContainsText(doc, s"$tvn")

      assertContainsText(doc, "What happens next")

      assertContainsTextForId(doc, "print-declared", "Print or save a declared copy of the estate’s declaration")

      assertContainsText(doc, "Keep a note of your reference in case you need to contact HMRC. If there is a problem with the declaration, we will contact Adam.")
    }

  }

  private def confirmationPageForAgent(view: HtmlFormat.Appendable) : Unit = {
    "display return to agent overview link" in {

      val doc = asDocument(view)
      val agentOverviewLink = doc.getElementById("agent-overview")
      assertAttributeValueForElement(agentOverviewLink, "href", frontendAppConfig.agentOverviewUrl)
      assertContainsTextForId(doc, "agent-overview", "return to register and maintain an estate for a client")
    }

  }

  private def closingConfirmationPage(view: HtmlFormat.Appendable) : Unit = {
    "display paragraphs relevant to closing the estate" in {

      val doc = asDocument(view)
      assertContainsText(doc, "Your request to close this estate will be processed and access to its online register will be removed.")
      assertContainsText(doc, "If this has been done in error, you can")

      val estatesHelplineLink = doc.getElementById("helpline")
      assertAttributeValueForElement(estatesHelplineLink, "href", frontendAppConfig.estatesHelplineUrl)
      assertContainsTextForId(doc, "helpline", "contact the deceased estate helpline to reopen the records (opens in a new tab)")
    }
  }

  "Confirmation view for an agent" must {
    val view = viewFor[ConfirmationView](Some(emptyUserAnswers))

    val applyView = view.apply(
      "Adam",
      tvn = tvn,
      isAgent = true,
      isClosing = false
    )(fakeRequest, messages)

    behave like confirmationPage(applyView)

    behave like confirmationPageForAgent(applyView)
  }

  "Confirmation view for an organisation" must {
    val view = viewFor[ConfirmationView](Some(emptyUserAnswers))

    val applyView = view.apply(
      "Adam",
      tvn = tvn,
      isAgent = true,
      isClosing = false
    )(fakeRequest, messages)

    behave like confirmationPage(applyView)
  }

  "Confirmation view when closing" must {
    val view = viewFor[ConfirmationView](Some(emptyUserAnswers))

    val applyView = view.apply(
      "Adam",
      tvn = tvn,
      isAgent = false,
      isClosing = true
    )(fakeRequest, messages)

    behave like closingConfirmationPage(applyView)
  }

}
