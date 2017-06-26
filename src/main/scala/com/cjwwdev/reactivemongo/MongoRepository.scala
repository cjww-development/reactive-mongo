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
import reactivemongo.api.indexes.Index
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

abstract class MongoRepository(collectionName: String) extends Indexes with MongoConnection {
  lazy val collection: Future[JSONCollection] = database map(_.collection[JSONCollection](collectionName))

  private val duplicateKeyError = "E11000"
  private val message = "Failed to ensure index"

  private def ensureIndex(index: Index)(implicit ec: ExecutionContext): Future[Boolean] = {
    collection flatMap {
      _.indexesManager.create(index) map { wr =>
        wr.writeErrors foreach { mes =>
          Logger.info(s"[MongoRepository] - [ensureIndex] $message ${mes.errmsg}")
        }
        wr.ok
      } recover {
        case t =>
          Logger.info(s"[MongoRepository] - [ensureIndex] $message", t)
          false
      }
    }
  }

  def ensureIndexes(implicit ec: ExecutionContext): Future[Seq[Boolean]] = {
    Future.sequence(indexes.map(ensureIndex))
  }
  ensureIndexes
}
