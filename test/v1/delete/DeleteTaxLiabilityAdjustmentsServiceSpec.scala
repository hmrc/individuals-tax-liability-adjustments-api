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

import api.models.domain.{Nino, TaxYear}
import api.models.errors.*
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v1.delete.def1.model.request.Def1_DeleteTaxLiabilityAdjustmentsRequestData
import v1.delete.model.request.DeleteTaxLiabilityAdjustmentsRequestData

import scala.concurrent.Future

class DeleteTaxLiabilityAdjustmentsServiceSpec extends ServiceSpec {

  private val nino: String    = "AA123456A"
  private val taxYear: String = "2026-27"

  trait Test extends MockDeleteTaxLiabilityAdjustmentsConnector {
    lazy val service = new DeleteTaxLiabilityAdjustmentsService(connector)
  }

  lazy val request: DeleteTaxLiabilityAdjustmentsRequestData = Def1_DeleteTaxLiabilityAdjustmentsRequestData(Nino(nino), TaxYear.fromMtd(taxYear))

  "deleteTaxLiabilityAdjustments" should {
    "return a Right" when {
      "the connector call is successful" in new Test {
        val response: ResponseWrapper[Unit] = ResponseWrapper(correlationId, ())
        MockDeleteTaxLiabilityAdjustmentsConnector.deleteTaxLiabilityAdjustments(request).returns(Future.successful(Right(response)))
        await(service.deleteTaxLiabilityAdjustments(request)) shouldBe Right(response)
      }
    }

    "return that wrapped error as-is" when {
      "the connector returns an outbound error" in new Test {
        val someError: MtdError                                = MtdError("SOME_CODE", "some message", BAD_REQUEST)
        val downstreamResponse: ResponseWrapper[OutboundError] = ResponseWrapper(correlationId, OutboundError(someError))
        MockDeleteTaxLiabilityAdjustmentsConnector.deleteTaxLiabilityAdjustments(request).returns(Future.successful(Left(downstreamResponse)))

        await(service.deleteTaxLiabilityAdjustments(request)) shouldBe Left(ErrorWrapper(correlationId, someError, None))
      }
    }

    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {
          MockDeleteTaxLiabilityAdjustmentsConnector
            .deleteTaxLiabilityAdjustments(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          private val result = await(service.deleteTaxLiabilityAdjustments(request))
          result shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors: Seq[(String, MtdError)] = List(
        "1215" -> NinoFormatError,
        "1117" -> TaxYearFormatError,
        "1216" -> InternalError,
        "4200" -> RuleOutsideAmendmentWindowError,
        "5000" -> InternalError,
        "5010" -> NotFoundError,
        ("UNMATCHED_STUB_ERROR", RuleIncorrectGovTestScenarioError)
      )
      errors.foreach(serviceError.tupled)
    }

  }

}
