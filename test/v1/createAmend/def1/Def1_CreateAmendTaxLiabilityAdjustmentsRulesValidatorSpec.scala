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

package v1.createAmend.def1

import api.models.domain.{Nino, TaxYear}
import api.models.errors.{ErrorWrapper, MtdError, NinoFormatError, RuleIncorrectOrEmptyBodyError}
import api.utils.UnitSpec
import play.api.libs.json.{JsObject, JsValue, Json}
import v1.createAmend.def1.model.request.{Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody, Def1_CreateAmendTaxLiabilityAdjustmentsRequestData}

class Def1_CreateAmendTaxLiabilityAdjustmentsRulesValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "someCorrelationId"

  private val validNino    = "AA123456A"
  private val validTaxYear = "2026-27"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  private val validRequestJson: JsValue =
    Json.parse(
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

  val invalidRequestJsonIncorrectTypes: JsValue =
    Json.parse(
      """
        |{
        |  "carryBackLossesDecrease": {
        |    "incomeTax": true,
        |    "class4": true,
        |    "capitalGainsTax": true
        |  },
        |  "averagingAdjustmentsDecrease": {
        |    "incomeTax": true,
        |    "class4": true,
        |    "capitalGainsTax": true
        |  }
        |}
        |""".stripMargin
    )

  val invalidRequestJsonEmptyFields: JsValue =
    Json.parse(
      """
        |{
        |  "carryBackLossesDecrease": { },
        |  "averagingAdjustmentsDecrease": { }
        |}
        |""".stripMargin
    )

  private def validate(nino: String = validNino, taxYear: String = validTaxYear, body: JsValue = validRequestJson) =
    new Def1_CreateAmendTaxLiabilityAdjustmentsValidator(nino, taxYear, body).validateAndWrapResult()

  private def error(mtdError: MtdError) = Left(ErrorWrapper(correlationId, mtdError))

  "Def1_CreateAmendTaxLiabilityAdjustmentsRulesValidator" should {
    "return the parsed object" when {
      "a valid request is supplied" in {
        validate(validNino, validTaxYear, validRequestJson) shouldBe
          Right(
            Def1_CreateAmendTaxLiabilityAdjustmentsRequestData(
              parsedNino,
              parsedTaxYear,
              validRequestJson.as[Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody]
            ))
      }
    }

    "return a NinoFormatError" when {
      "an invalid nino is supplied" in {
        validate(nino = "A12344A") shouldBe error(NinoFormatError)
      }
    }

    "return a RuleIncorrectOrEmptyBodyError" when {
      "an empty Json body is submitted" in {
        validate(body = JsObject.empty) shouldBe error(RuleIncorrectOrEmptyBodyError)
      }

      "a non-empty JSON body is submitted without any expected fields" in {
        validate(body = Json.parse("""{"field": "value"}""")) shouldBe error(RuleIncorrectOrEmptyBodyError)
      }

      "the submitted request body has fields with incorrect type" in {
        validate(body = invalidRequestJsonIncorrectTypes) shouldBe error(
          RuleIncorrectOrEmptyBodyError.withPaths(
            Seq(
              "/averagingAdjustmentsDecrease/capitalGainsTax",
              "/averagingAdjustmentsDecrease/class4",
              "/averagingAdjustmentsDecrease/incomeTax",
              "/carryBackLossesDecrease/capitalGainsTax",
              "/carryBackLossesDecrease/class4",
              "/carryBackLossesDecrease/incomeTax"
            )
          )
        )
      }

      "the submitted request body has empty carryBackLossesDecrease or averagingAdjustmentsDecrease objects" in {
        validate(body = invalidRequestJsonEmptyFields) shouldBe error(
          RuleIncorrectOrEmptyBodyError.withPaths(
            Seq(
              "/carryBackLossesDecrease",
              "/averagingAdjustmentsDecrease"
            )
          )
        )
      }
    }
  }

}
