/*
 * Copyright 2024 HM Revenue & Customs
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

package printers

import base.{FakeData, SpecBase}
import models.Correspondence
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class EstateNamePrinterSpec extends SpecBase {

  "estate name printer" must {

      "print" in {
        val printer = injector.instanceOf[EstateNamePrinter]

        val result = printer.name(Correspondence(
          abroadIndicator = false,
          name = "Estate of person",
          address = FakeData.correspondenceAddressUk,
          phoneNumber = "+448282828282"
        ))

        result.value mustBe AnswerSection(
          headingKey = Some("Estate name"),
          rows = Seq(
            AnswerRow("What is the estate’s name?", Html("Estate of person"))
          ),
          sectionKey = Some("Estate name")
        )
      }
    }
}
