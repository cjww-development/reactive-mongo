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

import com.cjwwdev.mongo.TestRepository
import com.cjwwdev.testing.unit.UnitTestSpec

class RepositoryIndexerSpec extends UnitTestSpec {

  class TestRepoImpl extends TestRepository {
    override lazy val mongoUri        = "mongodb://localhost:27017"
    override lazy val dbName          = "cjww-test-db"
    override lazy val collectionName  = "test-collection-name"
  }

  "RepositoryIndexer" should {
    "be successful" in {
      val testRepoIndexer = new RepositoryIndexer {
        override val repositories = Seq(new TestRepoImpl)
      }

      await(testRepoIndexer.runIndexing) mustBe Seq(true)
    }
  }
}
