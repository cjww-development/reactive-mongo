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
package com.cjwwdev.mongo

import com.cjwwdev.mongo.responses.{MongoSuccessCreate, MongoSuccessDelete, MongoSuccessUpdate}
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class DatabaseRepositoryISpec extends AnyFlatSpec with Matchers {
  val testRepository: TestRepository = new TestRepository {
    override lazy val mongoUri       = "mongodb://localhost:27017"
    override lazy val dbName         = "cjww-test-db"
    override lazy val collectionName = "test-collection-name"
    override implicit val ec         = ExecutionContext.global
  }

  "insertTestModel" should ""

  "insertTestModel" should {
    await(testRepository.collection map(_.drop(failIfNotFound = false)))

    "insert test model 1 into the database" in {
      val insert = TestModel("Id1", "testOne", 1)

      val result = await(testRepository.create(insert))
      result mustBe MongoSuccessCreate

      val verify = await(testRepository.read[TestModel]("Id1"))
      verify mustBe Some(insert)

      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }

    "insert test model 2 into the database" in {
      val insert = TestModel("Id2", "testOne", 2)

      val result = await(testRepository.create(insert))
      result mustBe MongoSuccessCreate

      val verify = await(testRepository.read[TestModel]("Id2"))
      verify mustBe Some(insert)

      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }

    "insert test model 3 into the database" in {
      val insert = TestModel("Id3", "testOne", 3)

      val result = await(testRepository.create(insert))
      result mustBe MongoSuccessCreate

      val verify = await(testRepository.read[TestModel]("Id3"))
      verify mustBe Some(insert)

      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }

    "insert test model 4 into the database" in {
      val insert = TestModel("Id4", "testOne", 4)

      val result = await(testRepository.create(insert))
      result mustBe MongoSuccessCreate

      val verify = await(testRepository.read[TestModel]("Id4"))
      verify mustBe Some(insert)

      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }
  }

  "updateTestModel" should {
    "update a test model with id 'Id101'" in {
      val testData = TestModel("Id101", "UPDATED_STRING", 101)

      await(testRepository.create[TestModel](testData.copy(string = "test")))
      val insVerify = await(testRepository.read[TestModel]("Id101"))
      insVerify mustBe Some(testData.copy(string = "test"))

      val result = await(testRepository.update("Id101", "UPDATED_STRING"))
      result mustBe MongoSuccessUpdate

      val verify = await(testRepository.read[TestModel]("Id101"))
      verify mustBe Some(testData)

      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }
  }

  "deleteTestModel" should {
    "remove a document with a specified id" in {
      val testData = TestModel("Id616", "testOne", 616)

      await(testRepository.create[TestModel](testData))
      val insVerify = await(testRepository.read[TestModel]("Id616"))
      insVerify mustBe Some(testData)

      val result = await(testRepository.delete("Id616"))
      result mustBe MongoSuccessDelete

      val verify = await(testRepository.read[TestModel]("Id616"))
      verify mustBe None

      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }
  }
}
