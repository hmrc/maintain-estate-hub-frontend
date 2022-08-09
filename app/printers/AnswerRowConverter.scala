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

import models.{AddressType, PassportType}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import printers.ImplicitConverters._
import viewmodels.AnswerRow

import java.time.LocalDate
import javax.inject.Inject

class AnswerRowConverter @Inject()(answersFormatters: AnswersFormatters) {

  def bind(messageArgs: String*)(implicit messages: Messages): Bound = new Bound(messageArgs: _*)

  class Bound(messageArgs: String*)(implicit messages: Messages) {

    def dateQuestion(date: LocalDate, labelKey: String): Option[AnswerRow] = {
      answerRow(date, labelKey, answersFormatters.date).toOption
    }

    def dateQuestion(date: Option[LocalDate], labelKey: String): Option[AnswerRow] = {
      date flatMap { d =>
        dateQuestion(d, labelKey)
      }
    }

    def stringQuestion(value: String, labelKey: String): Option[AnswerRow] = {
      answerRow(value, labelKey, HtmlFormat.escape).toOption
    }

    def stringQuestion(value: Option[String], labelKey: String): Option[AnswerRow] = {
      value flatMap { v =>
        stringQuestion(v, labelKey)
      }
    }

    def addressQuestion(value: AddressType, labelKey: String): Option[AnswerRow] = {
      answerRow(value, labelKey, answersFormatters.address).toOption
    }

    def yesNoQuestion(data: Boolean, labelKey: String): Option[AnswerRow] = {
      answerRow(data, labelKey, answersFormatters.yesOrNo).toOption
    }

    def ninoQuestion(nino: Option[String], labelKey: String): Option[AnswerRow] = {
      nino map { n =>
        answerRow(n, labelKey, answersFormatters.nino)
      }
    }

    def passportOrIdCardQuestion(passport: Option[PassportType], labelKey: String): Option[AnswerRow] = {
      passport map { p =>
        answerRow(p, labelKey, answersFormatters.passportOrIdCard)
      }
    }

    private def answerRow[T](answer: T,
                             labelKey: String,
                             format: T => Html): AnswerRow = {
      AnswerRow(
        label = messages(s"$labelKey.checkYourAnswersLabel", messageArgs: _*),
        answer = format(answer)
      )
    }
  }

}
