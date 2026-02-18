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

package v1.createAmend

import api.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import api.models.errors.{RuleTaxYearNotEndedError, RuleTaxYearNotSupportedError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import api.utils.UnitSpec
import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import v1.createAmend.CreateAmendTaxLiabilityAdjustmentsSchema.Def1

import java.time.{Clock, Instant, ZoneOffset}

class CreateAmendTaxLiabilityAdjustmentsSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  private implicit val fixedClock: Clock = Clock.fixed(Instant.parse("2027-08-01T00:00:00Z"), ZoneOffset.UTC)
  private val minimumTaxYear: TaxYear    = TaxYear.fromMtd("2026-27")

  private def schemaFor(taxYear: String, temporalValidationEnabled: Boolean = false) =
    CreateAmendTaxLiabilityAdjustmentsSchema.schemaFor(taxYear, temporalValidationEnabled)

  "schema lookup" when {
    "a valid tax year is supplied" must {
      "use Def1 schema for a supported tax year when temporal validation is disabled" in {
        forTaxYearsFrom(minimumTaxYear) { taxYear =>
          schemaFor(taxYear.asMtd) shouldBe Valid(Def1)
        }
      }

      "use Def1 schema for a supported tax year that has ended when temporal validation is enabled" in {
        schemaFor("2026-27", true) shouldBe Valid(Def1)
      }
    }

    "handle errors" when {
      "a supported tax year that has not ended is supplied with temporal validation enabled" must {
        "return RuleTaxYearNotEndedError" in {
          forTaxYearsFrom(TaxYear.currentTaxYear) { taxYear =>
            schemaFor(taxYear.asMtd, true) shouldBe Invalid(Seq(RuleTaxYearNotEndedError))
          }
        }
      }

      "an unsupported tax year is supplied" must {
        "return RuleTaxYearNotSupportedError" in {
          forTaxYearsBefore(minimumTaxYear) { taxYear =>
            schemaFor(taxYear.asMtd) shouldBe Invalid(Seq(RuleTaxYearNotSupportedError))
          }
        }
      }

      "the tax year format is invalid" must {
        "return TaxYearFormatError" in {
          schemaFor("NotATaxYear") shouldBe Invalid(Seq(TaxYearFormatError))
        }
      }

      "the tax year range is invalid" must {
        "return RuleTaxYearRangeInvalidError" in {
          schemaFor("2020-99") shouldBe Invalid(Seq(RuleTaxYearRangeInvalidError))
        }
      }
    }
  }

}
