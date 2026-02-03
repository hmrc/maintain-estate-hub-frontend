/*
 * Copyright 2026 HM Revenue & Customs
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
import models.http.{AuthInternalServerError, EstatesAuthResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[EstatesAuthConnectorImpl])
trait EstatesAuthConnector {
  def agentIsAuthorised(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstatesAuthResponse]
  def authorisedForUtr(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstatesAuthResponse]
}

class EstatesAuthConnectorImpl @Inject() (http: HttpClientV2, config: FrontendAppConfig) extends EstatesAuthConnector {

  val baseUrl: String = config.estatesAuthUrl + "/estates-auth"

  override def agentIsAuthorised(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstatesAuthResponse] = {
    val fullUrl = s"$baseUrl/agent-authorised"
    http.get(url"$fullUrl").execute[EstatesAuthResponse].recoverWith { case _ =>
      Future.successful(AuthInternalServerError)
    }
  }

  override def authorisedForUtr(
    utr: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstatesAuthResponse] = {
    val fullUrl = s"$baseUrl/authorised/$utr"
    http.get(url"$fullUrl").execute[EstatesAuthResponse].recoverWith { case _ =>
      Future.successful(AuthInternalServerError)
    }
  }

}
