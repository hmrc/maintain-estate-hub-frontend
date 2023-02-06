/*
 * Copyright 2023 HM Revenue & Customs
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

package services

import com.google.inject.Inject
import connectors.EstatesAuthConnector
import models.http.{AuthAgentAllowed, AuthAllowed, AuthDenied}
import models.requests.DataRequest
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import utils.Session

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EstateAuthenticationServiceImpl @Inject()(authConnector: EstatesAuthConnector) extends EstateAuthenticationService with Logging {

  override def authenticateAgent()(implicit hc: HeaderCarrier): Future[Either[Result, String]] = {

    authConnector.agentIsAuthorised.flatMap {
      case AuthAgentAllowed(arn) =>
        Future.successful(Right(arn))
      case AuthDenied(redirectUrl) =>
        Future.successful(Left(Redirect(redirectUrl)))
      case _ =>
        logger.warn(s"[Session ID: ${Session.id(hc)}] Unable to authenticate agent with estates-auth")
        Future.successful(Left(Unauthorized))
    }
  }

  override def authenticateForUtr[A](utr: String)
                                    (implicit request: DataRequest[A], hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]] = {

    authConnector.authorisedForUtr(utr).flatMap {
      case _: AuthAllowed =>
        Future.successful(Right(request))
      case AuthDenied(redirectUrl) =>
        Future.successful(Left(Redirect(redirectUrl)))
      case _ =>
        logger.warn(s"[Session ID: ${Session.id(hc)}] Unable to authenticate for utr with estates-auth")
        Future.successful(Left(Unauthorized))
    }
  }
}

trait EstateAuthenticationService {

  def authenticateAgent()(implicit hc: HeaderCarrier): Future[Either[Result, String]]

  def authenticateForUtr[A](utr: String)
                           (implicit request: DataRequest[A], hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]]
}
