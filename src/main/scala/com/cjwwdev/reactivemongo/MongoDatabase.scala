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

import com.typesafe.config.ConfigFactory
import play.api.Logger
import reactivemongo.api.indexes.Index
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class MongoDatabase(collectionName: String) {
  private lazy val mongoUri = ConfigFactory.load.getString("microservice.mongo.uri")
  lazy val uri              = MongoConnection.parseURI(mongoUri).get

  lazy val dbName           = uri.db.getOrElse("cjww-industries")

  val driver                = new MongoDriver()
  val connection            = driver.connection(uri)

  private val message       = "Failed to ensure index"

  def indexes: Seq[Index]   = Seq.empty

  def collection: Future[JSONCollection] = connection.database(dbName) map(_.collection[JSONCollection](collectionName))

  def ensureIndex(index: Index): Future[Boolean] = collection.flatMap {
    _.indexesManager.create(index) map { wr =>
      wr.writeErrors.foreach(mes => Logger.info(s"[MongoDatabase] - [ensureIndex] $message ${mes.errmsg}"))
      wr.ok
    } recover {
      case t =>
        Logger.info(s"[MongoDatabase] - [ensureIndex] $message", t)
        false
    }
  }

  def ensureIndexes: Future[Seq[Boolean]] = Future.sequence(indexes.map(ensureIndex))
  ensureIndexes
}
