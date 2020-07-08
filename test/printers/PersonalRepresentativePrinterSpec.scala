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
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class PersonalRepresentativePrinterSpec extends SpecBase {

  "personal representative individual printer" when {

    "personal rep individual with national insurance number and UK correspondence address" must {

      "print" in {
        val printer = injector.instanceOf[PersonalRepresentativePrinter]

        val result = printer.individual(FakeData.personalRepresentativeIndividualNino, FakeData.correspondenceAddressUk)

        result mustBe AnswerSection(
          headingKey = Some("Personal representative"),
          rows = Seq(
            AnswerRow(Html("Is the personal representative an individual or business?"), Html("Individual")),
            AnswerRow(Html("What is the personal representative’s name?"), Html("Adam Conder")),
            AnswerRow(Html("What is Adam Conder’s date of birth?"), Html("3 May 2010")),
            AnswerRow(Html("Does Adam Conder have a National Insurance number?"), Html("Yes")),
            AnswerRow(Html("What is Adam Conder’s National Insurance number?"), Html("JP 12 12 12 A")),
            AnswerRow(Html("Does Adam Conder live in the UK?"), Html("Yes")),
            AnswerRow(Html("What is Adam Conder’s address?"), Html("line1<br />line2<br />NE991ZZ")),
            AnswerRow(Html("What is Adam Conder’s telephone number?"), Html("+447838383823")),
            AnswerRow(Html("Do you know Adam Conder’s email address?"), Html("Yes")),
            AnswerRow(Html("What is Adam Conder’s email address?"), Html("email@test.com"))
          )
        )
      }
    }

    "personal rep individual with national insurance number and Non-UK correspondence address" must {

      "print" in {
        val printer = injector.instanceOf[PersonalRepresentativePrinter]

        val result = printer.individual(FakeData.personalRepresentativeIndividualNino, FakeData.correspondenceAddressNonUk)

        result mustBe AnswerSection(
          headingKey = Some("Personal representative"),
          rows = Seq(
            AnswerRow(Html("Is the personal representative an individual or business?"), Html("Individual")),
            AnswerRow(Html("What is the personal representative’s name?"), Html("Adam Conder")),
            AnswerRow(Html("What is Adam Conder’s date of birth?"), Html("3 May 2010")),
            AnswerRow(Html("Does Adam Conder have a National Insurance number?"), Html("Yes")),
            AnswerRow(Html("What is Adam Conder’s National Insurance number?"), Html("JP 12 12 12 A")),
            AnswerRow(Html("Does Adam Conder live in the UK?"), Html("No")),
            AnswerRow(Html("What is Adam Conder’s address?"), Html("line1<br />line2<br />line3<br />Germany")),
            AnswerRow(Html("What is Adam Conder’s telephone number?"), Html("+447838383823")),
            AnswerRow(Html("Do you know Adam Conder’s email address?"), Html("Yes")),
            AnswerRow(Html("What is Adam Conder’s email address?"), Html("email@test.com"))
          )
        )
      }
    }

    "personal rep individual with passport or id card, uk address" must {

      "print" in {
        val printer = injector.instanceOf[PersonalRepresentativePrinter]

        val result = printer.individual(FakeData.personalRepresentativeIndividualPassportOrIdCardUkAddress, FakeData.correspondenceAddressUk)

        result mustBe AnswerSection(
          headingKey = Some("Personal representative"),
          rows = Seq(
            AnswerRow(Html("Is the personal representative an individual or business?"), Html("Individual")),
            AnswerRow(Html("What is the personal representative’s name?"), Html("Adam Conder")),
            AnswerRow(Html("What is Adam Conder’s date of birth?"), Html("3 May 2010")),
            AnswerRow(Html("Does Adam Conder have a National Insurance number?"), Html("No")),
            AnswerRow(Html("What are Adam Conder’s passport or ID card details?"), Html("Germany<br />1234567890<br />1 January 2020")),
            AnswerRow(Html("Does Adam Conder live in the UK?"), Html("Yes")),
            AnswerRow(Html("What is Adam Conder’s address?"), Html("lane 1<br />lane 2<br />NE211ZZ")),
            AnswerRow(Html("What is Adam Conder’s telephone number?"), Html("+447838383823")),
            AnswerRow(Html("Do you know Adam Conder’s email address?"), Html("Yes")),
            AnswerRow(Html("What is Adam Conder’s email address?"), Html("email@test.com"))
          )
        )
      }
    }

    "personal rep individual with passport or id card, non-uk address" must {

      "print" in {
        val printer = injector.instanceOf[PersonalRepresentativePrinter]

        val result = printer.individual(FakeData.personalRepresentativeIndividualPassportOrIdCardNonUkAddress, FakeData.correspondenceAddressUk)

        result mustBe AnswerSection(
          headingKey = Some("Personal representative"),
          rows = Seq(
            AnswerRow(Html("Is the personal representative an individual or business?"), Html("Individual")),
            AnswerRow(Html("What is the personal representative’s name?"), Html("Adam Conder")),
            AnswerRow(Html("What is Adam Conder’s date of birth?"), Html("3 May 2010")),
            AnswerRow(Html("Does Adam Conder have a National Insurance number?"), Html("No")),
            AnswerRow(Html("What are Adam Conder’s passport or ID card details?"), Html("Germany<br />1234567890<br />1 January 2020")),
            AnswerRow(Html("Does Adam Conder live in the UK?"), Html("No")),
            AnswerRow(Html("What is Adam Conder’s address?"), Html("line1<br />line2<br />line3<br />France")),
            AnswerRow(Html("What is Adam Conder’s telephone number?"), Html("+447838383823")),
            AnswerRow(Html("Do you know Adam Conder’s email address?"), Html("Yes")),
            AnswerRow(Html("What is Adam Conder’s email address?"), Html("email@test.com"))
          )
        )
      }
    }
  }

  "personal representative business print" when {

    "personal rep business with utr and UK correspondence address" must {

      "print" in {
        val printer = injector.instanceOf[PersonalRepresentativePrinter]

        val result = printer.business(FakeData.personalRepresentativeBusinessUtr, FakeData.correspondenceAddressUk)

        result mustBe AnswerSection(
          headingKey = Some("Personal representative"),
          rows = Seq(
            AnswerRow(Html("Is the personal representative an individual or business?"), Html("Business")),
            AnswerRow(Html("Is the personal representative a UK registered business?"), Html("Yes")),
            AnswerRow(Html("What is the business’s name?"), Html("Company Ltd")),
            AnswerRow(Html("What is Company Ltd’s Unique Taxpayer Reference (UTR) number?"), Html("1234567892")),
            AnswerRow(Html("Is Company Ltd’s address in the UK?"), Html("Yes")),
            AnswerRow(Html("What is Company Ltd’s address?"), Html("line1<br />line2<br />NE991ZZ")),
            AnswerRow(Html("What is Company Ltd’s telephone number?"), Html("+447838383823")),
            AnswerRow(Html("Do you know Company Ltd’s email address?"), Html("No"))
          )
        )
      }
    }

    "personal rep business with utr and Non-UK correspondence address" must {

      "print" in {
        val printer = injector.instanceOf[PersonalRepresentativePrinter]

        val result = printer.business(FakeData.personalRepresentativeBusinessUtr, FakeData.correspondenceAddressNonUk)

        result mustBe AnswerSection(
          headingKey = Some("Personal representative"),
          rows = Seq(
            AnswerRow(Html("Is the personal representative an individual or business?"), Html("Business")),
            AnswerRow(Html("Is the personal representative a UK registered business?"), Html("Yes")),
            AnswerRow(Html("What is the business’s name?"), Html("Company Ltd")),
            AnswerRow(Html("What is Company Ltd’s Unique Taxpayer Reference (UTR) number?"), Html("1234567892")),
            AnswerRow(Html("Is Company Ltd’s address in the UK?"), Html("No")),
            AnswerRow(Html("What is Company Ltd’s address?"), Html("line1<br />line2<br />line3<br />Germany")),
            AnswerRow(Html("What is Company Ltd’s telephone number?"), Html("+447838383823")),
            AnswerRow(Html("Do you know Company Ltd’s email address?"), Html("No"))
          )
        )
      }
    }

    "personal rep business, no utr and uk address" must {

      "print" in {
        val printer = injector.instanceOf[PersonalRepresentativePrinter]

        val result = printer.business(FakeData.personalRepresentativeBusinessWithoutUtrUk, FakeData.correspondenceAddressNonUk)

        result mustBe AnswerSection(
          headingKey = Some("Personal representative"),
          rows = Seq(
            AnswerRow(Html("Is the personal representative an individual or business?"), Html("Business")),
            AnswerRow(Html("Is the personal representative a UK registered business?"), Html("No")),
            AnswerRow(Html("What is the business’s name?"), Html("Company Ltd")),
            AnswerRow(Html("Is Company Ltd’s address in the UK?"), Html("Yes")),
            AnswerRow(Html("What is Company Ltd’s address?"), Html("line1<br />line2<br />NE991ZZ")),
            AnswerRow(Html("What is Company Ltd’s telephone number?"), Html("+447838383823")),
            AnswerRow(Html("Do you know Company Ltd’s email address?"), Html("Yes")),
            AnswerRow(Html("What is Company Ltd’s email address?"), Html("email@test.com"))
          )
        )
      }
    }

    "personal rep business, no utr and non-uk address" must {

      "print" in {
        val printer = injector.instanceOf[PersonalRepresentativePrinter]

        val result = printer.business(FakeData.personalRepresentativeBusinessWithoutUtrNonUk, FakeData.correspondenceAddressNonUk)

        result mustBe AnswerSection(
          headingKey = Some("Personal representative"),
          rows = Seq(
            AnswerRow(Html("Is the personal representative an individual or business?"), Html("Business")),
            AnswerRow(Html("Is the personal representative a UK registered business?"), Html("No")),
            AnswerRow(Html("What is the business’s name?"), Html("Company Ltd")),
            AnswerRow(Html("Is Company Ltd’s address in the UK?"), Html("No")),
            AnswerRow(Html("What is Company Ltd’s address?"), Html("line1<br />line2<br />line3<br />France")),
            AnswerRow(Html("What is Company Ltd’s telephone number?"), Html("+447838383823")),
            AnswerRow(Html("Do you know Company Ltd’s email address?"), Html("Yes")),
            AnswerRow(Html("What is Company Ltd’s email address?"), Html("email@test.com"))
          )
        )
      }
    }

  }

}
