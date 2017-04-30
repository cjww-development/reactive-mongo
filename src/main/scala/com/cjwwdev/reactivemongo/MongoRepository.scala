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
import reactivemongo.api.DB
import reactivemongo.api.indexes.Index
import reactivemongo.core.errors.GenericDatabaseException
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

abstract class MongoRepository(collectionName: String,
                               mongo: () => DB,
                               mc: Option[JSONCollection] = None) extends Indexes {

  lazy val collection: JSONCollection = mc.getOrElse(mongo().collection[JSONCollection](collectionName))

  ensureIndexes(scala.concurrent.ExecutionContext.Implicits.global)

  private val DuplicateKeyError = "E11000"
  private val message: String = "Failed to ensure index"

  private def ensureIndex(index: Index)(implicit ec: ExecutionContext): Future[Boolean] = {
    collection.indexesManager.create(index).map(wr => {
      if(!wr.ok) {
        val maybeMsg = for {
          msg <- wr.errmsg
          m <- if (msg.contains(DuplicateKeyError)) {
            throw GenericDatabaseException(msg, wr.code)
          }else Some(msg)
        } yield m
        Logger.error(s"[MongoRepository] - [ensureIndex] $message : '${maybeMsg.map(_.toString)}'")
      }
      wr.ok
    }).recover {
      case t =>
        Logger.error(s"[MongoRepository] - [ensureIndex] $message", t)
        false
    }
  }

  def ensureIndexes(implicit ec: ExecutionContext): Future[Seq[Boolean]] = {
    Future.sequence(indexes.map(ensureIndex))
  }
}
