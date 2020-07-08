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

import javax.inject.Inject
import models.{AddressType, EstatePerRepIndType, EstatePerRepOrgType}
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class PersonalRepresentativePrinter @Inject()(countryOptions: CountryOptions) {


  def individual(perRepInd: EstatePerRepIndType, correspondenceAddress: AddressType)(implicit messages: Messages): AnswerSection = {

    val bound = new AnswerRowConverter(countryOptions, AnswersFormatters.fullName(perRepInd.name).body)

    val address = perRepInd.identification.address match {
      case Some(x) => x
      case None => correspondenceAddress
    }

    AnswerSection(
      headingKey = Some(messages("print.personalRepresentative")),
      rows = Seq(
        bound.stringQuestion(messages("print.personalRepresentative.individual"), "print.personalRepresentative.type"),
        bound.fullNameQuestion(perRepInd.name, "print.personalRepresentativeInd.name"),
        bound.dateQuestion(perRepInd.dateOfBirth, "print.personalRepresentativeInd.dateOfBirth"),
        bound.yesNoQuestion(perRepInd.identification.nino.isDefined, "print.personalRepresentativeInd.ninoYesNo"),
        bound.ninoQuestion(perRepInd.identification.nino, "print.personalRepresentativeInd.nino"),
        bound.passportOrIdCardQuestion(perRepInd.identification.passport, "print.personalRepresentativeInd.passportOrIdCard"),
        bound.yesNoQuestion(AddressType.isUK(address), "print.personalRepresentativeInd.addressUkYesNo"),
        bound.addressQuestion(address, "print.personalRepresentativeInd.address"),
        bound.stringQuestion(perRepInd.phoneNumber, "print.personalRepresentativeInd.telephone"),
        bound.yesNoQuestion(perRepInd.email.isDefined, "print.personalRepresentativeInd.emailYesNo"),
        bound.stringQuestion(perRepInd.email, "print.personalRepresentativeInd.email")
      ).flatten
    )
  }

  def business(perRepOrg: EstatePerRepOrgType, correspondenceAddress: AddressType)(implicit messages: Messages): AnswerSection = {

    val bound = new AnswerRowConverter(countryOptions, perRepOrg.orgName)

    val address = perRepOrg.identification.address match {
      case Some(x) => x
      case None => correspondenceAddress
    }

    AnswerSection(
      headingKey = Some(messages("print.personalRepresentative")),
      rows = Seq(
        bound.stringQuestion(messages("print.personalRepresentative.business"), "print.personalRepresentative.type"),
        bound.yesNoQuestion(perRepOrg.identification.utr.isDefined, "print.personalRepresentativeOrg.ukRegisteredYesNo"),
        bound.stringQuestion(perRepOrg.orgName, "print.personalRepresentativeOrg.name"),
        bound.stringQuestion(perRepOrg.identification.utr, "print.personalRepresentativeOrg.utr"),
        bound.yesNoQuestion(AddressType.isUK(address), "print.personalRepresentativeOrg.addressUkYesNo"),
        bound.addressQuestion(address, "print.personalRepresentativeOrg.address"),
        bound.stringQuestion(perRepOrg.phoneNumber, "print.personalRepresentativeOrg.telephone"),
        bound.yesNoQuestion(perRepOrg.email.isDefined, "print.personalRepresentativeOrg.emailYesNo"),
        bound.stringQuestion(perRepOrg.email, "print.personalRepresentativeOrg.email")
      ).flatten
    )
  }

}
