/*
 * Copyright 2026 HM Revenue & Customs
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

import base.{FakeData, SpecBase}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class DeceasedPersonPrinterSpec extends SpecBase {

  "deceased person printer" must {

    "print for deceased person with minimal data and middle name" in {

      val printer = injector.instanceOf[DeceasedPersonPrinter]

      val result = printer.deceasedPerson(FakeData.deceasedPersonWithMinimalData)

      result.value mustBe AnswerSection(
        headingKey = Some("Person who died"),
        rows = Seq(
          AnswerRow("What is the name of the person who died?", Html("John James Smith")),
          AnswerRow("What is John Smith’s date of death?", Html("1 January 2020")),
          AnswerRow("Do you know John Smith’s date of birth?", Html("No")),
          AnswerRow("Do you know John Smith’s National Insurance number?", Html("No")),
          AnswerRow("Do you know John Smith’s last known address?", Html("No"))
        ),
        sectionKey = Some("Person who died")
      )
    }

    "print for deceased person with date of birth and NINO" in {

      val printer = injector.instanceOf[DeceasedPersonPrinter]

      val result = printer.deceasedPerson(FakeData.deceasedPersonWithDateOfBirthAndNino)

      result.value mustBe AnswerSection(
        headingKey = Some("Person who died"),
        rows = Seq(
          AnswerRow("What is the name of the person who died?", Html("John Smith")),
          AnswerRow("What is John Smith’s date of death?", Html("1 January 2020")),
          AnswerRow("Do you know John Smith’s date of birth?", Html("Yes")),
          AnswerRow("What is John Smith’s date of birth?", Html("3 February 1996")),
          AnswerRow("Do you know John Smith’s National Insurance number?", Html("Yes")),
          AnswerRow("What is John Smith’s National Insurance number?", Html("AA 00 00 00 A"))
        ),
        sectionKey = Some("Person who died")
      )
    }

    "print for deceased person with UK address" in {

      val printer = injector.instanceOf[DeceasedPersonPrinter]

      val result = printer.deceasedPerson(FakeData.deceasedPersonWithUkAddress)

      result.value mustBe AnswerSection(
        headingKey = Some("Person who died"),
        rows = Seq(
          AnswerRow("What is the name of the person who died?", Html("John Smith")),
          AnswerRow("What is John Smith’s date of death?", Html("1 January 2020")),
          AnswerRow("Do you know John Smith’s date of birth?", Html("No")),
          AnswerRow("Do you know John Smith’s National Insurance number?", Html("No")),
          AnswerRow("Do you know John Smith’s last known address?", Html("Yes")),
          AnswerRow("Was John Smith’s last known address in the UK?", Html("Yes")),
          AnswerRow("What is John Smith’s last known address?", Html("1<br />British Lane<br />NE1 1NE"))
        ),
        sectionKey = Some("Person who died")
      )
    }

    "print for deceased person with non-UK address" in {

      val printer = injector.instanceOf[DeceasedPersonPrinter]

      val result = printer.deceasedPerson(FakeData.deceasedPersonWithNonUkAddress)

      result.value mustBe AnswerSection(
        headingKey = Some("Person who died"),
        rows = Seq(
          AnswerRow("What is the name of the person who died?", Html("John Smith")),
          AnswerRow("What is John Smith’s date of death?", Html("1 January 2020")),
          AnswerRow("Do you know John Smith’s date of birth?", Html("No")),
          AnswerRow("Do you know John Smith’s National Insurance number?", Html("No")),
          AnswerRow("Do you know John Smith’s last known address?", Html("Yes")),
          AnswerRow("Was John Smith’s last known address in the UK?", Html("No")),
          AnswerRow("What is John Smith’s last known address?", Html("2<br />German Street<br />Germany"))
        ),
        sectionKey = Some("Person who died")
      )
    }

  }
}
