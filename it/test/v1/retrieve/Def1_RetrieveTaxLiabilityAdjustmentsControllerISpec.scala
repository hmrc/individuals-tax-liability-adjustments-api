///*
// * Copyright 2025 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package v1.retrieve
//
//import api.services.DownstreamStub
//import api.models.errors.*
//import api.support.IntegrationBaseSpec
//import play.api.http.HeaderNames.ACCEPT
//import play.api.http.Status.*
//import play.api.libs.json.{JsValue, Json}
//import play.api.libs.ws.{WSRequest, WSResponse}
//import play.api.test.Helpers.AUTHORIZATION
//
//class Def1_RetrieveTaxLiabilityAdjustmentsControllerISpec extends IntegrationBaseSpec with Def1_RetrieveTaxLiabilityAdjustmentsFixture {
//
//  private trait Test {
//
//    val nino: String          = "AA123456A"
//    def taxYear: String       = "2026-27"
//    val responseBody: JsValue = fullMtdJson
//
//    val queryParams = Map("taxYear" -> "26-27")
//
//    def downstreamUri: String = s"/itsd/adjustments/tax/$nino"
//
//    def stubDownstreamSuccess(): Unit =
//      DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, queryParams, status = OK, body = fullDownstreamJson)
//
//    def request(): WSRequest = {
//      AuditStub.audit()
//      AuthStub.authorised()
//      MtdIdLookupStub.ninoFound(nino)
//      setupStubs()
//      buildRequest(s"/$nino/$taxYear")
//        .withHttpHeaders(
//          (ACCEPT, "application/vnd.hmrc.1.0+json"),
//          (AUTHORIZATION, "Bearer 123")
//        )
//    }
//
//    def setupStubs(): Unit = ()
//
//    def errorBody(code: String): String =
//      s"""
//         | [
//         |    {
//         |      "errorCode": "$code",
//         |      "errorDescription": "message"
//         |    }
//         |  ]
//      """.stripMargin
//
//  }
//
//  "Retrieve Foreign property details endpoint" should {
//    "return a 200 status code" when {
//      "successful request is made" in new Test {
//        override def setupStubs(): Unit = stubDownstreamSuccess()
//
//        val response: WSResponse = await(request().get())
//        response.json shouldBe responseBody
//        response.status shouldBe OK
//        response.header("X-CorrelationId") should not be empty
//        response.header("Content-Type") shouldBe Some("application/json")
//      }
//    }
//
//    "return validation error according to spec" when {
//      "validation error" when {
//        def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
//          s"validation fails with ${expectedBody.code} error" in new Test {
//
//            override val nino: String    = requestNino
//            override val taxYear: String = requestTaxYear
//
//            override def request(): WSRequest = {
//              AuditStub.audit()
//              AuthStub.authorised()
//              MtdIdLookupStub.ninoFound(nino)
//              setupStubs()
//              buildRequest(s"/$nino/$taxYear")
//                .withHttpHeaders(
//                  (ACCEPT, "application/vnd.hmrc.1.0+json"),
//                  (AUTHORIZATION, "Bearer 123")
//                )
//            }
//            val response: WSResponse = await(request().get())
//            response.status shouldBe expectedStatus
//            response.json shouldBe Json.toJson(expectedBody)
//          }
//        }
//
//        val input = List(
//          ("AA1123A", "2026-27", BAD_REQUEST, NinoFormatError),
//          ("AA123456A", "invalid", BAD_REQUEST, TaxYearFormatError),
//          ("AA123456A", "2025-27", BAD_REQUEST, RuleTaxYearRangeInvalidError),
//          ("AA123456A", "2025-26", BAD_REQUEST, RuleTaxYearNotSupportedError),
//          ("AA123456A", "2024-25", BAD_REQUEST, RuleOutsideAmendmentWindow)
//        )
//
//        input.foreach(args => validationErrorTest.tupled(args))
//      }
//    }
//
//    "downstream service error" when {
//      "return mapped downstream service error" when {
//        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
//          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {
//
//            override def setupStubs(): Unit = DownstreamStub.onError(
//              DownstreamStub.GET,
//              downstreamUri,
//              queryParams,
//              downstreamStatus,
//              errorBody(downstreamCode)
//            )
//
//            val response: WSResponse = await(request().get())
//            response.status shouldBe expectedStatus
//            response.json shouldBe Json.toJson(expectedBody)
//          }
//        }
//
//        val input = List(
//          (BAD_REQUEST, "1215", BAD_REQUEST, NinoFormatError),
//          (BAD_REQUEST, "1117", BAD_REQUEST, TaxYearFormatError),
//          (BAD_REQUEST, "1216", INTERNAL_SERVER_ERROR, InternalError),
//          // (BAD_REQUEST, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError),
//          (NOT_FOUND, "5010", NOT_FOUND, NotFoundError),
//          (UNPROCESSABLE_ENTITY, "4200", BAD_REQUEST, RuleOutsideAmendmentWindow),
//          (INTERNAL_SERVER_ERROR, "5000", INTERNAL_SERVER_ERROR, InternalError)
//        )
//
//        input.foreach(args => serviceErrorTest.tupled(args))
//      }
//    }
//  }
//
//}
