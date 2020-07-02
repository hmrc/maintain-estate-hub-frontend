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

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{get, okJson, urlEqualTo, _}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models.EstateLock
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.http.HeaderNames._
import play.api.http.MimeTypes._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

class EstatesStoreConnectorSpec extends SpecBase with WireMockHelper with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "estates store connector" must {

    "return OK with the current lock status" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesStoreConnector]

      val json = Json.parse(
        """
          |{
          | "utr":"123456789",
          | "managedByAgent":false,
          | "estateLocked":false
          |}
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates-store/lock"))
          .willReturn(okJson(json.toString))
      )

      val result = connector.get("123456789")

      result.futureValue mustBe
        Some(EstateLock(utr = "123456789", managedByAgent = false, estateLocked = false))

      application.stop()
    }
  }

}
