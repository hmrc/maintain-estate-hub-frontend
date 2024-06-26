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

package views.closure

import java.time.LocalDate

import forms.DateFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.closure.AdministrationPeriodEndDateView

class AdministrationPeriodEndDateViewSpec extends QuestionViewBehaviours[LocalDate] {

  private val messageKeyPrefix = "closure.administrationPeriodEndDate"
  private val trustStartDate = LocalDate.parse("2019-02-03")
  override val form: Form[LocalDate] = new DateFormProvider().withConfig(messageKeyPrefix, trustStartDate)

  "DateLastAssetSharedOut View" must {

    val view = viewFor[AdministrationPeriodEndDateView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(form, applyView, messageKeyPrefix, "value")

    behave like pageWithASubmitButton(applyView(form))
  }
}
