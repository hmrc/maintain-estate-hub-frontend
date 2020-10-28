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

package models

import play.api.Logger
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import play.api.libs.json.{Json, OFormat}
import utils.Session

import scala.language.implicitConversions

case class EstateLock(utr:String, managedByAgent: Boolean, estateLocked:Boolean)

object EstateLock {
  private val logger: Logger = Logger(getClass)

  implicit val formats : OFormat[EstateLock] = Json.format[EstateLock]

  implicit def httpReads(utr : String)(implicit hc: HeaderCarrier): HttpReads[Option[EstateLock]] =
    (method: String, url: String, response: HttpResponse) => {
      logger.info(s"[Session ID: ${Session.id(hc)}] response status received from estates store api: ${response.status}")

      response.status match {
        case OK =>
          response.json.asOpt[EstateLock] match {
            case validClaim@Some(c) =>
              if (c.utr.toLowerCase.trim == utr.toLowerCase.trim) {
                validClaim
              } else {
                None
              }
            case None => None
          }
        case _ =>
          None
      }
    }


}