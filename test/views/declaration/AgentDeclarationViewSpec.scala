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

package views.declaration

import forms.declaration.AgentDeclarationFormProvider
import models.declaration.AgentDeclaration
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.declaration.AgentDeclarationView

class AgentDeclarationViewSpec extends QuestionViewBehaviours[AgentDeclaration] {

  val messageKeyPrefix = "declaration"

  val form = new AgentDeclarationFormProvider()()

  "declaration for agents" when {

    "email enabled" must {

      val view = viewFor[AgentDeclarationView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, declarationEmailEnabled = true)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix, "paragraph1", "paragraph2")

      behave like pageWithBackLink(applyView(form))

      behave like pageWithTextFields(
        form,
        applyView,
        messageKeyPrefix,
        Seq(("firstName", None), ("middleName", None), ("lastName", None), ("agencyName", None), ("telephoneNumber", None), ("crn", None), ("email", None))
      )

      "have a warning" in {
        val doc = asDocument(applyView(form))

        doc.text() must include("I confirm that the information my client has given is true and complete to the best of their knowledge. I will make sure it is kept up to date, including any change of address. If I find out that an error has been made or something has changed, I will update the information.")
      }
    }

    "email disabled" must {

      val view = viewFor[AgentDeclarationView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, declarationEmailEnabled = false)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithTextFields(
        form,
        applyView,
        messageKeyPrefix,
        Seq(("firstName", None), ("middleName", None), ("lastName", None), ("agencyName", None), ("telephoneNumber", None), ("crn", None))
      )

      "have a warning" in {
        val doc = asDocument(applyView(form))

        doc.text() must include("I confirm that the information my client has given is true and complete to the best of their knowledge. I will make sure it is kept up to date, including any change of address. If I find out that an error has been made or something has changed, I will update the information.")
      }
    }
  }


}
