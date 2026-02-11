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

import api.models.domain.Timestamp
import play.api.libs.json.{Json, OFormat}
import v1.retrieve.model.response.RetrieveTaxLiabilityAdjustmentsResponse

case class Def1_RetrieveTaxLiabilityAdjustmentsResponse(
    submittedOn: Timestamp,
    carryBackLossesDecrease: Option[CarryBackLossesDecrease],
    averagingAdjustmentsDecrease: Option[AveragingAdjustmentsDecrease]
) extends RetrieveTaxLiabilityAdjustmentsResponse

object Def1_RetrieveTaxLiabilityAdjustmentsResponse {

  implicit val format: OFormat[Def1_RetrieveTaxLiabilityAdjustmentsResponse] =
    Json.format[Def1_RetrieveTaxLiabilityAdjustmentsResponse]

}
