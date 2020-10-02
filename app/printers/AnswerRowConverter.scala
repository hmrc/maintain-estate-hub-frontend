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

import java.time.LocalDate

import models.{AddressType, PassportType}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

class AnswerRowConverter(countryOptions: CountryOptions, messageArgs: String*) {

  import ImplicitConverters._

  def dateQuestion(date: LocalDate, labelKey: String)
                  (implicit messages:Messages): Option[AnswerRow] = {
      AnswerRow(
        HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", messageArgs:_*)),
        HtmlFormat.escape(date.format(AnswersFormatters.dateFormatter))
      ).toOption
  }

  def dateQuestion(date: Option[LocalDate], labelKey: String)
                  (implicit messages:Messages): Option[AnswerRow] = {
    date map { d =>
      AnswerRow(
        HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", messageArgs:_*)),
        HtmlFormat.escape(d.format(AnswersFormatters.dateFormatter))
      )
    }
  }

  def stringQuestion(value: String, labelKey: String)
                    (implicit messages:Messages): Option[AnswerRow] = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", messageArgs: _*)),
      HtmlFormat.escape(value)
    ).toOption
  }

  def stringQuestion(value: Option[String], labelKey:String)(implicit messages: Messages): Option[AnswerRow] = {
    value map { v =>
      AnswerRow(
        HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", messageArgs: _*)),
        HtmlFormat.escape(v)
      )
    }
  }

  def addressQuestion(value: AddressType, labelKey: String)
                    (implicit messages:Messages): Option[AnswerRow] = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", messageArgs: _*)),
      AnswersFormatters.address(value, countryOptions)
    ).toOption
  }

  def yesNoQuestion(data: Boolean, labelKey: String)
                   (implicit messages:Messages): Option[AnswerRow] = {
      AnswerRow(
        HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", messageArgs: _*)),
        AnswersFormatters.yesOrNo(data)
      ).toOption
  }

  def ninoQuestion(nino: Option[String], labelKey: String)
                  (implicit messages:Messages): Option[AnswerRow] = {
    nino map { n =>
      AnswerRow(
        HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", messageArgs:_*)),
        AnswersFormatters.nino(n)
      )
    }
  }

  def passportOrIdCardQuestion(passport: Option[PassportType], labelKey: String)
                  (implicit messages:Messages): Option[AnswerRow] = {
    passport map { p =>
      AnswerRow(
        HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", messageArgs:_*)),
        AnswersFormatters.passportOrIdCard(p, countryOptions)
      )
    }
  }

}
