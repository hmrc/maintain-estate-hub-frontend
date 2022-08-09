/*
 * Copyright 2022 HM Revenue & Customs
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

package models.requests

import java.time.LocalDateTime

import models.UserAnswers
import models.declaration.Address
import play.api.mvc.{Request, WrappedRequest}

case class OptionalDataRequest[A](request: Request[A],
                                  userAnswers: Option[UserAnswers],
                                  user: User) extends WrappedRequest[A](request)

case class DataRequest[A](request: Request[A],
                          userAnswers: UserAnswers,
                          user: User) extends WrappedRequest[A](request)


case class DataRequestWithUTR[A](request: Request[A],
                                 userAnswers: UserAnswers,
                                 user: User,
                                 utr: String) extends WrappedRequest[A](request)

case class AgentRequestWithAddress[A](request: Request[A],
                                      userAnswers: UserAnswers,
                                      user: AgentUser,
                                      utr: String,
                                      address: Address) extends WrappedRequest[A](request)

case class TvnRequest[A](request: Request[A],
                         userAnswers: UserAnswers,
                         user: User,
                         utr: String,
                         tvn: String,
                         clientReferenceNumber: Option[String],
                         submissionDate: LocalDateTime) extends WrappedRequest[A](request)
