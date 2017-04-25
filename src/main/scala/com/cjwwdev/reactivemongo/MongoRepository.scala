// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cjwwdev.reactivemongo

import com.cjwwdev.logging.Logger
import play.api.libs.json.OFormat
import reactivemongo.api.{DB, ReadPreference}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class MongoRepository(collectionName: String,
                               mongo: () => DB,
                               mc: Option[JSONCollection] = None) extends Indexes with MongoResponses {

  lazy val collection: JSONCollection = mc.getOrElse(mongo().collection[JSONCollection](collectionName))

  ensureIndexes

  def create[T](data: T)(implicit oFormat: OFormat[T]): Future[MongoCreateResponse] = {
    collection.insert(data).map { writeResult =>
      if(writeResult.ok) {
        Logger.info(s"[MongoRepository] - [create] : Data was successfully created in ${collection.name}")
        MongoSuccessCreate
      } else {
        Logger.error(s"[MongoRepository] - [create] : There was a problem inserting data into ${collection.name}]")
        MongoFailedCreate
      }
    }
  }

  def read[T](query: BSONDocument)(implicit oFormat: OFormat[T]): Future[Option[T]] = {
    collection.find(query).one[T]
  }

  def readBulk[T](query: BSONDocument)(implicit oFormat: OFormat[T]): Future[List[T]] = {
    collection.find(query).cursor[T](ReadPreference.primaryPreferred).collect[List]()
  }
}
