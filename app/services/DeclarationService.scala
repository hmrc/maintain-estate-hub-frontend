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

package services

import com.google.inject.{ImplementedBy, Inject}
import connectors.EstatesConnector
import models.declaration.{Declaration, VariationResponse}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DeclarationServiceImpl @Inject()(connector: EstatesConnector) extends DeclarationService {

  override def declare(utr: String, declaration: Declaration)
                      (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[VariationResponse] = {
    connector.declare(utr, declaration.toJson)
  }
}

@ImplementedBy(classOf[DeclarationServiceImpl])
trait DeclarationService {

  def declare(utr: String, declaration: Declaration)
             (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[VariationResponse]
}