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

package views.declaration

import forms.declaration.DeclarationFormProvider
import models.declaration.Declaration
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.declaration.IndividualDeclarationView

class IndividualDeclarationViewSpec extends QuestionViewBehaviours[Declaration] {

  val messageKeyPrefix = "declaration"

  val form = new DeclarationFormProvider()()

  "declaration for individuals" when {

    "email enabled" must {

      val view = viewFor[IndividualDeclarationView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, declarationEmailEnabled = true)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix, "paragraph1", "paragraph2")

      behave like pageWithBackLink(applyView(form))

      behave like pageWithTextFields(
        form,
        applyView,
        messageKeyPrefix,
        "name_firstName", "name_middleName", "name_lastName", "email"
      )

      "have a warning" in {
        val doc = asDocument(applyView(form))

        doc.text() must include("I confirm that the information given is true and complete to the best of my knowledge and belief. I will ensure it is regularly kept up to date as required including changes of address. If I discover that I have made an error or something has changed, I will update this register.")
      }
    }

    "email disabled" must {

      val view = viewFor[IndividualDeclarationView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, declarationEmailEnabled = false)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithTextFields(
        form,
        applyView,
        messageKeyPrefix,
        "name_firstName", "name_middleName", "name_lastName"
      )

      "have a warning" in {
        val doc = asDocument(applyView(form))

        doc.text() must include("I confirm that the information given is true and complete to the best of my knowledge and belief. I will ensure it is regularly kept up to date as required including changes of address. If I discover that I have made an error or something has changed, I will update this register.")
      }
    }
  }


}
