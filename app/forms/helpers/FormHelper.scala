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

package forms.helpers

object FormHelper {

  val emptyToNone: Option[String] => Option[String] = _.filter(_.nonEmpty)

  private val smartApostrophesOpen: Char = '‘'
  private val smartApostrophesClose: Char = '’'
  private val singleQuote: Char = '\''

  def replaceSmartApostrophesAndTrim(input: String) : String = {
    input.collect {
      case char if char == smartApostrophesOpen | char == smartApostrophesClose => singleQuote
      case char => char
    }.trim
  }

}