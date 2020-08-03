/*
 * Copyright 2020 HM Revenue & Customs
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
import pages.TVNPage
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import scala.concurrent.{ExecutionContext, Future}

class RequireTvnActionImpl @Inject()(implicit val executionContext: ExecutionContext) extends RequireTvnAction {

  override protected def refine[A](request: DataRequestWithUTR[A]): Future[Either[Result, TvnRequest[A]]] = {
    request.userAnswers.get(TVNPage) match {
      case Some(tvn) =>
        Future.successful(Right(TvnRequest(request.request, request.userAnswers, request.user, request.utr, tvn)))
      case None =>
        Future.successful(Left(Redirect(routes.EstateStatusController.problemWithService())))
    }
  }
}

@ImplementedBy(classOf[RequireTvnActionImpl])
sealed trait RequireTvnAction extends ActionRefiner[DataRequestWithUTR, TvnRequest]