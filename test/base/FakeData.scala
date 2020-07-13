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

package base

import java.time.LocalDate

import models._

object FakeData {

  lazy val correspondenceAddressUk = AddressType("line1", "line2", None, None, Some("NE991ZZ"), "GB")
  lazy val correspondenceAddressNonUk = AddressType("line1", "line2", Some("line3"), None, None, "DE")

  lazy val personalRepresentativeIndividualNino : EstatePerRepIndType = EstatePerRepIndType(
    name = NameType("Adam", None, "Conder"),
    dateOfBirth = LocalDate.of(2010, 5, 3),
    identification = IdentificationType(
      nino = Some("JP121212A"),
      passport = None,
      address = None
    ),
    email = Some("email@test.com"),
    lineNo = "1",
    bpMatchStatus = Some("01"),
    entityStart = LocalDate.of(2020, 4, 10),
    phoneNumber = "+447838383823"
  )

  lazy val personalRepresentativeIndividualPassportOrIdCardNonUkAddress : EstatePerRepIndType = EstatePerRepIndType(
    name = NameType("Adam", None, "Conder"),
    dateOfBirth = LocalDate.of(2010, 5, 3),
    identification = IdentificationType(
      nino = None,
      passport = Some(PassportType("1234567890", LocalDate.of(2020, 1, 1), countryOfIssue = "DE", isPassport = None)),
      address = Some(AddressType("line1", "line2", Some("line3"), None, None, "FR"))
    ),
    email = Some("email@test.com"),
    lineNo = "1",
    bpMatchStatus = Some("01"),
    entityStart = LocalDate.of(2020, 4, 10),
    phoneNumber = "+447838383823"
  )

  lazy val personalRepresentativeIndividualPassportOrIdCardUkAddress : EstatePerRepIndType = EstatePerRepIndType(
    name = NameType("Adam", None, "Conder"),
    dateOfBirth = LocalDate.of(2010, 5, 3),
    identification = IdentificationType(
      nino = None,
      passport = Some(PassportType("1234567890", LocalDate.of(2020, 1, 1), countryOfIssue = "DE", isPassport = None)),
      address = Some(AddressType("lane 1", "lane 2", None, None, Some("NE211ZZ"), "GB"))
    ),
    email = Some("email@test.com"),
    lineNo = "1",
    bpMatchStatus = Some("01"),
    entityStart = LocalDate.of(2020, 4, 10),
    phoneNumber = "+447838383823"
  )

  lazy val personalRepresentativeBusinessUtr : EstatePerRepOrgType = EstatePerRepOrgType(
    orgName = "Company Ltd",
    identification = IdentificationOrgType(
      utr = Some("1234567892"),
      address = None
    ),
    email = None,
    lineNo = "1",
    bpMatchStatus = Some("01"),
    entityStart = LocalDate.of(2020, 4, 10),
    phoneNumber = "+447838383823"
  )

  lazy val personalRepresentativeBusinessWithoutUtrUk : EstatePerRepOrgType = EstatePerRepOrgType(
    orgName = "Company Ltd",
    identification = IdentificationOrgType(
      utr = None,
      address = Some(AddressType("line1", "line2", None, None, Some("NE991ZZ"), "GB"))
    ),
    email = Some("email@test.com"),
    lineNo = "1",
    bpMatchStatus = Some("01"),
    entityStart = LocalDate.of(2020, 4, 10),
    phoneNumber = "+447838383823"
  )

  lazy val personalRepresentativeBusinessWithoutUtrNonUk : EstatePerRepOrgType = EstatePerRepOrgType(
    orgName = "Company Ltd",
    identification = IdentificationOrgType(
      utr = None,
      address = Some(AddressType("line1", "line2", Some("line3"), None, None, "FR"))
    ),
    email = Some("email@test.com"),
    lineNo = "1",
    bpMatchStatus = Some("01"),
    entityStart = LocalDate.of(2020, 4, 10),
    phoneNumber = "+447838383823"
  )

  def fakeGetEstateWithPersonalRep(personalRep: PersonalRepresentativeType, correspondenceAddress: AddressType): GetEstate = GetEstate(
    matchData = MatchData("1234567890"),
    correspondence = Correspondence(
      abroadIndicator = false,
      name = "Estate of person",
      address = correspondenceAddress,
      phoneNumber = "+448282828282"
    ),
    declaration = Declaration(NameType("First", None, "Last"), AddressType("line1", "line2", None, None, None, "GB")),
    estate = Estate(
      administrationEndDate = None,
      periodTaxDues = "01",
      entities = EntitiesType(
        personalRepresentative = personalRep,
        deceased = EstateWillType(
          name = NameType("Deceased", None, "Last"),
          dateOfBirth = Some(LocalDate.of(2010, 5, 3)),
          dateOfDeath = LocalDate.of(2020, 10, 12),
          identification = None,
          lineNo = "1",
          bpMatchStatus = Some("01"),
          entityStart = LocalDate.of(2020, 4, 10)
        )
      )
    )
  )

}