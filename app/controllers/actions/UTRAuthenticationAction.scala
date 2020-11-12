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

import com.google.inject.{ImplementedBy, Inject}
import models.requests.{DataRequest, DataRequestWithUTR}
import pages.UTRPage
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, BodyParsers, Result}
import services.EstateAuthenticationService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

class UTRAuthenticationActionImpl @Inject()(val parser: BodyParsers.Default,
                                            service: EstateAuthenticationService
                                            )(override implicit val executionContext: ExecutionContext
) extends UTRAuthenticationAction with Logging {

  override def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequestWithUTR[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    request.userAnswers.get(UTRPage) map { utr =>
      service.authenticateForUtr(utr)(request, hc) map {
        case Left(redirect) => Left(redirect)
        case Right(b) =>
          Right(DataRequestWithUTR(b.request, b.userAnswers, b.user, utr))
      }
    } getOrElse {
      logger.info(s"[Session ID: ${Session.id(hc)}] cannot authenticate user due to no cached utr")
      Future.successful(Left(Redirect(controllers.routes.IndexController.onPageLoad())))
    }

  }

}

@ImplementedBy(classOf[UTRAuthenticationActionImpl])
trait UTRAuthenticationAction extends ActionRefiner[DataRequest, DataRequestWithUTR] {

  def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequestWithUTR[A]]]

}