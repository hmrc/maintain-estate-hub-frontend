/*
 * Copyright 2024 HM Revenue & Customs
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
import models.GetEstate
import play.api.i18n.Messages
import viewmodels.AnswerSection

class PrintHelper @Inject()(personalRepresentativePrinter: PersonalRepresentativePrinter,
                            estateNamePrinter: EstateNamePrinter,
                            administrationPeriodPrinter: AdministrationPeriodPrinter,
                            deceasedPersonPrinter: DeceasedPersonPrinter){

  def estateName(getEstate: GetEstate)(implicit messages: Messages): Seq[AnswerSection] =
    estateNamePrinter.name(getEstate.correspondence).toList

  def personalRepresentative(getEstate: GetEstate)(implicit messages: Messages) : Seq[AnswerSection] = {

    val individual = getEstate.estate.entities.personalRepresentative.estatePerRepInd
    val business = getEstate.estate.entities.personalRepresentative.estatePerRepOrg

    val correspondenceAddress = getEstate.correspondence.address

    Seq(
      personalRepresentativePrinter.individual(individual, correspondenceAddress),
      personalRepresentativePrinter.business(business, correspondenceAddress)
    ).flatten
  }

  def administrationPeriod(getEstate: GetEstate)(implicit messages: Messages): Seq[AnswerSection] =
    administrationPeriodPrinter.period(getEstate.trustEndDate).toList

  def deceasedPerson(getEstate: GetEstate)(implicit messages: Messages) : Seq[AnswerSection] = {
    deceasedPersonPrinter.deceasedPerson(getEstate.estate.entities.deceased).toList
  }

}
