package models

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class EntitiesType(personalRepresentative: PersonalRepresentativeType,
                        deceased: EstateWillType)

object EntitiesType {
  implicit val entitiesTypeFormat: Format[EntitiesType] = Json.format[EntitiesType]
}

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
                          identification: IdentificationType,
                          lineNo: String,
                          bpMatchStatus: Option[String],
                          entityStart: LocalDate)

object EstateWillType {
  implicit val writes: Writes[EstateWillType] = Json.writes[EstateWillType]

  implicit val reads: Reads[EstateWillType] =
    ((__ \ 'name).read[NameType] and
      (__ \ 'dateOfBirth).readNullable[LocalDate] and
      (__ \ 'dateOfDeath).read[LocalDate] and
      (__ \ 'identification).readNullable[IdentificationType] and
      (__ \ 'lineNo).read[String] and
      (__ \ 'bpMatchStatus).readNullable[String] and
      (__ \ "entityStart").read[LocalDate]).tupled.map{

      case (name, dob, dod, Some(identification), lineNo, bpMatchStatus, entityStart) =>
        EstateWillType(name, dob, dod, identification, lineNo, bpMatchStatus, entityStart)
      case (name, dob, dod, None, lineNo, bpMatchStatus, entityStart) =>
        EstateWillType(name, dob, dod, IdentificationType(None, None, None), lineNo, bpMatchStatus, entityStart)

    }
}
