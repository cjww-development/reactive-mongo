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

import javax.inject.{Inject, Singleton}

import com.cjwwdev.config.ConfigurationLoader
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TestModel(_id: String, string: String, int: Int)

object TestModel {
  implicit val testModelFormat: OFormat[TestModel] = (
    (__ \ "_id").format[String] and
    (__ \ "string").format[String] and
    (__ \ "int").format[Int]
  )(TestModel.apply, unlift(TestModel.unapply))
}

@Singleton
class TestRepository @Inject()(val configLoader: ConfigurationLoader) extends MongoDatabase {

  def create[T](data: T)(implicit format: OFormat[T]): Future[MongoCreateResponse] = collection.flatMap {
    _.insert[T](data).map(wr => if(wr.ok) MongoSuccessCreate else MongoFailedCreate)
  }

  def read[T](id: String)(implicit format: OFormat[T]): Future[Option[T]] = collection flatMap(_.find(BSONDocument("_id" -> id)).one[T])

  def update(id: String, stringUpdate: String): Future[MongoUpdatedResponse] = {
    val selector = BSONDocument("_id" -> id)
    val update = BSONDocument("$set" -> BSONDocument("string" -> stringUpdate))
    collection flatMap {
      _.update(selector, update) map { wr =>
        if(wr.ok) MongoSuccessUpdate else MongoFailedUpdate
      }
    }
  }

  def delete(id: String): Future[MongoDeleteResponse] = collection flatMap {
    _.remove(BSONDocument("_id" -> id)) map { wr =>
      if(wr.ok) MongoSuccessDelete else MongoFailedDelete
    }
  }
}
