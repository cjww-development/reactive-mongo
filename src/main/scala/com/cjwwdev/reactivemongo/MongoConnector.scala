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

import com.typesafe.config.ConfigFactory
import reactivemongo.api.{DefaultDB, FailoverStrategy, MongoConnection, MongoDriver}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MongoConnector @Inject()() {

  val connectionUri: String = ConfigFactory.load.getString("mongo.uri")
  val failoverStrategy: Option[FailoverStrategy] = None

  private val driver: MongoDriver = new MongoDriver
  private val parsedUri: MongoConnection.ParsedURI = MongoConnection.parseURI(connectionUri).get
  private val connection: MongoConnection = driver.connection(parsedUri)
  private val database: DefaultDB = Await.result(connection.database(parsedUri.db.get), 30.seconds)

  implicit def db: () => DefaultDB = () => database
}
