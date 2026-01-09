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

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import models.http.{AuthAgentAllowed, AuthAllowed, AuthDenied, AuthInternalServerError}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.test.DefaultAwaitTimeout
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

class EstatesAuthConnectorSpec extends AsyncFreeSpec with Matchers with WireMockHelper with DefaultAwaitTimeout {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val authorisedUrl: String = "/estates-auth/agent-authorised"
  private def authorisedUrlFor(utr: String): String = s"/estates-auth/authorised/$utr"

  private def responseFromJson(json: JsValue) = {
    aResponse().withStatus(Status.OK).withBody(json.toString())
  }

  private def allowedResponse = responseFromJson(Json.obj("authorised" -> true))
  private def allowedAgentResponse = responseFromJson(Json.obj("arn" -> "SomeArn"))

  private val redirectResponse = responseFromJson(Json.obj("redirectUrl" -> "redirect-url"))

  private def wireMock(url: String, response: ResponseDefinitionBuilder) = {
    server.stubFor(get(urlEqualTo(url)).willReturn(response))
  }

  lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.estates-auth.port" -> server.port(),
      "auditing.enabled" -> false
    ): _*).build()

  private lazy val connector = app.injector.instanceOf[EstatesAuthConnector]

  private val utr = "0123456789"

  "EstatesAuthConnector" - {

    "authorisedForUtr" - {

      "returns 'Allowed' when" - {
        "service returns with no redirect url" in {

          wireMock(authorisedUrlFor(utr), allowedResponse)

          connector.authorisedForUtr(utr) map { result =>
            result mustEqual AuthAllowed()
          }
        }
      }

      "returns 'Denied' when" - {
        "service returns a redirect url" in {

          wireMock(authorisedUrlFor(utr), redirectResponse)

          connector.authorisedForUtr(utr) map { result =>
            result mustEqual AuthDenied("redirect-url")
          }
        }
      }

      "returns 'Internal server error' when" - {
        "service returns something not OK" in {

          wireMock(authorisedUrlFor(utr), aResponse().withStatus(Status.INTERNAL_SERVER_ERROR))

          connector.authorisedForUtr(utr) map { result =>
            result mustEqual AuthInternalServerError
          }
        }
      }
    }

    "authorised" - {

      "returns 'Agent Allowed' when" - {
        "service returns with agent authorised response" in {

          wireMock(authorisedUrl, allowedAgentResponse)

          connector.agentIsAuthorised map { result =>
            result mustEqual AuthAgentAllowed("SomeArn")
          }
        }
      }

      "returns 'Denied' when" - {
        "service returns a redirect url" in {

          wireMock(authorisedUrl, redirectResponse)

          connector.agentIsAuthorised map { result =>
            result mustEqual AuthDenied("redirect-url")
          }
        }
      }

      "returns 'Internal server error' when" - {
        "service returns something not OK" in {

          wireMock(authorisedUrl, aResponse().withStatus(Status.INTERNAL_SERVER_ERROR))

          connector.agentIsAuthorised map { result =>
            result mustEqual AuthInternalServerError
          }
        }
      }
    }
  }
}
