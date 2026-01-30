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

import models.{AddressType, PassportType}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat.escape
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.language.LanguageUtils
import utils.countryOptions.CountryOptions
import java.time.LocalDate

import javax.inject.Inject

import scala.util.Try

class AnswersFormatters @Inject() (languageUtils: LanguageUtils)(implicit countryOptions: CountryOptions) {

  def date(date: LocalDate)(implicit messages: Messages): Html =
    HtmlFormat.escape(languageUtils.Dates.formatDate(date))

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html =
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }

  def address(address: AddressType)(implicit messages: Messages): Html = {

    val lines = address.postCode match {
      case Some(x) if AddressType.isUK(address) =>
        Seq(
          Some(HtmlFormat.escape(address.line1)),
          Some(HtmlFormat.escape(address.line2)),
          address.line3.map(HtmlFormat.escape),
          address.line4.map(HtmlFormat.escape),
          Some(HtmlFormat.escape(x))
        ).flatten
      case _                                    =>
        Seq(
          Some(HtmlFormat.escape(address.line1)),
          Some(HtmlFormat.escape(address.line2)),
          address.line3.map(HtmlFormat.escape),
          address.line4.map(HtmlFormat.escape),
          Some(country(address.country))
        ).flatten
    }

    breakLines(lines)
  }

  def nino(nino: String): Html = {
    val formatted = Try(Nino(nino).formatted).getOrElse(nino)
    escape(formatted)
  }

  private def country(code: String)(implicit messages: Messages): Html =
    HtmlFormat.escape(countryOptions.options().find(_.value.equals(code)).map(_.label).getOrElse(""))

  def utr(answer: String): Html = HtmlFormat.escape(answer)

  def passportOrIdCard(passport: PassportType)(implicit messages: Messages): Html = {
    val lines =
      Seq(
        country(passport.countryOfIssue),
        HtmlFormat.escape(passport.number),
        date(passport.expirationDate)
      )

    breakLines(lines)
  }

  private def breakLines(lines: Seq[Html]): Html =
    Html(lines.mkString("<br />"))

}
