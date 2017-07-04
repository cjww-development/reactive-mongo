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

import play.api.libs.json.Json
import reactivemongo.api.{DefaultDB, MongoDriver, MongoConnection => MConnect}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TestModel(modelId: String, testString1: String, testString2: String, testInt: Int)

object TestModel {
  implicit val format = Json.format[TestModel]
}

class TestRepository extends MongoRepository {

  override val collectionName = "TestCollection"

  override val driver = new MongoDriver
  override val database: Future[DefaultDB] = for {
    uri <- Future.fromTry(MConnect.parseURI(mongoUri))
    con =  driver.connection(uri)
    dn  <- Future(uri.db.get)
    db  <- con.database(dn)
  } yield db

  def insertTestModel(model: TestModel) = {
    collection flatMap {
      _.insert(model) map { wr =>
          if(wr.ok) MongoSuccessCreate else MongoFailedCreate
      }
    }
  }

  def findTestModel(id: String) = collection.flatMap {
    _.find(BSONDocument("modelId" -> id)).one[TestModel]
  }

  def updateTestModel(id: String, testString2Update: String) = {
    val selector = BSONDocument("modelId" -> id)
    val update = BSONDocument("$set" -> BSONDocument("testString2" -> testString2Update))
    collection.flatMap {
      _.update(selector, update) map { wr =>
        if(wr.ok) MongoSuccessUpdate else MongoFailedUpdate
      }
    }
  }

  def deleteTestModel(id: String) = collection flatMap {
    _.remove(BSONDocument("modelId" -> id)) map { wr =>
      if(wr.ok) MongoSuccessDelete else MongoFailedDelete
    }
  }
}
