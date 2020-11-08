package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json}

case class IdentificationType(nino: Option[String],
                              passport: Option[PassportType],
                              address: Option[AddressType])

object IdentificationType {
  implicit val identificationTypeFormat: Format[IdentificationType] = Json.format[IdentificationType]
}

case class IdentificationOrgType(utr: Option[String],
                                 address: Option[AddressType])

object IdentificationOrgType {
  implicit val identificationOrgTypeFormat: Format[IdentificationOrgType] = Json.format[IdentificationOrgType]
}

case class PassportType(number: String,
                        expirationDate: LocalDate,
                        countryOfIssue: String,
                        isPassport: Option[Boolean] = None)

object PassportType {
  implicit val passportTypeFormat: Format[PassportType] = Json.format[PassportType]
}

case class AddressType(line1: String,
                       line2: String,
                       line3: Option[String],
                       line4: Option[String],
                       postCode: Option[String],
                       country: String)

object AddressType {

  def isUK(addressType: AddressType): Boolean = addressType.country.toUpperCase == "GB"

  implicit val addressTypeFormat: Format[AddressType] = Json.format[AddressType]
}

case class NameType(firstName: String,
                    middleName: Option[String],
                    lastName: String) {

  def fullName : String = {
    val middle = middleName.map(" " + _ + " ").getOrElse(" ")
    s"$firstName$middle$lastName"
  }

  def displayName: String = s"$firstName $lastName"

}

object NameType {
  implicit val nameTypeFormat: Format[NameType] = Json.format[NameType]
}
