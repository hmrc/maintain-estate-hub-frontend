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

package printers

import base.{FakeData, SpecBase}
import models.{Correspondence, EstateWillType}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class DeceasedPersonPrinterSpec extends SpecBase {

  "deceased person printer" must {

    "print for deceased person with minimal data" in {

      val printer = injector.instanceOf[DeceasedPersonPrinter]

      val result = printer.deceasedPerson(FakeData.deceasedPersonWithMinimalData)

      result.value mustBe AnswerSection(
        headingKey = Some("Person who died"),
        rows = Seq(
          AnswerRow(Html("What is the name of the person who died?"), Html("John Smith")),
          AnswerRow(Html("What is John Smith’s date of death?"), Html("1 January 2020")),
          AnswerRow(Html("Do you know John Smith’s date of birth?"), Html("No")),
          AnswerRow(Html("Do you know John Smith’s National Insurance number?"), Html("No")),
          AnswerRow(Html("Do you know John Smith’s last known address?"), Html("No"))
        )
      )
    }

    "print for deceased person with date of birth and NINO" in {

      val printer = injector.instanceOf[DeceasedPersonPrinter]

      val result = printer.deceasedPerson(FakeData.deceasedPersonWithDateOfBirthAndNino)

      result.value mustBe AnswerSection(
        headingKey = Some("Person who died"),
        rows = Seq(
          AnswerRow(Html("What is the name of the person who died?"), Html("John Smith")),
          AnswerRow(Html("What is John Smith’s date of death?"), Html("1 January 2020")),
          AnswerRow(Html("Do you know John Smith’s date of birth?"), Html("Yes")),
          AnswerRow(Html("What is John Smith’s date of birth?"), Html("3 February 1996")),
          AnswerRow(Html("Do you know John Smith’s National Insurance number?"), Html("Yes")),
          AnswerRow(Html("What is John Smith’s National Insurance number?"), Html("AA 00 00 00 A"))
        )
      )
    }

    "print for deceased person with UK address" in {

      val printer = injector.instanceOf[DeceasedPersonPrinter]

      val result = printer.deceasedPerson(FakeData.deceasedPersonWithUkAddress)

      result.value mustBe AnswerSection(
        headingKey = Some("Person who died"),
        rows = Seq(
          AnswerRow(Html("What is the name of the person who died?"), Html("John Smith")),
          AnswerRow(Html("What is John Smith’s date of death?"), Html("1 January 2020")),
          AnswerRow(Html("Do you know John Smith’s date of birth?"), Html("No")),
          AnswerRow(Html("Do you know John Smith’s National Insurance number?"), Html("No")),
          AnswerRow(Html("Do you know John Smith’s last known address?"), Html("Yes")),
          AnswerRow(Html("Was John Smith’s last known address in the UK?"), Html("Yes")),
          AnswerRow(Html("What is John Smith’s last known address?"), Html("1<br />British Lane<br />NE1 1NE"))
        )
      )
    }

    "print for deceased person with non-UK address" in {

      val printer = injector.instanceOf[DeceasedPersonPrinter]

      val result = printer.deceasedPerson(FakeData.deceasedPersonWithNonUkAddress)

      result.value mustBe AnswerSection(
        headingKey = Some("Person who died"),
        rows = Seq(
          AnswerRow(Html("What is the name of the person who died?"), Html("John Smith")),
          AnswerRow(Html("What is John Smith’s date of death?"), Html("1 January 2020")),
          AnswerRow(Html("Do you know John Smith’s date of birth?"), Html("No")),
          AnswerRow(Html("Do you know John Smith’s National Insurance number?"), Html("No")),
          AnswerRow(Html("Do you know John Smith’s last known address?"), Html("Yes")),
          AnswerRow(Html("Was John Smith’s last known address in the UK?"), Html("No")),
          AnswerRow(Html("What is John Smith’s last known address?"), Html("2<br />German Street<br />Germany"))
        )
      )
    }

  }
}
