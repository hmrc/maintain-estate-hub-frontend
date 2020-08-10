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

package pages

import base.TestUserAnswers
import models.declaration.{InternationalAddress, UKAddress}
import pages.behaviours.PageBehaviours
import pages.declaration.{AgencyRegisteredAddressInternationalPage, AgencyRegisteredAddressUkPage, AgencyRegisteredAddressUkYesNoPage}

class AgencyRegisteredAddressUkYesNoPageSpec extends PageBehaviours {

  "AgencyRegisteredAddressUkYesNoPage" must {

    beRetrievable[Boolean](AgencyRegisteredAddressUkYesNoPage)

    beSettable[Boolean](AgencyRegisteredAddressUkYesNoPage)

    beRemovable[Boolean](AgencyRegisteredAddressUkYesNoPage)
  }

  "clean up" must {

    "remove international address" in {
      val initial = TestUserAnswers.emptyUserAnswers
        .set(AgencyRegisteredAddressUkYesNoPage, false).success.value
        .set(AgencyRegisteredAddressInternationalPage, InternationalAddress("line1", "line2", country = "FR")).success.value

      val cleaned = initial.set(AgencyRegisteredAddressUkYesNoPage, true).success.value

      cleaned.get(AgencyRegisteredAddressInternationalPage) mustNot be(defined)
    }

    "remove uk address" in {
      val initial = TestUserAnswers.emptyUserAnswers
        .set(AgencyRegisteredAddressUkYesNoPage, true).success.value
        .set(AgencyRegisteredAddressUkPage, UKAddress("line1", "line2", postcode = "postcode")).success.value

      val cleaned = initial.set(AgencyRegisteredAddressUkYesNoPage, false).success.value

      cleaned.get(AgencyRegisteredAddressUkPage) mustNot be(defined)
    }

  }
}
