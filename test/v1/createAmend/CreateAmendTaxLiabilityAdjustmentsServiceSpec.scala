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

import api.models.domain.{Nino, TaxYear}
import api.models.errors.*
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v1.createAmend.def1.fixture.Def1_CreateAmendTaxLiabilityAdjustmentsConnectorFixture.mtdRequestBody
import v1.createAmend.def1.model.request.Def1_CreateAmendTaxLiabilityAdjustmentsRequestData

import scala.concurrent.Future

class CreateAmendTaxLiabilityAdjustmentsServiceSpec extends ServiceSpec {

  val requestData: Def1_CreateAmendTaxLiabilityAdjustmentsRequestData = Def1_CreateAmendTaxLiabilityAdjustmentsRequestData(
    Nino("AA123456A"),
    TaxYear.fromMtd("2026-27"),
    mtdRequestBody
  )

  "CreateAmendTaxLiabilityAdjustmentsService" should {
    "return a success response" when {
      "using schema Def1" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
        MockCreateAmendTaxLiabilityAdjustmentsConnector
          .createAmendTaxLiabilityAdjustments(requestData)
          .returns(Future.successful(outcome))
        await(service.createAmendTaxLiabilityAdjustments(requestData)) shouldBe outcome
      }
    }

    def serviceError(downStreamErrorCode: String, error: MtdError): Unit =
      s"a $downStreamErrorCode error is returned from the service" in new Test {
        MockCreateAmendTaxLiabilityAdjustmentsConnector
          .createAmendTaxLiabilityAdjustments(requestData)
          .returns(
            Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downStreamErrorCode)))))
          )

        await(service.createAmendTaxLiabilityAdjustments(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
      }

    val errorMap = List(
      "1215" -> NinoFormatError,
      "1117" -> TaxYearFormatError,
      "1216" -> InternalError,
      "1000" -> InternalError,
      "1115" -> RuleTaxYearNotEndedError,
      "4200" -> RuleOutsideAmendmentWindowError,
      "5000" -> InternalError
    )

    errorMap.foreach(args => serviceError.tupled(args))
  }

  trait Test extends MockCreateAmendTaxLiabilityAdjustmentsConnector {
    val service = new CreateAmendTaxLiabilityAdjustmentsService(mockConnector)
  }

}
