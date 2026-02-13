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

package v1.retrieve.def1.model.request

import api.models.domain.{Nino, TaxYear}
import v1.retrieve.RetrieveTaxLiabilityAdjustmentsSchema
import v1.retrieve.model.request.RetrieveTaxLiabilityAdjustmentsRequestData

case class Def1_RetrieveTaxLiabilityAdjustmentsRequestData(nino: Nino, taxYear: TaxYear) extends RetrieveTaxLiabilityAdjustmentsRequestData {
  override val schema: RetrieveTaxLiabilityAdjustmentsSchema = RetrieveTaxLiabilityAdjustmentsSchema.Def1
}
