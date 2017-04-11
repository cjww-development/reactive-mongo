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

package com.cjwwdev.mongo

import javax.inject.{Inject, Singleton}

import com.typesafe.config.ConfigFactory
import com.cjwwdev.logging.Logger
import play.api.libs.json.OFormat
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

sealed trait MongoCreateResponse
sealed trait MongoReadResponse
sealed trait MongoUpdatedResponse
sealed trait MongoDeleteResponse

case object MongoSuccessCreate extends MongoCreateResponse
case object MongoFailedCreate extends MongoCreateResponse

case class MongoSuccessRead(data : Any) extends MongoReadResponse
case object MongoFailedRead extends MongoReadResponse

case object MongoSuccessUpdate extends MongoUpdatedResponse
case object MongoFailedUpdate extends MongoUpdatedResponse

case object MongoSuccessDelete extends MongoDeleteResponse
case object MongoFailedDelete extends MongoDeleteResponse

@Singleton
class MongoConnector @Inject()() {
  private[mongo] val DATABASE_URI = Try(ConfigFactory.load.getString(s"mongo.uri")) match {
    case Success(uri) => uri
    case Failure(e) => throw e
  }
  private[mongo] val driver = new MongoDriver
  private[mongo] val parsedUri = MongoConnection.parseURI(DATABASE_URI).get
  private[mongo] val connection = driver.connection(parsedUri)
  private[mongo] val database = connection.database(parsedUri.db.get)
  private[mongo] def collection(name: String): Future[JSONCollection] = {
    database map {
      _.collection(name)
    }
  }

  def create[T](collectionName: String, data: T)(implicit format: OFormat[T]): Future[MongoCreateResponse] = {
    for {
      collection <- collection(collectionName)
      result <- collection.insert[T](data)
    } yield {
      if(result.ok) {
        MongoSuccessCreate
      } else {
        Logger.error(s"[MongoConnector] - [create] : Inserting document of type ${data.getClass} FAILED reason : ${result.writeConcernError.get.errmsg}")
        MongoFailedCreate
      }
    }
  }

  def read[T](collectionName: String, query: BSONDocument)(implicit format: OFormat[T]): Future[MongoReadResponse] = {
    for {
      collection <- collection(collectionName)
      result <- collection.find[BSONDocument](query).one[T]
    } yield result match {
      case Some(data) => MongoSuccessRead(data)
      case None =>
        Logger.info(s"[MongoConnector] - [read] : Query returned no results")
        MongoFailedRead
    }
  }

  def readBulk[T](collectionName: String, query: BSONDocument)(implicit format: OFormat[T]): Future[MongoReadResponse] = {
    for {
      collection <- collection(collectionName)
      result <- collection.find(query).cursor[T]().collect[List]()
    } yield {
      if(result.isEmpty) {
        Logger.info(s"[MongoConnector] - [read] : Query returned no results")
        MongoFailedRead
      } else {
        MongoSuccessRead(result)
      }
    }
  }

  def update[T](collectionName: String, selectedData: BSONDocument, data: T)(implicit format: OFormat[T]): Future[MongoUpdatedResponse] = {
    for {
      collection <- collection(collectionName)
      result <- collection.update(selectedData, data)
    } yield {
      if(result.ok) {
        MongoSuccessUpdate
      } else {
        Logger.error(s"[MongoConnector] - [update] : Updating a document in $collectionName FAILED reason : ${result.errmsg.get}")
        MongoFailedUpdate
      }
    }
  }

  def delete(collectionName: String, query: BSONDocument): Future[MongoDeleteResponse] = {
    for {
      collection <- collection(collectionName)
      result <- collection.remove(query)
    } yield {
      if(result.ok) {
        MongoSuccessDelete
      } else {
        Logger.error(s"[MongoConnector] - [delete] : Deleting a document from $collectionName FAILED reason : ${result.writeConcernError.get.errmsg}")
        MongoFailedDelete
      }
    }
  }
}
