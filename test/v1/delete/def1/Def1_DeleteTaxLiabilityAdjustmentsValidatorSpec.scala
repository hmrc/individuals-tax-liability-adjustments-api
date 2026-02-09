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

package v1.delete.def1

import api.config.MockAppConfig
import api.models.domain.{Nino, TaxYear}
import api.models.errors.{ErrorWrapper, NinoFormatError}
import api.utils.UnitSpec
import v1.delete.def1.model.request.Def1_DeleteTaxLiabilityAdjustmentsRequestData
import v1.delete.model.request.DeleteTaxLiabilityAdjustmentsRequestData

class Def1_DeleteTaxLiabilityAdjustmentsValidatorSpec extends UnitSpec with MockAppConfig {
  private implicit val correlationId: String = "1234"
  private val validNino                      = "AA123456A"
  private val validTaxYear                   = "2020-21"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  private def validator(nino: String, taxYear: String) =
    new Def1_DeleteTaxLiabilityAdjustmentsValidator(nino, taxYear).validateAndWrapResult()

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied" in {
        val result: Either[ErrorWrapper, DeleteTaxLiabilityAdjustmentsRequestData] =
          validator(validNino, validTaxYear)

        result shouldBe Right(Def1_DeleteTaxLiabilityAdjustmentsRequestData(parsedNino, parsedTaxYear))

      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, DeleteTaxLiabilityAdjustmentsRequestData] =
          validator("A12344A", validTaxYear)

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }

  }

}
