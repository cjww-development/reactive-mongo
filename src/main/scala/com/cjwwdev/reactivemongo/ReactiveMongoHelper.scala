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

import reactivemongo.api
import reactivemongo.api._
import reactivemongo.core.nodeset.Authenticate

case class ReactiveMongoHelper(dbName: String,
                               servers: Seq[String],
                               auth: Seq[Authenticate],
                               failoverStrategy: Option[FailoverStrategy],
                               connectionOptions: MongoConnectionOptions = MongoConnectionOptions()) {

  lazy val driver = new MongoDriver

  lazy val connection: api.MongoConnection = driver.connection(
    servers,
    authentications = auth,
    options = connectionOptions
  )

  lazy val db: DefaultDB = failoverStrategy match {
    case Some(fs : FailoverStrategy) => DB(dbName, connection, fs)
    case None => DB(dbName, connection)
  }
}
