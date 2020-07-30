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

package forms.declaration

import com.google.inject.Inject
import forms.Validation
import forms.mappings.Mappings
import models.NameType
import play.api.data.Forms.{mapping, optional}
import play.api.data.{Form, Mapping}

class DeclarationFormProvider @Inject()() extends Mappings {

  private val fullName: Mapping[NameType] = mapping(
    "firstName" -> text("declaration.error.firstName.required")
      .verifying(
        firstError(
          maxLength(35, s"declaration.error.firstName.length"),
          isNotEmpty("firstName", s"declaration.error.firstName.required"),
          regexp(Validation.nameRegex, s"declaration.error.firstName.invalid")
        )),
    "middleName" -> optional(text()
      .verifying(
        firstError(
          maxLength(35, s"declaration.error.middleName.length"),
          regexp(Validation.nameRegex, s"declaration.error.middleName.invalid"))
      )),
    "lastName" -> text("declaration.error.lastName.required")
      .verifying(
        firstError(
          maxLength(35, s"declaration.error.lastName.length"),
          isNotEmpty("lastName", s"declaration.error.lastName.required"),
          regexp(Validation.nameRegex, s"declaration.error.lastName.invalid")
        ))
  )(NameType.apply)(NameType.unapply)

  def apply(): Form[models.declaration.Declaration] =
    Form(
      mapping(
        "name" -> fullName,
        "email" -> optional(text().verifying(
          firstError(
            maxLength(35, s"declaration.error.email.length"),
            regexp(Validation.emailRegex, s"declaration.error.email.invalid"))
        ))
      )(models.declaration.Declaration.apply)(models.declaration.Declaration.unapply)
    )
}
