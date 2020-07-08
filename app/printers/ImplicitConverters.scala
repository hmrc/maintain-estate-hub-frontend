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

package printers

import viewmodels.{AnswerRow, AnswerSection}

object ImplicitConverters {

  implicit class AnswerSectionToOption(answerRow: AnswerSection) {
    def toOption = Some(answerRow)
  }

  implicit class AnswerRowToOption(answerRow: AnswerRow) {
    def toOption = Some(answerRow)
  }

}
