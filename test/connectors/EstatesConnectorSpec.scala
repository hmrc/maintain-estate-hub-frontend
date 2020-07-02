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

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import generators.Generators
import models.http._
import models.{AddressType, NameType}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Inside, MustMatchers, OptionValues}
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source

class EstatesConnectorSpec extends PlaySpec with MustMatchers
  with OptionValues with Generators with SpecBase with WireMockHelper with ScalaFutures with Inside {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def getEstateUrl(utr: String) : String = s"/estates/$utr"

  "Estates Connector" when {

    "get estate" must {

      "return EstateFound response" in {

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        val utr = "10000000008"

        server.stubFor(
          get(urlEqualTo(getEstateUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(
                  """
                    |{
                    |  "responseHeader": {
                    |    "dfmcaReturnUserStatus": "In Processing",
                    |    "formBundleNo": "1"
                    |  }
                    |}
                    |""".stripMargin
                )
            )
        )

        val result  = Await.result(connector.getEstate(utr), Duration.Inf)
        result mustBe Processing

        application.stop()
      }

      "return NoContent response" in {

        val utr = "6666666666"

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        server.stubFor(
          get(urlEqualTo(getEstateUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.NO_CONTENT)
            )
        )

        val result  = Await.result(connector.getEstate(utr),Duration.Inf)
        result mustBe SorryThereHasBeenAProblem

        application.stop()
      }

      "return NotFound response" in {

        val utr = "10000000008"

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        server.stubFor(
          get(urlEqualTo(getEstateUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.NOT_FOUND)
            )
        )

        val result  = Await.result(connector.getEstate(utr),Duration.Inf)
        result mustBe UtrNotFound

        application.stop()
      }

      "return ServiceUnavailable response" in {

        val utr = "10000000008"

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        server.stubFor(
          get(urlEqualTo(getEstateUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.SERVICE_UNAVAILABLE)
            )
        )

        val result  = Await.result(connector.getEstate(utr), Duration.Inf)
        result mustBe EstatesServiceUnavailable

        application.stop()
      }

      "must return playback data inside a Processed estate" in {
        val utr = "2000000000"
        val payload = Source.fromFile(getClass.getResource("/display-estate.json").getPath).mkString

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstatesConnector]

        server.stubFor(
          get(urlEqualTo(getEstateUrl(utr)))
            .willReturn(okJson(payload))
        )

        val processed = Await.result(connector.getEstate(utr), Duration.Inf)

        inside(processed) {
          case Processed(data, bundleNumber) =>

            bundleNumber mustBe "1"

            data.matchData.utr mustBe "2000000000"

            val correspondence = data.correspondence

            correspondence.abroadIndicator mustBe false
            correspondence.name mustBe "Estates 02"
            correspondence.address mustBe AddressType("Address line 1", "Town or city", None, None, Some("Z99 2YY"), "GB")

            val declaration = data.declaration

            declaration.name mustBe NameType("Alister", Some("Stuart"), "Mc'Govern")
            declaration.address mustBe AddressType("Address line 1", "Address line 2", Some("Address line 3"), Some("Town or city"), Some("Z99 2YY"), "GB")

            val personalRepInd = data.estate.entities.personalRepresentative.estatePerRepInd.get

            personalRepInd.lineNo mustBe "1"
            personalRepInd.bpMatchStatus.get mustBe "01"
            personalRepInd.entityStart mustBe LocalDate.parse("2017-02-28")
            personalRepInd.name mustBe NameType("Alister", None, "Mc'Govern")
            personalRepInd.dateOfBirth mustBe LocalDate.parse("1980-06-01")
            personalRepInd.identification.nino.get mustBe "JS123456A"
            personalRepInd.phoneNumber mustBe "078888888"

            data.estate.entities.personalRepresentative.estatePerRepOrg mustBe None

            val deceased = data.estate.entities.deceased

            deceased.lineNo mustBe "1"
            deceased.bpMatchStatus.get mustBe "01"
            deceased.entityStart mustBe LocalDate.parse("2017-02-28")
            deceased.name mustBe NameType("Wilbert", None, "Jefferies")
            deceased.dateOfDeath mustBe LocalDate.parse("2016-04-06")
            deceased.identification.get.nino.get mustBe "JS123456A"

            data.estate.administrationEndDate.get mustBe LocalDate.parse("2017-06-01")

            data.estate.periodTaxDues mustBe "01"
        }

        application.stop()
      }
    }
  }

}
