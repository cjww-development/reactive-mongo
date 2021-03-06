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

import com.cjwwdev.mongo.DatabaseRepository
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}

trait RepositoryIndexer extends {
  val repositories: Seq[DatabaseRepository]

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private def ensureMultipleIndexes(repo: DatabaseRepository)(implicit ec: ExecutionContext): Future[Seq[Boolean]] = {
    Future.sequence(repo.indexes map repo.ensureSingleIndex)
  }

  def runIndexing(implicit ec: ExecutionContext): Future[Seq[Boolean]] = {
    Future.sequence(repositories map { repo =>
      ensureMultipleIndexes(repo) map { boolSeq =>
        if(boolSeq.contains(false)) {
          logger.error(s"There was a problem ensuring one or more of the indexes for ${repo.getClass.getCanonicalName}")
        } else {
          logger.info(s"Indexes ensure for repository ${repo.getClass.getCanonicalName}")
        }
        boolSeq
      }
    }) map(_.flatten)
  }
}
