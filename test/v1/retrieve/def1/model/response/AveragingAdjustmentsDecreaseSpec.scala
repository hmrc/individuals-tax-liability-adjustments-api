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
import v1.retrieve.def1.model.Def1_RetrieveTaxLiabilityAdjustmentsFixture

class AveragingAdjustmentsDecreaseSpec extends UnitSpec with Def1_RetrieveTaxLiabilityAdjustmentsFixture {

  "AveragingAdjustmentsDecrease" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(averagingAdjustmentsDecrease)

      json shouldBe Json.obj(
        "incomeTax"       -> 5000.99,
        "class4"          -> 5000.99,
        "capitalGainsTax" -> 5000.99
      )
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "incomeTax"       -> 5000.99,
        "class4"          -> 5000.99,
        "capitalGainsTax" -> 5000.99
      )

      val result = json.validate[AveragingAdjustmentsDecrease]

      result.isSuccess shouldBe true
      result.get shouldBe averagingAdjustmentsDecrease
    }

    "round-trip serialize and deserialize" in {
      val json   = Json.toJson(averagingAdjustmentsDecrease)
      val parsed = json.as[AveragingAdjustmentsDecrease]

      parsed shouldBe averagingAdjustmentsDecrease
    }

  }

}
