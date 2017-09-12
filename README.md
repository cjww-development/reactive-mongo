[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[ ![Download](https://api.bintray.com/packages/cjww-development/releases/reactive-mongo/images/download.svg) ](https://bintray.com/cjww-development/releases/reactive-mongo/_latestVersion)

reactive-mongo
=================

Mechanisms to connect to a MongoDB database (reactive mongo implementation)

To utilise this library add this to your sbt build file

```
"com.cjww-dev.libs" % "reactive-mongo_2.11" % "3.4.0" 
```

## About
#### Configuration
Add this snippet to your application.conf file.

```hocon
    microservice {
      mongo {
        uri = ""
      }
    }
```

#### MongoDatabase.scala
Flatmapping **collection** from this abstract class will grant access to mongo CRUD operations.

```scala
    class ExampleMongoRepository extends MongoDatabase("example-collection") {
      
      def findById(id: String): Option[JsObject] = {
        collection flatMap {
          _.find(BSONDocument("_id" -> id)).one[JsObject]
        }
      }
    }
``` 

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
