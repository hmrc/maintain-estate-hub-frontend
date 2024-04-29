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

package models.declaration

import java.time.LocalDate

import models.{AddressType, NameType}
import play.api.libs.json.{Format, Json}

case class AgentDetails(arn: String,
                        agentName: String,
                        agentAddress: AddressType,
                        agentTelephoneNumber: String,
                        clientReference: String)

object AgentDetails {
  implicit val agentDetailsFormat: Format[AgentDetails] = Json.format[AgentDetails]
}

case class Declaration(name: NameType)

object Declaration {
  implicit val declarationFormat: Format[Declaration] = Json.format[Declaration]
}

case class DeclarationForApi(declaration: Declaration,
                             agentDetails: Option[AgentDetails],
                             endDate: Option[LocalDate])

object DeclarationForApi {
  implicit val declarationForApiFormat: Format[DeclarationForApi] = Json.format[DeclarationForApi]
}
