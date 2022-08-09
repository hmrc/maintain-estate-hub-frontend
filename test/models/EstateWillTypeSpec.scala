/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.LocalDate

import base.SpecBase
import play.api.libs.json.Json

class EstateWillTypeSpec extends SpecBase {

  "EstateWillType model" must {

    "read json with no identification as IdentificationType(None, None, None)" in {

      val json =
        """
          |{
          | "lineNo": "1",
          | "bpMatchStatus": "01",
          | "entityStart": "2017-02-28",
          | "name" : {
          |   "firstName": "Wilbert",
          |   "lastName": "Jefferies"
          | },
          | "dateOfDeath": "2016-04-06"
          |}
          |""".stripMargin

      val deceasedPerson = Json.parse(json).validate[EstateWillType].get

      deceasedPerson.identification mustBe IdentificationType(None, None, None)
      deceasedPerson.lineNo mustBe "1"
      deceasedPerson.bpMatchStatus.get mustBe "01"
      deceasedPerson.entityStart mustBe LocalDate.parse("2017-02-28")
      deceasedPerson.name mustBe NameType("Wilbert", None, "Jefferies")
      deceasedPerson.dateOfDeath mustBe LocalDate.parse("2016-04-06")
    }

    "read json with identification" in {

      val json =
        """
          |{
          | "lineNo": "1",
          | "bpMatchStatus": "01",
          | "entityStart": "2017-02-28",
          | "name" : {
          |   "firstName": "Wilbert",
          |   "lastName": "Jefferies"
          | },
          | "dateOfDeath": "2016-04-06",
          | "dateOfBirth": "1996-04-06",
          | "identification": {
          |   "nino": "AA000000A"
          | }
          |}
          |""".stripMargin

      val deceasedPerson = Json.parse(json).validate[EstateWillType].get

      deceasedPerson.identification mustBe IdentificationType(Some("AA000000A"), None, None)
      deceasedPerson.lineNo mustBe "1"
      deceasedPerson.bpMatchStatus.get mustBe "01"
      deceasedPerson.entityStart mustBe LocalDate.parse("2017-02-28")
      deceasedPerson.name mustBe NameType("Wilbert", None, "Jefferies")
      deceasedPerson.dateOfDeath mustBe LocalDate.parse("2016-04-06")
      deceasedPerson.dateOfBirth.get mustBe LocalDate.parse("1996-04-06")
    }
  }
}
