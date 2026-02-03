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

import config.FrontendAppConfig
import models.declaration.VariationResponse
import models.http.{EstateResponse, EstateStatusReads}
import play.api.libs.json.{JsSuccess, JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EstatesConnector @Inject() (http: HttpClientV2, config: FrontendAppConfig) {

  private def getEstateUrl(utr: String) = s"${config.estatesUrl}/estates/$utr"

  private def getTransformedEstateUrl(utr: String) = s"${config.estatesUrl}/estates/$utr/transformed"

  private def declareUrl(utr: String) = s"${config.estatesUrl}/estates/declare/$utr"

  private def closeUrl(utr: String) = s"${config.estatesUrl}/estates/close/$utr"

  private def getDateOfDeathUrl(utr: String) = s"${config.estatesUrl}/estates/$utr/date-of-death"

  private def clearTransformationsUrl(utr: String) = s"${config.estatesUrl}/estates/$utr/clear-transformations"

  def getEstate(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstateResponse] = {
    val fullUrl = getEstateUrl(utr)
    http.get(url"$fullUrl").execute[EstateResponse](EstateStatusReads.httpReads, ec)
  }

  def getTransformedEstate(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstateResponse] = {
    val fullUrl = getTransformedEstateUrl(utr)
    http.get(url"$fullUrl").execute[EstateResponse](EstateStatusReads.httpReads, ec)
  }

  def declare(utr: String, payload: JsValue)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[VariationResponse] = {
    val fullUrl = declareUrl(utr)
    http.post(url"$fullUrl").withBody(payload).execute[VariationResponse](VariationResponse.httpReads, ec)
  }

  def close(
    utr: String,
    closeDate: LocalDate
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = closeUrl(utr)
    http.post(url"$fullUrl").withBody(Json.toJson(closeDate)).execute[HttpResponse]
  }

  def getDateOfDeath(utr: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[LocalDate] = {
    val fullUrl = getDateOfDeathUrl(utr)

    http
      .get(url"$fullUrl")
      .execute[JsValue]
      .map(_.validate[LocalDate] match {
        case JsSuccess(dateOfDeath, _) => dateOfDeath
        case _                         => config.minDate
      })
  }

  def clearTransformations(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = clearTransformationsUrl(utr)
    http.post(url"$fullUrl").execute[HttpResponse]
  }

}
