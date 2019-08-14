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

package com.cjwwdev.mongo.indexes

import com.cjwwdev.mongo.connection.Collection
import org.slf4j.{Logger, LoggerFactory}
import reactivemongo.api.indexes.Index

import scala.concurrent.{ExecutionContext, Future}

trait Indexing {
  self: Collection =>

  def indexes: Seq[Index] = Seq.empty

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def ensureSingleIndex(index: Index)(implicit ec: ExecutionContext): Future[Boolean] = collection flatMap {
    _.indexesManager.create(index) map { wr =>
      wr.writeErrors.foreach(mes => logger.error(s"Indexing - Failed to ensure index ${index.name} => ${mes.errmsg}"))
      wr.ok
    } recover {
      case t =>
        logger.error(s"Indexing - Failed to ensure ${index.name}", t)
        false
    }
  }
}
