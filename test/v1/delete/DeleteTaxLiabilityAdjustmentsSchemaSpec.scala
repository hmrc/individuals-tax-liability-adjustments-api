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

package v1.delete

import api.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import api.models.errors.{RuleTaxYearNotSupportedError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import api.utils.UnitSpec
import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class DeleteTaxLiabilityAdjustmentsSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "schema lookup" when {
    "a correctly formatted tax year is supplied" must {
      "disallow tax years prior to 2026-27 and return RuleTaxYearNotSupportedError" in {
        forTaxYearsBefore(TaxYear.fromMtd("2026-27")) { taxYear =>
          DeleteTaxLiabilityAdjustmentsSchema.schemaFor(taxYear.asMtd) shouldBe Invalid(Seq(RuleTaxYearNotSupportedError))
        }
      }

      "use Def1 for tax years 2026-27 onwards" in {
        forTaxYearsFrom(TaxYear.fromMtd("2026-27")) { taxYear =>
          DeleteTaxLiabilityAdjustmentsSchema.schemaFor(taxYear.asMtd) shouldBe Valid(DeleteTaxLiabilityAdjustmentsSchema.Def1)
        }
      }
    }

    "a badly formatted tax year is supplied" when {
      "the tax year format is invalid" must {
        "return a TaxYearFormatError" in {
          DeleteTaxLiabilityAdjustmentsSchema.schemaFor("NotATaxYear") shouldBe Invalid(Seq(TaxYearFormatError))
        }
      }

      "the tax year range is invalid" must {
        "return a RuleTaxYearRangeInvalidError" in {
          DeleteTaxLiabilityAdjustmentsSchema.schemaFor("2020-99") shouldBe Invalid(Seq(RuleTaxYearRangeInvalidError))
        }
      }
    }
  }

}
