/*
 * Copyright 2019 CJWW Development
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

package com.cjwwdev.mongo.connection

import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait Collection {
  val mongoUri, dbName, collectionName: String

  private lazy val parsedUri = MongoConnection.parseURI(mongoUri).get

  private val driver     = MongoDriver()
  private val connection = driver.connection(parsedUri, strictUri = true)

  private def database(implicit ec: ExecutionContext): Future[DefaultDB] = {
    connection.fold(
      e => throw e,
      _.database(dbName)
    )
  }

  def collection(implicit ec: ExecutionContext): Future[JSONCollection] = database map(_.collection[JSONCollection](collectionName))
}
