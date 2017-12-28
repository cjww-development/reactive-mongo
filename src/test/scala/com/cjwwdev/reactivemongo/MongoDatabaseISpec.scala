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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.cjwwdev.mocks.MongoMocks
import org.scalatest.BeforeAndAfter
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Application, Configuration}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.ahc.AhcWSClient
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable}

class MongoDatabaseISpec extends PlaySpec with MockitoSugar with MongoMocks with BeforeAndAfter with GuiceOneAppPerSuite {
  def await[T](awaitable: Awaitable[T]) = Await.result(awaitable, 5.minute)

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val ws = AhcWSClient()

  val testRepository = new TestRepository {
    override val loadedConfig = Configuration()
    override lazy val mongoUri       = "mongodb://localhost:27017"
    override lazy val dbName         = "reactive-mongo-test-db"
    override lazy val collectionName = "test-collection"
  }

  implicit val request = FakeRequest()

  "insertTestModel" should {
    await(testRepository.collection map(_.drop(failIfNotFound = false)))
    "insert test model 1 into the database" in {
      val insert = TestModel("Id1", "testOne", 1)

      val result = await(testRepository.create(insert))
      result mustBe MongoSuccessCreate
      //await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }

    "insert test model 2 into the database" in {
      val insert = TestModel("Id2", "testOne", 2)

      val result = await(testRepository.create(insert))
      result mustBe MongoSuccessCreate
      //await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }

    "insert test model 3 into the database" in {
      val insert = TestModel("Id3", "testOne", 3)

      val result = await(testRepository.create(insert))
      result mustBe MongoSuccessCreate
      //await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }

    "insert test model 4 into the database" in {
      val insert = TestModel("Id4", "testOne", 4)

      val result = await(testRepository.create(insert))
      result mustBe MongoSuccessCreate
      //await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }
  }

  "updateTestModel" should {
    "update a test model with id 'Id101'" in {
      val testData = TestModel("Id101", "testOne", 101)

      val result = await(testRepository.update("Id101", "UPDATED_STRING"))
      result mustBe MongoSuccessUpdate
    }
  }

  "deleteTestModel" should {
    "remove a document with a specified id" in {
      val testData = TestModel("Id616", "testOne", 616)

      val result = await(testRepository.delete("Id616"))
      result mustBe MongoSuccessDelete
    }
  }
}