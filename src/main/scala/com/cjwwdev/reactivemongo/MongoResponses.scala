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

sealed trait MongoCreateResponse
sealed trait MongoReadResponse
sealed trait MongoUpdatedResponse
sealed trait MongoDeleteResponse

case object MongoSuccessCreate extends MongoCreateResponse
case object MongoFailedCreate extends MongoCreateResponse

case object MongoSuccessUpdate extends MongoUpdatedResponse
case object MongoFailedUpdate extends MongoUpdatedResponse

case object MongoSuccessDelete extends MongoDeleteResponse
case object MongoFailedDelete extends MongoDeleteResponse

