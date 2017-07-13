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
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Mockito.when
import org.mockito.Mockito.reset
import org.mockito.ArgumentMatchers
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.ws.ahc.AhcWSClient
import reactivemongo.api.{DefaultDB, MongoDriver}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{Await, Awaitable, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class MongoRepositorySpec extends PlaySpec with MockitoSugar with MongoMocks with BeforeAndAfter with GuiceOneAppPerSuite {
  def await[T](awaitable: Awaitable[T]) = Await.result(awaitable, 5.seconds)

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val ws = AhcWSClient()

  val mockDriver = mock[MongoDriver]
  val mockDatabase = mock[DefaultDB]
  val mockCollection = mock[JSONCollection]
  val mockCollectionName = "TestCollection"

  val testRepository = new TestRepository {
    override val driver = mockDriver
    override val database = Future.successful(mockDatabase)
    override lazy val collection = Future.successful(mockCollection)
  }

  before(reset(mockCollection))

  "insertTestModel" should {
    val success = mockWriteResult(true)

    "insert test model 1 into the database" in {
      val insert = TestModel("Id1", "testOne", "testTwo", 1)

      when(mockCollection.insert(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(success))

      val result = await(testRepository.insertTestModel(insert))
      result mustBe MongoSuccessCreate
    }

    "insert test model 2 into the database" in {
      val insert = TestModel("Id2", "testOne", "testTwo", 2)

      when(mockCollection.insert(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(success))

      val result = await(testRepository.insertTestModel(insert))
      result mustBe MongoSuccessCreate
    }

    "insert test model 3 into the database" in {
      val insert = TestModel("Id3", "testOne", "testTwo", 3)

      when(mockCollection.insert(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(success))

      val result = await(testRepository.insertTestModel(insert))
      result mustBe MongoSuccessCreate
    }

    "insert test model 4 into the database" in {
      val insert = TestModel("Id4", "testOne", "testTwo", 4)

      when(mockCollection.insert(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(success))

      val result = await(testRepository.insertTestModel(insert))
      result mustBe MongoSuccessCreate
      await(testRepository.collection map(_.drop(failIfNotFound = false)))
    }
  }

  "updateTestModel" should {
    val successUpdate = mockUpdateWriteResult(true)

    "update a test model with id 'Id101'" in {
      val testData = TestModel("Id101", "testOne", "testTwo", 101)

      when(mockCollection.update(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(successUpdate))

      val result = await(testRepository.updateTestModel("Id101", "UPDATED_STRING"))
      result mustBe MongoSuccessUpdate
    }
  }

  "deleteTestModel" should {
    "remove a document with a specified id" in {
      val success = mockWriteResult(true)
      val testData = TestModel("Id616", "testOne", "testTwo", 616)

      when(mockCollection.remove(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(success))

      val result = await(testRepository.deleteTestModel("Id616"))
      result mustBe MongoSuccessDelete
    }
  }
}