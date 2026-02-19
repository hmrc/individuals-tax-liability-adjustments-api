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

import api.models.domain.{Nino, TaxYear}
import v1.createAmend.CreateAmendTaxLiabilityAdjustmentsSchema
import v1.createAmend.model.request.CreateAmendTaxLiabilityAdjustmentsRequestData

case class Def1_CreateAmendTaxLiabilityAdjustmentsRequestData(
    nino: Nino,
    taxYear: TaxYear,
    body: Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody
) extends CreateAmendTaxLiabilityAdjustmentsRequestData {
  val schema: CreateAmendTaxLiabilityAdjustmentsSchema = CreateAmendTaxLiabilityAdjustmentsSchema.Def1
}
