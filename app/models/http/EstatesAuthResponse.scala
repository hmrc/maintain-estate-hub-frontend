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

package models.http

import play.api.libs.json.{Format, Json, Reads, __}

sealed trait EstatesAuthResponse
object EstatesAuthResponse {
  implicit val reads: Reads[EstatesAuthResponse] =
    __.read[AuthAllowed].widen[EstatesAuthResponse] orElse
      __.read[AuthAgentAllowed].widen[EstatesAuthResponse] orElse
      __.read[AuthDenied].widen[EstatesAuthResponse]
}

case class AuthAllowed(authorised: Boolean = true) extends EstatesAuthResponse
case object AuthAllowed {
  implicit val format: Format[AuthAllowed] = Json.format[AuthAllowed]
}

case class AuthAgentAllowed(arn: String) extends EstatesAuthResponse
case object AuthAgentAllowed {
  implicit val format: Format[AuthAgentAllowed] = Json.format[AuthAgentAllowed]
}

case class AuthDenied(redirectUrl: String) extends EstatesAuthResponse
case object AuthDenied {
  implicit val format: Format[AuthDenied] = Json.format[AuthDenied]
}

case object AuthInternalServerError extends EstatesAuthResponse
