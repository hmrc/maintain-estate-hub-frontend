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

package models

import viewmodels.RadioOption

sealed trait WhatIsNext

object WhatIsNext extends Enumerable.Implicits {

  case object DeclareNewPersonalRep extends WithName("declare") with WhatIsNext

  case object MakeChanges extends WithName("make-changes") with WhatIsNext

  case object CloseEstate extends WithName("close-estate") with WhatIsNext

  val values: List[WhatIsNext] = List(
    DeclareNewPersonalRep, MakeChanges, CloseEstate
  )

  val options: List[(RadioOption, String)] = values.map {
    value =>
      (RadioOption("declarationWhatNext", value.toString), s"declarationWhatNext.${value.toString}.hint")
  }

  implicit val enumerable: Enumerable[WhatIsNext] =
    Enumerable(values.map(v => v.toString -> v): _*)
}