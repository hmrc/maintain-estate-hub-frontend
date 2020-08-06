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

import java.time.LocalDate

import config.FrontendAppConfig
import javax.inject.Inject
import models.declaration.VariationResponse
import models.http.{EstateResponse, EstateStatusReads}
import play.api.libs.json.{JsSuccess, JsValue, Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class EstatesConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private def getEstateUrl(utr: String) = s"${config.estatesUrl}/estates/$utr"

  private def getTransformedEstateUrl(utr: String) = s"${config.estatesUrl}/estates/$utr/transformed"

  private def declareUrl(utr: String) = s"${config.estatesUrl}/estates/declare/$utr"

  private def closeUrl(utr: String) = s"${config.estatesUrl}/estates/close/$utr"

  private def getDateOfDeathUrl(utr: String) = s"${config.estatesUrl}/estates/$utr/date-of-death"

  private def getCloseDateUrl(utr: String) = s"${config.estatesUrl}/estates/$utr/close-date"

  def getEstate(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstateResponse] = {
    http.GET[EstateResponse](getEstateUrl(utr))(EstateStatusReads.httpReads, hc, ec)
  }

  def getTransformedEstate(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstateResponse] = {
    http.GET[EstateResponse](getTransformedEstateUrl(utr))(EstateStatusReads.httpReads, hc, ec)
  }

  def declare(utr: String, payload: JsValue)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[VariationResponse] = {
    http.POST[JsValue, VariationResponse](declareUrl(utr), payload)(implicitly[Writes[JsValue]], VariationResponse.httpReads, hc, ec)
  }

  def close(utr: String, closeDate: LocalDate)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](closeUrl(utr), Json.toJson(closeDate))
  }

  def getDateOfDeath(utr: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[LocalDate] = {
    http.GET[JsValue](getDateOfDeathUrl(utr)).map(_.validate[LocalDate] match {
      case JsSuccess(dateOfDeath, _) => dateOfDeath
      case _ => config.minDate
    })
  }

  def getCloseDate(utr: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Option[LocalDate]] = {
    http.GET[JsValue](getCloseDateUrl(utr)).map(_.validate[LocalDate] match {
      case JsSuccess(closeDate, _) => Some(closeDate)
      case _ => None
    })
  }

}
