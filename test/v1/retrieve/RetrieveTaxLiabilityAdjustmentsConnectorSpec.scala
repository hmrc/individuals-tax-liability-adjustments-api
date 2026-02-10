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

package v1.retrieve

import api.connectors.ConnectorSpec
import uk.gov.hmrc.http.StringContextOps
import v1.retrieve.def1.model.request.Def1_RetrieveTaxLiabilityAdjustmentsRequestData
import v1.retrieve.model.request.RetrieveTaxLiabilityAdjustmentsRequestData
import v1.retrieve.model.response.RetrieveTaxLiabilityAdjustmentsResponse
import scala.concurrent.Future
import api.models.domain.{Nino, TaxYear}
import api.models.outcomes.ResponseWrapper
import v1.retrieve.def1.model.Def1_RetrieveTaxLiabilityAdjustmentsFixture

class RetrieveTaxLiabilityAdjustmentsConnectorSpec extends ConnectorSpec with Def1_RetrieveTaxLiabilityAdjustmentsFixture {

  private val nino          = "AA123456A"
  private val taxYear       = "2026-27"
  private val downstreamUrl = url"$baseUrl/itsd/adjustments/tax/$nino"

  trait Test {
    self: ConnectorTest =>

    val connector: RetrieveTaxLiabilityAdjustmentsConnector =
      new RetrieveTaxLiabilityAdjustmentsConnector(http = mockHttpClient, appConfig = mockAppConfig)

  }

  "RetrieveTaxLiabilityAdjustmentsConnector" when {
    "the request is made and data is returned" in new HipTest with Test {
      private val requestParams = List(
        "taxYear" -> "26-27"
      )

      val requestData: RetrieveTaxLiabilityAdjustmentsRequestData =
        Def1_RetrieveTaxLiabilityAdjustmentsRequestData(
          Nino(nino),
          TaxYear.fromMtd(taxYear)
        )

      willGet(url = downstreamUrl, parameters = requestParams).returns(
        Future.successful(Right(ResponseWrapper(correlationId, response)))
      )

      await(connector.retrieveTaxLiabilityAdjustments(requestData)).shouldBe(
        Right(ResponseWrapper(correlationId, response))
      )
    }

  }

}
