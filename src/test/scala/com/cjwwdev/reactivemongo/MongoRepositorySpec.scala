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

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class MongoRepositorySpec extends PlaySpec with GuiceOneAppPerSuite {
  def await[T](awaitable: Awaitable[T]) = Await.result(awaitable, 5.seconds)

  val testRepository = new TestRepository

  "insertTestModel" should {
    "insert test model 1 into the database" in {
      val insert = TestModel("Id1", "testOne", "testTwo", 1)

      val result = await(testRepository.insertTestModel(insert))
      result mustBe MongoSuccessCreate
    }

    "insert test model 2 into the database" in {
      val insert = TestModel("Id2", "testOne", "testTwo", 2)

      val result = await(testRepository.insertTestModel(insert))
      result mustBe MongoSuccessCreate
    }

    "insert test model 3 into the database" in {
      val insert = TestModel("Id3", "testOne", "testTwo", 3)

      val result = await(testRepository.insertTestModel(insert))
      result mustBe MongoSuccessCreate
    }

    "insert test model 4 into the database" in {
      val insert = TestModel("Id4", "testOne", "testTwo", 4)

      val result = await(testRepository.insertTestModel(insert))
      result mustBe MongoSuccessCreate
      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }
  }

  "updateTestModel" should {
    "update a test model with id 'Id101'" in {
      val testData = TestModel("Id101", "testOne", "testTwo", 101)

      await(testRepository.insertTestModel(testData))

      val result = await(testRepository.updateTestModel("Id101", "UPDATED_STRING"))
      result mustBe MongoSuccessUpdate

      val res = await(testRepository.collection flatMap(_.find(BSONDocument("modelId" -> "Id101")).one[TestModel]))
      res.get.testString2 mustBe "UPDATED_STRING"
      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }
  }

  "deleteTestModel" should {
    "remove a document with a specified id" in {
      val testData = TestModel("Id616", "testOne", "testTwo", 616)
      await(testRepository.insertTestModel(testData))
      val result = await(testRepository.deleteTestModel("Id616"))
      result mustBe MongoSuccessDelete
      val res = await(testRepository.findTestModel("Id616"))
      res mustBe None
      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }
  }
}
