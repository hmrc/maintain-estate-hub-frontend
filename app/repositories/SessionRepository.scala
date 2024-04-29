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

package repositories

import java.util.concurrent.TimeUnit
import com.google.inject.ImplementedBy
import config.FrontendAppConfig

import javax.inject.{Inject, Singleton}
import models.UserAnswers
import org.mongodb.scala.model.{FindOneAndUpdateOptions, IndexModel, IndexOptions, Indexes, Updates}
import org.mongodb.scala.model.Filters.equal
import play.api.libs.json._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import org.mongodb.scala.model.ReplaceOptions

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DefaultSessionRepository @Inject()( val mongo: MongoComponent,
                                          val config: FrontendAppConfig)
                                        (implicit val ec: ExecutionContext)

  extends PlayMongoRepository[UserAnswers](
    mongoComponent = mongo,
    domainFormat = Format(UserAnswers.reads,UserAnswers.writes),
    collectionName = "user-answers",
    indexes = Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions().name("user-answers-last-updated-index").expireAfter(config.cachettlInSeconds, TimeUnit.SECONDS).unique(false)),
      IndexModel(
        Indexes.ascending("internalId"),
        IndexOptions().name("internal-auth-id-index")
      )
    ), replaceIndexes = config.dropIndexes
  )
    with SessionRepository {

  def get(id: String): Future[Option[UserAnswers]] = {

    val selector = equal("internalId", id)

    val modifier = Updates.set("updatedAt", Instant.now())

    val updateOption = new FindOneAndUpdateOptions().upsert(false)

    collection.findOneAndUpdate(selector, modifier, updateOption).toFutureOption()

  }

  def set(userAnswers: UserAnswers): Future[Boolean] = {
    val selector = equal("internalId", userAnswers.id)
    val updated = userAnswers.copy(lastUpdated = Instant.now)

    collection.replaceOne(selector, updated, ReplaceOptions().upsert(true)).head()
      .map(_.wasAcknowledged())
  }

  def resetCache(internalId: String): Future[Boolean] = {

    val selector = equal("internalId",internalId)

    collection.deleteOne(selector).headOption().map(_.exists(_.wasAcknowledged()))

  }

}

@ImplementedBy(classOf[DefaultSessionRepository])
trait SessionRepository {

  def get(id: String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]

  def resetCache(internalId: String): Future[Boolean]

}
