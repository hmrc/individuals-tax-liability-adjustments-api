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

package v1.createAmend.def1

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.*
import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.ResolveParsedNumber
import api.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import v1.createAmend.def1.model.request.{
  AveragingAdjustmentsDecrease,
  CarryBackLossesDecrease,
  Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody,
  Def1_CreateAmendTaxLiabilityAdjustmentsRequestData
}

object Def1_CreateAmendTaxLiabilityAdjustmentsRulesValidator extends RulesValidator[Def1_CreateAmendTaxLiabilityAdjustmentsRequestData] {

  private val resolveNonNegativeParsedNumber = ResolveParsedNumber()

  def validateBusinessRules(
      parsed: Def1_CreateAmendTaxLiabilityAdjustmentsRequestData): Validated[Seq[MtdError], Def1_CreateAmendTaxLiabilityAdjustmentsRequestData] = {

    import parsed.*

    combine(
      validateAveragingAdjustmentsDecrease(body),
      validateCarryBackLossesDecrease(body)
    ).onSuccess(parsed)
  }

  private def validateAveragingAdjustmentsDecrease(
      requestBody: Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody): Validated[Seq[MtdError], Unit] = {
    requestBody.averagingAdjustmentsDecrease match {
      case Some(averagingAdjustmentsDecrease) =>
        validatePresenceOfAtLeastOneField(averagingAdjustmentsDecrease)
          .productR(
            validateAveragingAdjustmentsDecrease(averagingAdjustmentsDecrease)
          )
      case None => valid
    }
  }

  private def validateAveragingAdjustmentsDecrease(averagingAdjustmentsDecrease: AveragingAdjustmentsDecrease): Validated[Seq[MtdError], Unit] = {

    import averagingAdjustmentsDecrease.*

    List(
      (incomeTax, "/averagingAdjustmentsDecrease/incomeTax"),
      (class4, "/averagingAdjustmentsDecrease/class4"),
      (capitalGainsTax, "/averagingAdjustmentsDecrease/capitalGainsTax")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }
  }

  private def validateCarryBackLossesDecrease(requestBody: Def1_CreateAmendTaxLiabilityAdjustmentsRequestBody): Validated[Seq[MtdError], Unit] = {
    requestBody.carryBackLossesDecrease match {
      case Some(carryBackLossesDecrease) =>
        validatePresenceOfAtLeastOneField(carryBackLossesDecrease)
          .productR(
            validateCarryBackLossesDecrease(carryBackLossesDecrease)
          )
      case None => valid
    }
  }

  private def validateCarryBackLossesDecrease(carryBackLossesDecrease: CarryBackLossesDecrease): Validated[Seq[MtdError], Unit] = {
    import carryBackLossesDecrease.*

    List(
      (incomeTax, "/carryBackLossesDecrease/incomeTax"),
      (class4, "/carryBackLossesDecrease/class4"),
      (capitalGainsTax, "/carryBackLossesDecrease/capitalGainsTax")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }
  }

  private def validatePresenceOfAtLeastOneField(averagingAdjustmentsDecrease: AveragingAdjustmentsDecrease): Validated[Seq[MtdError], Unit] = {
    import averagingAdjustmentsDecrease.*
    if (incomeTax.isEmpty && class4.isEmpty && capitalGainsTax.isEmpty) {
      Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/averagingAdjustmentsDecrease")))
    } else {
      valid
    }
  }

  private def validatePresenceOfAtLeastOneField(carryBackLossesDecrease: CarryBackLossesDecrease): Validated[Seq[MtdError], Unit] = {
    import carryBackLossesDecrease.*
    if (incomeTax.isEmpty && class4.isEmpty && capitalGainsTax.isEmpty) {
      Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/carryBackLossesDecrease")))
    } else {
      valid
    }
  }

}
