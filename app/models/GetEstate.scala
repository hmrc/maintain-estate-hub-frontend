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

package models

import java.time.LocalDate

import play.api.libs.json._

case class GetEstate(matchData: MatchData,
                     correspondence: Correspondence,
                     declaration: Declaration,
                     estate: Estate,
                     trustEndDate: Option[LocalDate]) {

  def isClosing: Boolean = trustEndDate.isDefined
}

object GetEstate {
  implicit val formats: Format[GetEstate] = Json.format[GetEstate]
}

case class MatchData(utr: String)

object MatchData {
  implicit val matchDataFormat: Format[MatchData] = Json.format[MatchData]
}

case class Correspondence(abroadIndicator: Boolean,
                          name: String,
                          address: AddressType,
                          phoneNumber: String)

object Correspondence {
  implicit val correspondenceFormat : Format[Correspondence] = Json.format[Correspondence]
}

case class Declaration(name: NameType,
                       address: AddressType)

object Declaration {
  implicit val declarationFormat: Format[Declaration] = Json.format[Declaration]
}

case class Estate(entities: EntitiesType,
                  administrationEndDate: Option[LocalDate],
                  periodTaxDues: String)

object Estate {
  implicit val estateFormat: Format[Estate] = Json.format[Estate]
}
