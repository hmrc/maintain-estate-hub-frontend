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

package printers

import java.time.LocalDate

import base.SpecBase
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class AdministrationPeriodPrinterSpec extends SpecBase {

  private val printer: AdministrationPeriodPrinter = injector.instanceOf[AdministrationPeriodPrinter]

  "administration period printer" must {

    "print if close date exists" in {

      val result = printer.period(Some(LocalDate.parse("2020-01-01")))

      result mustBe Some(AnswerSection(
        headingKey = Some("Administration period"),
        rows = Seq(
          AnswerRow(messages("What is the date the administration period ended?"), Html("1 January 2020"))
        )
      ))
    }

    "not print if close date doesn't exist" in {

      val result = printer.period(None)

      result mustBe None
    }
  }
}
