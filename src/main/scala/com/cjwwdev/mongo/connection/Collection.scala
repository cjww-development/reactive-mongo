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

package com.cjwwdev.mongo.connection

import reactivemongo.api.{AsyncDriver, DefaultDB, MongoConnection}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait Collection {
  val mongoUri, dbName, collectionName: String

  implicit val ec: ExecutionContext

  private val parsedUri: Future[MongoConnection.ParsedURI] = MongoConnection.fromString(mongoUri)

  private val driver: AsyncDriver = AsyncDriver()
  private val connection = parsedUri.flatMap(driver.connect)

  private def database: Future[DefaultDB] = connection.flatMap(_.database(dbName))

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection](collectionName))
}
