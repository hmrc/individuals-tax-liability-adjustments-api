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

package v1.retrieve.def1

import api.models.domain.{Nino, TaxYear}
import api.models.errors.{ErrorWrapper, NinoFormatError}
import api.utils.UnitSpec
import v1.retrieve.def1.model.request.Def1_RetrieveTaxLiabilityAdjustmentsRequestData

class Def1_RetrieveTaxLiabilityAdjustmentsValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino    = "AA123456A"
  private val validTaxYear = "2025-26"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  private def validator(nino: String = validNino, taxYear: String = validTaxYear) =
    new Def1_RetrieveTaxLiabilityAdjustmentsValidator(nino, taxYear)

  "validator" should {
    "return the parsed domain object" when {
      "given a valid request" in {
        validator().validateAndWrapResult() shouldBe
          Right(Def1_RetrieveTaxLiabilityAdjustmentsRequestData(parsedNino, parsedTaxYear))
      }
    }

    "return a single error" when {
      "given an invalid nino" in {
        validator(nino = "invalidNino").validateAndWrapResult() shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }
  }

}
