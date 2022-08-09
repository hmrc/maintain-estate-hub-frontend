/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import models.{AddressType, PassportType}
import play.api.i18n.{Lang, MessagesImpl}
import play.twirl.api.Html

import java.time.LocalDate

class AnswersFormattersSpec extends SpecBase {

  private val answersFormatters: AnswersFormatters = injector.instanceOf[AnswersFormatters]

  "CheckAnswersFormatters" when {

    ".date" when {

      def messages(langCode: String): MessagesImpl = {
        val lang: Lang = Lang(langCode)
        MessagesImpl(lang, messagesApi)
      }

      val recentDate: LocalDate = LocalDate.parse("2015-01-25")
      val oldDate: LocalDate = LocalDate.parse("1840-12-01")

      "in English mode" must {
        "format date in English" when {
          "recent date" in {
            val result: Html = answersFormatters.date(recentDate)(messages("en"))
            result mustBe Html("25 January 2015")
          }

          "old date" in {
            val result: Html = answersFormatters.date(oldDate)(messages("en"))
            result mustBe Html("1 December 1840")
          }
        }
      }

      "in Welsh mode" must {
        "format date in Welsh" when {
          "recent date" in {
            val result: Html = answersFormatters.date(recentDate)(messages("cy"))
            result mustBe Html("25 Ionawr 2015")
          }

          "old date" in {
            val result: Html = answersFormatters.date(oldDate)(messages("cy"))
            result mustBe Html("1 Rhagfyr 1840")
          }
        }
      }
    }

    ".yesOrNo" when {

      "true" must {
        "return Yes" in {
          val result: Html = answersFormatters.yesOrNo(answer = true)
          result mustBe Html("Yes")
        }
      }

      "false" must {
        "return No" in {
          val result: Html = answersFormatters.yesOrNo(answer = false)
          result mustBe Html("No")
        }
      }
    }

    ".nino" must {

      "format a nino with prefix and suffix" in {
        val nino = "JP121212A"
        val result = answersFormatters.nino(nino)
        result mustBe Html("JP 12 12 12 A")
      }

      "suppress IllegalArgumentException and not format nino" in {
        val nino = "JP121212"
        val result = answersFormatters.nino(nino)
        result mustBe Html("JP121212")
      }
    }

    ".utr" must {
      "return UTR" in {
        val utr = "1234567890"
        val result = answersFormatters.utr(utr)
        result mustBe Html(utr)
      }
    }

    ".address" when {

      "UK address" must {
        "return formatted address" when {

          "lines 3 and 4 provided" in {
            val address: AddressType = AddressType("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), Some("AB1 1AB"), "GB")
            val result: Html = answersFormatters.address(address)
            result mustBe Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB1 1AB")
          }

          "lines 3 and 4 not provided" in {
            val address: AddressType = AddressType("Line 1", "Line 2", None, None, Some("AB1 1AB"), "GB")
            val result: Html = answersFormatters.address(address)
            result mustBe Html("Line 1<br />Line 2<br />AB1 1AB")
          }
        }
      }

      "non-UK address" must {
        "return formatted address" when {

          "line 3 provided" in {
            val address: AddressType = AddressType("Line 1", "Line 2", Some("Line 3"), None, None, "FR")
            val result: Html = answersFormatters.address(address)
            result mustBe Html("Line 1<br />Line 2<br />Line 3<br />France")
          }

          "line 3 not provided" in {
            val address: AddressType = AddressType("Line 1", "Line 2", None, None, None, "FR")
            val result: Html = answersFormatters.address(address)
            result mustBe Html("Line 1<br />Line 2<br />France")
          }
        }
      }
    }

    ".passportOrIdCard" must {
      "return formatted passport or ID card details" in {
        val passportOrIdCard: PassportType = PassportType("1234567890", LocalDate.parse("2020-01-01"), "FR")
        val result: Html = answersFormatters.passportOrIdCard(passportOrIdCard)
        result mustBe Html("France<br />1234567890<br />1 January 2020")
      }
    }
  }
}
