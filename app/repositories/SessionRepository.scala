/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time.LocalDateTime

import javax.inject.Inject
import models.{MongoDateTimeFormats, UserAnswers}
import play.api.Configuration
import play.api.libs.json._
import reactivemongo.api.WriteConcern
import reactivemongo.api.indexes.IndexType
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class DefaultSessionRepository @Inject()(
                                          val mongo: MongoDriver,
                                          val config: Configuration
                                        )(implicit val ec: ExecutionContext)
  extends SessionRepository
    with IndexManager {

  implicit final val jsObjectWrites: OWrites[JsObject] = OWrites[JsObject](identity)

  override val collectionName: String = "user-answers"

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private def collection: Future[JSONCollection] =
    for {
      _ <- ensureIndexes
      res <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
    } yield res

  private def ensureIndexes: Future[Unit] = {
    for {
      collection  <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
      _           <- collection.indexesManager.ensure(lastUpdatedIndex)
      _           <- collection.indexesManager.ensure(internalAuthIdIndex)
    } yield ()
  }

  private val lastUpdatedIndex = MongoIndex(
    key     = Seq("lastUpdated" -> IndexType.Ascending),
    name    = "user-answers-last-updated-index",
    expireAfterSeconds = Some(cacheTtl)
  )

  private val internalAuthIdIndex = MongoIndex(
    key = Seq("internalId" -> IndexType.Ascending),
    name = "internal-auth-id-index"
  )

  override def get(id: String): Future[Option[UserAnswers]] = {
    val selector = Json.obj(
      "internalId" -> id
    )

    val modifier = Json.obj(
      "$set" -> Json.obj(
        "updatedAt" -> MongoDateTimeFormats.localDateTimeWrite.writes(LocalDateTime.now)
      )
    )

    collection.flatMap {
      _.findAndUpdate(
        selector = selector,
        update = modifier,
        fetchNewObject = true,
        upsert = false,
        sort = None,
        fields = None,
        bypassDocumentValidation = false,
        writeConcern = WriteConcern.Default,
        maxTime = None,
        collation = None,
        arrayFilters = Nil
      ).map(_.result[UserAnswers])
    }
  }

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "internalId" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (lastUpdated = LocalDateTime.now))
    )

    collection.flatMap {
      _.update(ordered = false).one(selector, modifier, upsert = true, multi = false).map {
        result => result.ok
      }
    }
  }

  override def resetCache(internalId: String): Future[Option[JsObject]] = {

    val selector = Json.obj(
      "internalId" -> internalId
    )

    collection.flatMap(_.findAndRemove(selector, None, None, WriteConcern.Default, None, None, Seq.empty).map(
      _.value
    ))
  }
}

trait SessionRepository {

  def get(id: String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]

  def resetCache(internalId: String): Future[Option[JsObject]]
}
