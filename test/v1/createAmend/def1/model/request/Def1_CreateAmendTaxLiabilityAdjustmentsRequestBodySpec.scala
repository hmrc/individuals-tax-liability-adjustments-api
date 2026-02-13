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

package v1.createAmend.def1.model.request

import api.utils.UnitSpec
import play.api.libs.json.{JsError, JsValue, Json}

class Def1_CreateAmendTaxLiabilityAdjustmentsRequestBodySpec extends UnitSpec {

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "carryBackLossesDecrease": {
      |    "incomeTax": 5000.99,
      |    "class4": 5000.99,
      |    "capitalGainsTax": 5000.99
      |  },
      |  "averagingAdjustmentsDecrease": {
      |    "incomeTax": 5000.99,
      |    "class4": 5000.99,
      |    "capitalGainsTax": 5000.99
      |  }
      |}
      |""".stripMargin
  )

  val invalidJson: JsValue = Json.parse(
    """
      |
      |{
      |  "carryBackLossesDecrease": {
      |    "incomeTax": true,
      |    "class4": true,
      |    "capitalGainsTax": true
      |  },
      |  "averagingAdjustmentsDecrease": {
      |     "incomeTax": true,
      |     "class4": true,
      |     "capitalGainsTax": true
      |   }
      |}
      |""".stripMargin
  )

  val averagingAdjustmentsDecrease: AveragingAdjustmentsDecrease =
    AveragingAdjustmentsDecrease(
      incomeTax = Some(5000.99),
      class4 = Some(5000.99),
      capitalGainsTax = Some(5000.99)
    )

  val carryBackLossesDecrease: CarryBackLossesDecrease =
    CarryBackLossesDecrease(
      incomeTax = Some(5000.99),
      class4 = Some(5000.99),
      capitalGainsTax = Some(5000.99)
    )

  val mtdRequestBody: Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody =
    Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody(
      carryBackLossesDecrease = Some(carryBackLossesDecrease),
      averagingAdjustmentsDecrease = Some(averagingAdjustmentsDecrease)
    )

  "Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody" when {
    "read from valid Json" should {
      "produce the expected object" in {
        mtdJson.as[Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody] shouldBe mtdRequestBody
      }
    }

    "read from invalid Json" should {
      "provide a JsError" in {
        invalidJson.validate[Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody] shouldBe a[JsError]
      }
    }

    "written to Json" should {
      "produce the expected JsObject" in {
        Json.toJson(mtdRequestBody) shouldBe mtdJson
      }
    }
  }

}
