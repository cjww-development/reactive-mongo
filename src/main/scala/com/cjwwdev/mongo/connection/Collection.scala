/*
 * Copyright 2018 CJWW Development
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

import org.slf4j.{Logger, LoggerFactory}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Collection {
  val mongoUri, dbName, collectionName: String

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private lazy val parsedUri = MongoConnection.parseURI(mongoUri).get

  private val driver     = MongoDriver()
  private val connection = driver.connection(parsedUri)

  private def database: Future[DefaultDB] = connection.database(dbName)

  def collection: Future[JSONCollection] = database map(_.collection[JSONCollection](collectionName))
}
