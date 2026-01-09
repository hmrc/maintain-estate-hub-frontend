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

package forms

import forms.behaviours.StringFieldBehaviours
import forms.declaration.InternationalAddressFormProvider
import models.declaration.InternationalAddress
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class InternationalAddressFormProviderSpec extends StringFieldBehaviours {

  private val prefix: String = "internationalAddress.error"

  private val form = new InternationalAddressFormProvider()()

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = s"$prefix.$fieldName.required"
    val lengthKey = s"$prefix.$fieldName.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

  ".line2" must {

    val fieldName = "line2"
    val requiredKey = s"$prefix.$fieldName.required"
    val lengthKey = s"$prefix.$fieldName.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

  ".line3" must {

    val fieldName = "line3"
    val lengthKey = s"$prefix.$fieldName.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.addressLineRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "  line3  ", "country" -> "country"))
      result.value.value.line3 shouldBe Some("line3")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "  ", "country" -> "country"))
      result.value.value.line3 shouldBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "", "country" -> "country"))
      result.value.value.line3 shouldBe None
    }
  }

  "all lines excluding country" must {
    "bind whitespace, trim text, and replace smart apostrophes with single quotes" in {
      val smartApostrophesOpen= '‘'
      val smartApostrophesClose= '’'

      val testAddressLine =  s"   ${smartApostrophesOpen}TestAddressLine${smartApostrophesClose}  "

      val result = form.bind(
        Map("line1" -> testAddressLine, "line2" -> testAddressLine, "line3" -> testAddressLine, "country" -> "NL")
      )

      result.value.value shouldBe InternationalAddress("'TestAddressLine'", "'TestAddressLine'", Some("'TestAddressLine'"), "NL")
    }
  }

  ".country" must {

    val fieldName = "country"
    val requiredKey = s"$prefix.$fieldName.required"
    val lengthKey = s"$prefix.$fieldName.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

}
