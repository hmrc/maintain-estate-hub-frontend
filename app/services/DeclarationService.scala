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

package services

import com.google.inject.{ImplementedBy, Inject}
import connectors.EstatesConnector
import models.declaration._
import models.requests.AgentRequestWithAddress
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DeclarationServiceImpl @Inject()(connector: EstatesConnector) extends DeclarationService {

  override def declare(utr: String, declaration: IndividualDeclaration)
                      (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[VariationResponse] = {

    val payload = DeclarationForApi(
      declaration = Declaration(declaration.name),
      agentDetails = None,
      endDate = None
    )

    connector.declare(utr, Json.toJson(payload))
  }

  override def declare(agentRequest: AgentRequestWithAddress[_],
                       declaration: AgentDeclaration)
                      (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[VariationResponse] = {

    import models.Implicits._

    val arn = agentRequest.user.agentReferenceNumber
    val address = agentRequest.address
    val utr = agentRequest.utr
    val agencyName = declaration.agencyName
    val tel = declaration.telephoneNumber
    val crn = declaration.crn

    val agentDetails = AgentDetails(arn, agencyName, address.convert, tel, crn)

    val payload = DeclarationForApi(
      declaration = Declaration(declaration.name),
      agentDetails = Some(agentDetails),
      endDate = None
    )

    connector.declare(utr, Json.toJson(payload))
  }
}

@ImplementedBy(classOf[DeclarationServiceImpl])
trait DeclarationService {

  def declare(utr: String, declaration: IndividualDeclaration)
             (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[VariationResponse]

  def declare(agentRequest: AgentRequestWithAddress[_],
              declaration: AgentDeclaration)
             (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[VariationResponse]
}