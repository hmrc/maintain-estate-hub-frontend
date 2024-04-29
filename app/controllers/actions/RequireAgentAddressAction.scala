/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.actions

import com.google.inject.ImplementedBy
import controllers.routes
import javax.inject.Inject
import models.requests.{AgentRequestWithAddress, AgentUser, DataRequestWithUTR}
import pages.declaration.AgencyRegisteredAddressPage
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import scala.concurrent.{ExecutionContext, Future}

class RequireAgentAddressActionImpl @Inject()(implicit val executionContext: ExecutionContext) extends RequireAgentAddressAction {

  override protected def refine[A](request: DataRequestWithUTR[A]): Future[Either[Result, AgentRequestWithAddress[A]]] = {
    request.user match {
      case a : AgentUser =>
        request.userAnswers.get(AgencyRegisteredAddressPage) match {
          case Some(x) =>
            Future.successful(Right(AgentRequestWithAddress(request.request, request.userAnswers, a, request.utr, x)))
          case None =>
            Future.successful(Left(Redirect(routes.EstateStatusController.problemWithService())))
        }
      case _ =>
        Future.successful(Left(Redirect(routes.UnauthorisedController.onPageLoad())))
    }
  }
}

@ImplementedBy(classOf[RequireAgentAddressActionImpl])
sealed trait RequireAgentAddressAction extends ActionRefiner[DataRequestWithUTR, AgentRequestWithAddress]
