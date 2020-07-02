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

package models.http

import models.GetEstate
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

sealed trait EstateResponse

sealed trait EstateStatus extends EstateResponse

case object Processing extends EstateStatus
case object Closed extends EstateStatus
case class Processed(playback: GetEstate, formBundleNumber : String) extends EstateStatus
case object SorryThereHasBeenAProblem extends EstateStatus
case object UtrNotFound extends EstateResponse
case object ServiceUnavailable extends EstateResponse
case object ServerError extends EstateResponse

object EstateStatusReads {

  implicit object StatusReads extends Reads[EstateStatus] {
    override def reads(json:JsValue): JsResult[EstateStatus] = json("responseHeader")("dfmcaReturnUserStatus") match {
      case JsString("In Processing") => JsSuccess(Processing)
      case JsString("Closed") => JsSuccess(Closed)
      case JsString("Pending Closure") => JsSuccess(Closed)
      case JsString("Processed") =>
         json("trustOrEstateDisplay").validate[GetEstate] match {
          case JsSuccess(estate, _) =>
            val formBundle = json("responseHeader")("formBundleNo").as[String]
            JsSuccess(Processed(estate, formBundle))
          case JsError(errors) => JsError(s"Can not parse as GetEstate due to $errors")
        }
      case JsString("Parked") => JsSuccess(SorryThereHasBeenAProblem)
      case JsString("Obsoleted") => JsSuccess(SorryThereHasBeenAProblem)
      case JsString("Suspended") => JsSuccess(SorryThereHasBeenAProblem)
      case _ => JsError("Unexpected Status")
    }
  }

  implicit lazy val httpReads: HttpReads[EstateResponse] =
    new HttpReads[EstateResponse] {
      override def read(method: String, url: String, response: HttpResponse): EstateResponse = {
        Logger.info(s"[EstateStatusReads] response status received from estates status api: ${response.status}")

        response.status match {
          case OK =>
            response.json.as[EstateStatus]
          case NO_CONTENT =>
            SorryThereHasBeenAProblem
          case NOT_FOUND =>
            UtrNotFound
          case SERVICE_UNAVAILABLE =>
            ServiceUnavailable
          case _ =>
            ServerError
        }
      }
    }
}
