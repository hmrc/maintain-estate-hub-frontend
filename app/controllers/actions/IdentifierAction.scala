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

import com.google.inject.Inject
import config.FrontendAppConfig
import models.requests.{AgentUser, IdentifierRequest, OrganisationUser}
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc._
import services.EstateAuthenticationService
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject()(
                                               override val authConnector: AuthConnector,
                                               config: FrontendAppConfig,
                                               val parser: BodyParsers.Default,
                                               authenticationService: EstateAuthenticationService
                                             )
                                             (implicit val executionContext: ExecutionContext
                                             ) extends IdentifierAction with AuthorisedFunctions with Logging {

  private def authoriseAgent[A](internalId: String,
                                enrolments: Enrolments,
                                block: IdentifierRequest[A] => Future[Result])
                               (implicit request: Request[A], hc: HeaderCarrier) = {

    authenticationService.authenticateAgent() flatMap {
      case Right(arn) => block(IdentifierRequest(request ,AgentUser(internalId, enrolments, arn)))
      case Left(result: Result) => Future.successful(result)
    }
  }

  private def authenticateOrganisation[A](internalId: String,
                                          enrolments: Enrolments,
                                          block: IdentifierRequest[A] => Future[Result])
                                         (implicit request: Request[A]) = {
    block(IdentifierRequest(request, OrganisationUser(internalId, enrolments)))
  }

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val retrievals = Retrievals.internalId and
      Retrievals.affinityGroup and
      Retrievals.allEnrolments

    authorised().retrieve(retrievals) {
      case Some(internalId) ~ Some(Agent) ~ enrolments =>
        authoriseAgent(internalId, enrolments, block)(request, hc)

      case Some(internalId) ~ Some(Organisation) ~ enrolments =>
        authenticateOrganisation(internalId, enrolments, block)(request)

      case Some(_) ~ _ ~ _ =>
        Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))

      case _ =>
        logger.warn(s"[Session ID: ${Session.id(hc)}] Unable to retrieve retrievals")
        Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
    } recover {
      case _: NoActiveSession => Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case _: AuthorisationException => Redirect(controllers.routes.UnauthorisedController.onPageLoad())
    }
  }
}
