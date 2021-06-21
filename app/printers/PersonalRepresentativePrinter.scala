/*
 * Copyright 2021 HM Revenue & Customs
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

import models.{AddressType, EstatePerRepIndType, EstatePerRepOrgType}
import play.api.i18n.Messages
import viewmodels.AnswerSection

import javax.inject.Inject

class PersonalRepresentativePrinter @Inject()(answerRowConverter: AnswerRowConverter) {

  import ImplicitConverters._

  def individual(perRepInd: Option[EstatePerRepIndType], correspondenceAddress: AddressType)(implicit messages: Messages): Option[AnswerSection] = {
    perRepInd flatMap { ind =>

      val bound = answerRowConverter.bind(ind.name.displayName)

      val address = ind.identification.address match {
        case Some(x) => x
        case None => correspondenceAddress
      }

      val questions = Seq(
        bound.stringQuestion(messages("print.personalRepresentative.individual"), "print.personalRepresentative.type"),
        bound.stringQuestion(ind.name.fullName, "print.personalRepresentativeInd.name"),
        bound.dateQuestion(ind.dateOfBirth, "print.personalRepresentativeInd.dateOfBirth"),
        bound.yesNoQuestion(ind.identification.nino.isDefined, "print.personalRepresentativeInd.ninoYesNo"),
        bound.ninoQuestion(ind.identification.nino, "print.personalRepresentativeInd.nino"),
        bound.passportOrIdCardQuestion(ind.identification.passport, "print.personalRepresentativeInd.passportOrIdCard"),
        bound.yesNoQuestion(AddressType.isUK(address), "print.personalRepresentativeInd.addressUkYesNo"),
        bound.addressQuestion(address, "print.personalRepresentativeInd.address"),
        bound.stringQuestion(ind.phoneNumber, "print.personalRepresentativeInd.telephone"),
        bound.yesNoQuestion(ind.email.isDefined, "print.personalRepresentativeInd.emailYesNo"),
        bound.stringQuestion(ind.email, "print.personalRepresentativeInd.email")
      ).flatten

      questions match {
        case Nil => None
        case _ => AnswerSection(
          headingKey = Some(messages("print.personalRepresentative")),
          rows = questions
        ).toOption
      }
    }
  }

  def business(perRepOrg: Option[EstatePerRepOrgType], correspondenceAddress: AddressType)(implicit messages: Messages): Option[AnswerSection] = {
    perRepOrg.flatMap { org =>

      val bound = answerRowConverter.bind(org.orgName)

      val address = org.identification.address match {
        case Some(x) => x
        case None => correspondenceAddress
      }

      val questions = Seq(
        bound.stringQuestion(messages("print.personalRepresentative.business"), "print.personalRepresentative.type"),
        bound.yesNoQuestion(org.identification.utr.isDefined, "print.personalRepresentativeOrg.ukRegisteredYesNo"),
        bound.stringQuestion(org.orgName, "print.personalRepresentativeOrg.name"),
        bound.stringQuestion(org.identification.utr, "print.personalRepresentativeOrg.utr"),
        bound.yesNoQuestion(AddressType.isUK(address), "print.personalRepresentativeOrg.addressUkYesNo"),
        bound.addressQuestion(address, "print.personalRepresentativeOrg.address"),
        bound.stringQuestion(org.phoneNumber, "print.personalRepresentativeOrg.telephone"),
        bound.yesNoQuestion(org.email.isDefined, "print.personalRepresentativeOrg.emailYesNo"),
        bound.stringQuestion(org.email, "print.personalRepresentativeOrg.email")
      ).flatten

      questions match {
        case Nil => None
        case _ => AnswerSection(
          headingKey = Some(messages("print.personalRepresentative")),
          rows = questions
        ).toOption
      }
    }
  }

}
