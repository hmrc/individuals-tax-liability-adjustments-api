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

import api.models.domain.TaxYear
import api.support.IntegrationBaseSpec
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.test.Helpers.*
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import api.models.errors.*
import api.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import play.api.libs.ws.DefaultBodyReadables.readableAsString
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import v1.createAmend.def1.fixture.Def1_CreateAmendTaxLiabilityAdjustmentsFixture.requestBodyJson

import java.time.{Clock, Instant, ZoneOffset}

class CreateAmendTaxLiabilityAdjustmentsControllerISpec extends IntegrationBaseSpec {

  private def errorBody(code: String): String =
    s"""
       | [
       |    {
       |      "errorCode": "$code",
       |      "errorDescription": "message"
       |    }
       |  ]
      """.stripMargin

  val invalidRequestJsonNegativeFields: JsValue =
    Json.parse(
      """
        |{
        |  "carryBackLossesDecrease": {
        |    "incomeTax": -5000.99,
        |    "class4": -5000.99,
        |    "capitalGainsTax": -5000.99
        |  },
        |  "averagingAdjustmentsDecrease": {
        |    "incomeTax": -5000.99,
        |    "class4": -5000.99,
        |    "capitalGainsTax": -5000.99
        |  }
        |}
        |""".stripMargin
    )

  private implicit val fixedClock: Clock = Clock.fixed(Instant.parse("2026-08-01T00:00:00Z"), ZoneOffset.UTC)
  private val currentTaxYear: TaxYear    = TaxYear.currentTaxYear

  "Calling the Create Amend tax liability adjustments endpoint" should {
    "return a 204 status code" when {
      "a valid request is made with the current tax year and suspendTemporalValidations is true" in new Test {
        override val taxYear: String = currentTaxYear.asMtd

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.authorised()

          DownstreamStub.onSuccess(
            method = DownstreamStub.PUT,
            uri = downstreamUri,
            queryParams = Map("taxYear" -> currentTaxYear.asTysDownstream),
            status = NO_CONTENT,
            body = JsObject.empty
          )
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe NO_CONTENT
        response.body shouldBe ""
        response.header("Content-Type") shouldBe None
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestTaxYear: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String                       = requestNino
            override val taxYear: String                    = requestTaxYear
            override val suspendTemporalValidations: String = "false"

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              MtdIdLookupStub.ninoFound(nino)
              AuthStub.authorised()
            }

            val response: WSResponse = await(request().put(requestBody))
            response.status shouldBe expectedStatus
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = List(
          ("AA1123A", "2026-27", requestBodyJson, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "invalid", requestBodyJson, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "2025-27", requestBodyJson, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "2025-26", requestBodyJson, BAD_REQUEST, RuleTaxYearNotSupportedError),
          (
            "AA123456A",
            "2026-27",
            invalidRequestJsonNegativeFields,
            BAD_REQUEST,
            ValueFormatError.withPaths(
              Seq(
                "/averagingAdjustmentsDecrease/capitalGainsTax",
                "/averagingAdjustmentsDecrease/class4",
                "/averagingAdjustmentsDecrease/incomeTax",
                "/carryBackLossesDecrease/capitalGainsTax",
                "/carryBackLossesDecrease/class4",
                "/carryBackLossesDecrease/incomeTax"
              )
            )),
          ("AA123456A", currentTaxYear.asMtd, requestBodyJson, BAD_REQUEST, RuleTaxYearNotEndedError),
          ("AA123456A", "2026-27", JsObject.empty, BAD_REQUEST, RuleIncorrectOrEmptyBodyError)
        )

        input.foreach(validationErrorTest.tupled)
      }
    }
    "downstream service error" when {
      def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            MtdIdLookupStub.ninoFound(nino)
            AuthStub.authorised()
            DownstreamStub.onError(DownstreamStub.PUT, downstreamUri, downstreamStatus, errorBody(downstreamCode))
          }

          val response: WSResponse = await(request().put(requestBodyJson))
          response.json shouldBe Json.toJson(expectedBody)
          response.status shouldBe expectedStatus
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val errors = List(
        (BAD_REQUEST, "1215", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "1115", BAD_REQUEST, RuleTaxYearNotEndedError),
        (BAD_REQUEST, "1117", BAD_REQUEST, TaxYearFormatError),
        (BAD_REQUEST, "1216", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "1000", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError),
        (UNPROCESSABLE_ENTITY, "4200", BAD_REQUEST, RuleOutsideAmendmentWindowError),
        (NOT_IMPLEMENTED, "5000", INTERNAL_SERVER_ERROR, InternalError)
      )

      errors.foreach(serviceErrorTest.tupled)
    }
  }

  private trait Test {
    val nino: String                       = "AA123456A"
    val taxYear: String                    = "2026-27"
    val suspendTemporalValidations: String = "true"

    def setupStubs(): StubMapping

    private def mtdUri: String = s"/$nino/$taxYear"

    def request(): WSRequest = {
      AuthStub.resetAll()
      setupStubs()

      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123"),
          ("suspend-temporal-validations", suspendTemporalValidations)
        )
    }

    def downstreamUri: String = s"/itsd/adjustments/tax/$nino"

  }

}
