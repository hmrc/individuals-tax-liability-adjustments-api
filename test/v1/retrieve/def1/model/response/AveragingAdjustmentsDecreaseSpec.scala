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

package v1.retrieve.def1.model.response

import api.utils.UnitSpec
import play.api.libs.json.*
import v1.retrieve.def1.model.Def1_RetrieveTaxLiabilityAdjustmentsFixture.averagingAdjustmentsDecrease

class AveragingAdjustmentsDecreaseSpec extends UnitSpec {

  "AveragingAdjustmentsDecrease" when {

    "read from JSON" should {
      "return the parsed object" in {
        val json = Json.obj(
          "incomeTax"       -> 5000.99,
          "class4"          -> 5000.99,
          "capitalGainsTax" -> 5000.99
        )

        json.as[AveragingAdjustmentsDecrease] shouldBe averagingAdjustmentsDecrease
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(averagingAdjustmentsDecrease) shouldBe Json.obj(
          "incomeTax"       -> 5000.99,
          "class4"          -> 5000.99,
          "capitalGainsTax" -> 5000.99
        )
      }
    }

  }

}
