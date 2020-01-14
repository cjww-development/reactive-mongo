/*
 * Copyright 2020 CJWW Development
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
package com.cjwwdev.mongo

import com.cjwwdev.mongo.responses._
import play.api.libs.json._
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.Future

case class TestModel(_id: String, string: String, int: Int)

object TestModel {
  implicit val testModelFormat: OFormat[TestModel] = Json.format[TestModel]
}

trait TestRepository extends DatabaseRepository {

  override def indexes: Seq[Index] = Seq(
    Index(BSONSerializationPack)(
      key = Seq("string" -> IndexType.Ascending),
      name = Some("StringIndex"),
      unique = false,
      background = false,
      dropDups = false,
      sparse = false,
      expireAfterSeconds = None,
      storageEngine = None,
      weights = None,
      defaultLanguage = None,
      languageOverride = None,
      textIndexVersion = None,
      sphereIndexVersion = None,
      bits = None,
      min = None,
      max = None,
      bucketSize = None,
      collation = None,
      wildcardProjection = None,
      version = None,
      partialFilter = None,
      options = BSONDocument.empty
    )
  )

  def create[T](data: T)(implicit format: OFormat[T]): Future[MongoCreateResponse] = collection.flatMap {
    _.insert(ordered = false).one(data).map(wr => if(wr.ok) MongoSuccessCreate else MongoFailedCreate)
  }

  def read[T](id: String)(implicit format: OFormat[T]): Future[Option[T]] = collection.flatMap {
    _.find(BSONDocument("_id" -> id), None)(BSONDocumentWrites, BSONDocumentWrites).one[T]
  }

  def update(id: String, stringUpdate: String): Future[MongoUpdatedResponse] = {
    val selector = BSONDocument("_id" -> id)
    val update   = BSONDocument("$set" -> BSONDocument("string" -> stringUpdate))
    collection flatMap {
      _.update(ordered = false).one(selector, update).map { wr =>
        if(wr.ok) MongoSuccessUpdate else MongoFailedUpdate
      }
    }
  }

  def delete(id: String): Future[MongoDeleteResponse] = collection.flatMap {
    _.delete(ordered = false).one(BSONDocument("_id" -> id)).map { wr =>
      if(wr.ok) MongoSuccessDelete else MongoFailedDelete
    }
  }
}
