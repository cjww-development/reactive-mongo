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

import reactivemongo.api.{DefaultDB, FailoverStrategy, MongoConnection}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

case class MongoConnector(connectionUri: String, failoverStrategy: Option[FailoverStrategy]) extends MongoConnectionSettings

trait MongoConnectionSettings {

  val connectionUri: String
  val failoverStrategy: Option[FailoverStrategy]

  implicit def db: () => DefaultDB = () => mongoDb

  private lazy val mongoDb = connect

  private def connect = rMh.db

  lazy val rMh: ReactiveMongoHelper = MongoConnection.parseURI(connectionUri) match {
    case Success(MongoConnection.ParsedURI(hosts, options, ignoreOptions, Some(db), auth)) =>
      ReactiveMongoHelper(db, hosts.map(h => h._1 + ":" + h._2), auth.toList, failoverStrategy, options)
    case Success(MongoConnection.ParsedURI(_, _, _, None, _)) =>
      throw new Exception(s"Missing database name in mongodb.uri '$connectionUri'")
    case Failure(e) => throw new Exception(s"Invalid mongodb.uri '$connectionUri'", e)
  }

  def closeConnection(): Future[_] = {
    Await.ready(rMh.connection.askClose()(10.seconds), 10.seconds)
  }
}
