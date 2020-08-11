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

import config.annotations.AllCountries
import javax.inject.Inject
import models.EstateWillType
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class DeceasedPersonPrinter @Inject()(@AllCountries countryOptions: CountryOptions) {

  import ImplicitConverters._

  def period(deceasedPerson: EstateWillType)(implicit messages: Messages): Option[AnswerSection] = {

    val bound = new AnswerRowConverter(countryOptions, deceasedPerson.name.fullName)

    val prefix: String = "print.deceasedPerson"

    val questions: Seq[AnswerRow] = Seq(
      bound.fullNameQuestion(deceasedPerson.name, s"$prefix.name"),
      bound.dateQuestion(deceasedPerson.dateOfDeath, s"$prefix.dateOfDeath"),
      bound.yesNoQuestion(deceasedPerson.dateOfBirth.isDefined, s"$prefix.dateOfBirthYesNo"),
      bound.dateQuestion(deceasedPerson.dateOfBirth, s"$prefix.dateOfBirth"),
      ???
    ).flatten

    questions match {
      case Nil => None
      case _ => AnswerSection(
        headingKey = Some(messages("print.deceasedPerson")),
        rows = questions
      ).toOption
    }
  }

}
