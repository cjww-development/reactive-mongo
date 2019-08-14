[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[ ![Download](https://api.bintray.com/packages/cjww-development/releases/reactive-mongo/images/download.svg) ](https://bintray.com/cjww-development/releases/reactive-mongo/_latestVersion)

reactive-mongo
=================

Mechanisms to connect to a MongoDB database (reactive mongo implementation)

To utilise this library add this to your sbt build file

```
"com.cjww-dev.libs" % "reactive-mongo_2.13" % "x.x.x" 
```

| Major Version | Scala Version |
|---------------|---------------|
| 0.x.x - 6.x.x | 2.11.x        |
| 7.x.x         | 2.12.x        |
| 8.x.x         | 2.13.x        |

## About
#### Configuration
Configuration for uri, database and collection is derived from the database repositories package structure.


```hocon
    package.structure {
      RepositoryClass {
        uri = ""
        database = ""
        collection = ""
      }
    }
```

#### com.cjwwdev.mongo.DatabaseRepository
Flatmapping **collection** from this trait class will grant access to mongo CRUD operations.

```scala
    class ExampleDataBaseRepository extends DatabaseRepository {
      def findById(id: String): Option[JsObject] = {
        collection flatMap {
          _.find(BSONDocument("_id" -> id)).one[JsObject]
        }
      }
    }
``` 

#### com.cjwwdev.mongo.indexes.RepositoryIndexer
To ensure each of your repositories indexes are ensured you need to implement RepositoryIndexer. Provide each of your repositories
in a sequence. like so

```scala
    class ExampleRepoIndexer extends RepositoryIndexer {
      override val repositories: Seq[DatabaseRepository] = Seq(TestRepo1, TestRepo2)
      
      runIndexing
    }
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
