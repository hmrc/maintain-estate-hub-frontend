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

package controllers.actions

import com.google.inject.ImplementedBy
import controllers.routes
import javax.inject.Inject
import models.requests.{DataRequestWithUTR, TvnRequest}
import pages.{AgentDeclarationPage, SubmissionDatePage, TVNPage}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import scala.concurrent.{ExecutionContext, Future}

class RequireTvnActionImpl @Inject()(implicit val executionContext: ExecutionContext) extends RequireTvnAction {

  override protected def refine[A](request: DataRequestWithUTR[A]): Future[Either[Result, TvnRequest[A]]] = {
    Future.successful((for {
        tvn <- request.userAnswers.get(TVNPage)
        submissionDate <- request.userAnswers.get(SubmissionDatePage)
      } yield {
        val optionalCrn = request.userAnswers.get(AgentDeclarationPage).map(_.crn)

        Right(TvnRequest(request.request, request.userAnswers, request.user, request.utr, tvn, optionalCrn, submissionDate))
      }
    ).getOrElse {
      Left(Redirect(routes.EstateStatusController.problemWithService()))
    })
  }
}

@ImplementedBy(classOf[RequireTvnActionImpl])
sealed trait RequireTvnAction extends ActionRefiner[DataRequestWithUTR, TvnRequest]
