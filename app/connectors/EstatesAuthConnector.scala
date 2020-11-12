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

package connectors

import com.google.inject.ImplementedBy
import config.FrontendAppConfig
import javax.inject.Inject
import models.http.{AuthInternalServerError, EstatesAuthResponse}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[EstatesAuthConnectorImpl])
trait EstatesAuthConnector {
  def agentIsAuthorised(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstatesAuthResponse]
  def authorisedForUtr(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstatesAuthResponse]
}

class EstatesAuthConnectorImpl @Inject()(http: HttpClient, config: FrontendAppConfig)
  extends EstatesAuthConnector {

  val baseUrl: String = config.estatesAuthUrl + "/estates-auth"

  override def agentIsAuthorised(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstatesAuthResponse] = {
    http.GET[EstatesAuthResponse](s"$baseUrl/agent-authorised").recoverWith {
      case _ => Future.successful(AuthInternalServerError)
    }
  }

  override def authorisedForUtr(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstatesAuthResponse] = {
    http.GET[EstatesAuthResponse](s"$baseUrl/authorised/$utr").recoverWith {
      case _ => Future.successful(AuthInternalServerError)
    }
  }
}
