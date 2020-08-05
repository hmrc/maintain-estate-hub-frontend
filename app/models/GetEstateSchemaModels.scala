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

package models

import java.time.LocalDate

import play.api.libs.json._

case class PersonalRepresentativeType(estatePerRepInd : Option[EstatePerRepIndType] = None,
                                      estatePerRepOrg : Option[EstatePerRepOrgType] = None)

object PersonalRepresentativeType {
  implicit val personalRepFormats : Format[PersonalRepresentativeType] = Json.format[PersonalRepresentativeType]
}

case class EstatePerRepIndType(name: NameType,
                               dateOfBirth: LocalDate,
                               identification: IdentificationType,
                               phoneNumber: String,
                               email: Option[String],
                               lineNo: Option[String],
                               bpMatchStatus: Option[String],
                               entityStart: LocalDate)

object EstatePerRepIndType {
  implicit val estatePerRepIndTypeFormat: Format[EstatePerRepIndType] = Json.format[EstatePerRepIndType]
}

case class EstatePerRepOrgType(orgName: String,
                               phoneNumber: String,
                               email: Option[String] = None,
                               identification: IdentificationOrgType,
                               lineNo: Option[String],
                               bpMatchStatus: Option[String],
                               entityStart: LocalDate)

object EstatePerRepOrgType {
  implicit val estatePerRepOrgTypeFormat: Format[EstatePerRepOrgType] = Json.format[EstatePerRepOrgType]
}

case class EstateWillType(name: NameType,
                          dateOfBirth: Option[LocalDate],
                          dateOfDeath: LocalDate,
                          identification: Option[IdentificationType],
                          lineNo: String,
                          bpMatchStatus: Option[String],
                          entityStart: LocalDate)

object EstateWillType {
  implicit val estateWillTypeFormat: Format[EstateWillType] = Json.format[EstateWillType]
}

case class EntitiesType(personalRepresentative: PersonalRepresentativeType,
                        deceased: EstateWillType)

object EntitiesType {
  implicit val entitiesTypeFormat: Format[EntitiesType] = Json.format[EntitiesType]
}

case class Estate(entities: EntitiesType,
                  administrationEndDate: Option[LocalDate],
                  periodTaxDues: String)

object Estate {
  implicit val estateFormat: Format[Estate] = Json.format[Estate]
}

case class GetEstate(matchData: MatchData,
                     correspondence: Correspondence,
                     declaration: Declaration,
                     estate: Estate)

object GetEstate {
  implicit val formats: Format[GetEstate] = Json.format[GetEstate]
}

case class NameType(firstName: String,
                    middleName: Option[String],
                    lastName: String) {

  def fullName : String = {
    val middle = middleName.map(" " + _ + " ").getOrElse(" ")
    s"${firstName}$middle${lastName}"
  }

}

object NameType {
  implicit val nameTypeFormat: Format[NameType] = Json.format[NameType]
}

case class AddressType(line1: String,
                       line2: String,
                       line3: Option[String],
                       line4: Option[String],
                       postCode: Option[String],
                       country: String)

object AddressType {

  def isUK(addressType: AddressType) = addressType.country.toUpperCase == "GB"

  implicit val addressTypeFormat: Format[AddressType] = Json.format[AddressType]
}

case class IdentificationOrgType(utr: Option[String],
                                 address: Option[AddressType])

object IdentificationOrgType {
  implicit val identificationOrgTypeFormat: Format[IdentificationOrgType] = Json.format[IdentificationOrgType]
}

case class IdentificationType(nino: Option[String],
                              passport: Option[PassportType],
                              address: Option[AddressType])

object IdentificationType {
  implicit val identificationTypeFormat: Format[IdentificationType] = Json.format[IdentificationType]
}

case class PassportType(number: String,
                        expirationDate: LocalDate,
                        countryOfIssue: String,
                        isPassport: Option[Boolean] = None)

object PassportType {
  implicit val passportTypeFormat: Format[PassportType] = Json.format[PassportType]
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