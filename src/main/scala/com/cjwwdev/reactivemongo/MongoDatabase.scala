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

import com.cjwwdev.config.{ConfigurationLoader, MissingConfigurationException}
import org.slf4j.{Logger, LoggerFactory}
import reactivemongo.api.indexes.Index
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MongoDatabase {
  val configLoader: ConfigurationLoader

  lazy val mongoUri = configLoader.loadedConfig.getString("microservice.mongo.uri")
    .getOrElse(throw new MissingConfigurationException("Missing uri for mongo"))

  lazy val uri = MongoConnection.parseURI(mongoUri).get

  lazy val dbName = configLoader.loadedConfig.getString(s"${getClass.getCanonicalName}.database")
    .getOrElse(throw new MissingConfigurationException(s"Missing database name for $getClass"))

  lazy val collectionName = configLoader.loadedConfig.getString(s"${getClass.getCanonicalName}.collection")
    .getOrElse(throw new MissingConfigurationException(s"Missing collection name for $getClass"))

  private val driver = new MongoDriver()
  private val connection = driver.connection(uri)

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def indexes: Seq[Index] = Seq.empty

  def collection: Future[JSONCollection] = connection.database(dbName) map(_.collection[JSONCollection](collectionName))

  def ensureIndex(index: Index): Future[Boolean] = collection.flatMap {
    _.indexesManager.create(index) map { wr =>
      wr.writeErrors.foreach(mes => logger.info(s"[MongoDatabase] - [ensureIndex] - Failed to ensure index ${mes.errmsg}"))
      wr.ok
    } recover {
      case t =>
        logger.info(s"[MongoDatabase] - [ensureIndex] - Failed to ensure index", t)
        false
    }
  }

  def ensureIndexes: Future[Seq[Boolean]] = Future.sequence(indexes.map(ensureIndex))
  ensureIndexes
}
