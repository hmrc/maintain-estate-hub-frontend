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

import models.{AddressType, EstateWillType}
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class DeceasedPersonPrinter @Inject() (answerRowConverter: AnswerRowConverter) {

  import ImplicitConverters._

  def deceasedPerson(deceasedPerson: EstateWillType)(implicit messages: Messages): Option[AnswerSection] = {

    val bound = answerRowConverter.bind(deceasedPerson.name.displayName)

    val prefix: String = "print.deceasedPerson"

    val addressQuestions: Seq[Option[AnswerRow]] = deceasedPerson.identification.address match {
      case Some(address)                                        =>
        Seq(
          bound.yesNoQuestion(data = true, s"$prefix.addressYesNo"),
          bound.yesNoQuestion(AddressType.isUK(address), s"$prefix.addressUkYesNo"),
          bound.addressQuestion(address, s"$prefix.address")
        )
      case None if deceasedPerson.identification.nino.isDefined => Nil
      case _                                                    => Seq(bound.yesNoQuestion(data = false, s"$prefix.addressYesNo"))
    }

    val questions: Seq[AnswerRow] = Seq(
      bound.stringQuestion(deceasedPerson.name.fullName, s"$prefix.name"),
      bound.dateQuestion(deceasedPerson.dateOfDeath, s"$prefix.dateOfDeath"),
      bound.yesNoQuestion(deceasedPerson.dateOfBirth.isDefined, s"$prefix.dateOfBirthYesNo"),
      bound.dateQuestion(deceasedPerson.dateOfBirth, s"$prefix.dateOfBirth"),
      bound.yesNoQuestion(deceasedPerson.identification.nino.isDefined, s"$prefix.ninoYesNo"),
      bound.ninoQuestion(deceasedPerson.identification.nino, s"$prefix.nino")
    ).flatten ++ addressQuestions.flatten

    questions match {
      case Nil => None
      case _   =>
        AnswerSection(
          headingKey = Some(messages(prefix)),
          rows = questions,
          sectionKey = Some(messages(prefix))
        ).toOption
    }
  }

}
