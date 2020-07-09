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

import java.time.format.DateTimeFormatter

import models.{AddressType, NameType, PassportType}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.domain.Nino
import utils.countryOptions.CountryOptions

object AnswersFormatters {

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def fullName(name: NameType) = {
    val middle = name.middleName.map(" " + _ + " ").getOrElse(" ")
    HtmlFormat.escape(s"${name.firstName}$middle${name.lastName}")
  }

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }
  }

  def address(address: AddressType, countryOptions: CountryOptions): Html = {

    val lines = address.postCode match {
      case Some(x) if AddressType.isUK(address) =>
        Seq(
          Some(HtmlFormat.escape(address.line1)),
          Some(HtmlFormat.escape(address.line2)),
          address.line3.map(HtmlFormat.escape),
          address.line4.map(HtmlFormat.escape),
          Some(HtmlFormat.escape(x))
        ).flatten
      case _ =>
        Seq(
          Some(HtmlFormat.escape(address.line1)),
          Some(HtmlFormat.escape(address.line2)),
          address.line3.map(HtmlFormat.escape),
          address.line4.map(HtmlFormat.escape),
          Some(country(address.country, countryOptions))
        ).flatten
    }

    Html(lines.mkString("<br />"))
  }

  def nino(nino: String): Html = HtmlFormat.escape(Nino(nino).formatted)

  def country(code: String, countryOptions: CountryOptions): Html =
    HtmlFormat.escape(countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse(""))

  def utr(answer: String)(implicit messages: Messages): Html = HtmlFormat.escape(answer)

  def passportOrIdCard(passport: PassportType, countryOptions: CountryOptions): Html = {
    val lines =
      Seq(
        Some(country(passport.countryOfIssue, countryOptions)),
        Some(HtmlFormat.escape(passport.number)),
        Some(HtmlFormat.escape(passport.expirationDate.format(dateFormatter)))
      ).flatten

    Html(lines.mkString("<br />"))
  }

}
