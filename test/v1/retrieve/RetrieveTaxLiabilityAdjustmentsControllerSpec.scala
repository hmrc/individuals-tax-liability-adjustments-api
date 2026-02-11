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

import play.api.Configuration
import play.api.mvc.Result
import api.models.domain.{Nino, TaxYear}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import api.models.audit.*
import api.routing.Version1
import api.controllers.ControllerTestRunner
import api.models.errors.{ErrorWrapper, NinoFormatError, RuleOutsideAmendmentWindowError}
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.JsValue
import v1.retrieve.def1.model.Def1_RetrieveTaxLiabilityAdjustmentsFixture
import v1.retrieve.def1.model.request.Def1_RetrieveTaxLiabilityAdjustmentsRequestData
import v1.retrieve.model.request.RetrieveTaxLiabilityAdjustmentsRequestData

class RetrieveTaxLiabilityAdjustmentsControllerSpec
    extends ControllerTestRunner
    with MockRetrieveTaxLiabilityAdjustmentsService
    with MockRetrieveTaxLiabilityAdjustmentsValidatorFactory
    with Def1_RetrieveTaxLiabilityAdjustmentsFixture {

  private val taxYear = "2026-27"

  "RetrieveTaxLiabilityAdjustmentsController" should {
    "return (OK) 200 status" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveTaxLiabilityAdjustmentsService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        runOkTest(expectedStatus = OK, maybeExpectedResponseBody = Some(fullMtdJson))
      }
    }

    "return validation error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveTaxLiabilityAdjustmentsService
          .retrieve(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleOutsideAmendmentWindowError))))

        runErrorTest(RuleOutsideAmendmentWindowError)
      }
    }
  }

  trait Test extends ControllerTest {

    protected val controller: RetrieveTaxLiabilityAdjustmentsController = new RetrieveTaxLiabilityAdjustmentsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveTaxLiabilityAdjustmentsValidatorFactory,
      service = mockRetrieveTaxLiabilityAdjustmentsService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "DeleteTaxLiabilityAdjustments",
        transactionName = "delete-tax-liability-adjustments",
        detail = GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          versionNumber = Version1.name,
          params = Map("nino" -> validNino, "taxYear" -> taxYear),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

    MockedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns true

    protected def callController(): Future[Result] = controller.retrieve(validNino, taxYear)(fakeGetRequest)

    protected val requestData: RetrieveTaxLiabilityAdjustmentsRequestData =
      Def1_RetrieveTaxLiabilityAdjustmentsRequestData(Nino(validNino), TaxYear.fromMtd(taxYear))

  }

}
