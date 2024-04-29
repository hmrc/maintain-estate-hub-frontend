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

package pages

import java.time.LocalDate

import models.{UserAnswers, WhatIsNext}
import models.WhatIsNext._
import pages.behaviours.PageBehaviours
import pages.closure.{AdministrationPeriodEndDatePage, ChangePersonalRepDetailsYesNoPage, HasAdministrationPeriodEndedYesNoPage}

class WhatIsNextPageSpec extends PageBehaviours {

  "WhatIsNextPage" must {

    beRetrievable[WhatIsNext](WhatIsNextPage)

    beSettable[WhatIsNext](WhatIsNextPage)

    beRemovable[WhatIsNext](WhatIsNextPage)

    "implement cleanup logic" when {

      val baseAnswers: UserAnswers = emptyUserAnswers
        .set(WhatIsNextPage, CloseEstate).success.value
        .set(HasAdministrationPeriodEndedYesNoPage, true).success.value
        .set(AdministrationPeriodEndDatePage, LocalDate.parse("2020-01-01")).success.value
        .set(ChangePersonalRepDetailsYesNoPage, true).success.value

      "DeclareNewPersonalRep selected" in {

        val userAnswers = baseAnswers
          .set(WhatIsNextPage, DeclareNewPersonalRep).success.value

        userAnswers.get(HasAdministrationPeriodEndedYesNoPage) mustNot be(defined)
        userAnswers.get(AdministrationPeriodEndDatePage) mustNot be(defined)
        userAnswers.get(ChangePersonalRepDetailsYesNoPage) mustNot be(defined)
      }

      "MakeChanges selected" in {

        val userAnswers = baseAnswers
          .set(WhatIsNextPage, MakeChanges).success.value

        userAnswers.get(HasAdministrationPeriodEndedYesNoPage) mustNot be(defined)
        userAnswers.get(AdministrationPeriodEndDatePage) mustNot be(defined)
        userAnswers.get(ChangePersonalRepDetailsYesNoPage) mustNot be(defined)
      }
    }
  }
}
