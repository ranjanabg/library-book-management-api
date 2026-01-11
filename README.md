# Library Management Services

## Development Platform

* Backend: Java using JAX-RS framework
* Frontend: ReactJS

## Setup

### Database Setup

* Locate the `tcss559project.sql` file inside the workspace. Below command gives the path of this .sql file.

```bash
find . -name "tcss559project.sql
```
* Using the `tcss559project.sql` create the database and tables in mysql. Below command setups this resources.

```bash
mysql -u root -p < {tcss559project.sql path} 
```

### Service Setup

* Open eclipse
* Select `Library_Management` as workspace
* Import LibraryBooksManagement as project

## Running the service

* Update Maven project (Alt + F5)
* Click Google Cloud Platform button from the Eclipse toolbar and click "Run on App Engine"

## Directory 

src
├── main
│   ├── java
│   │   └── edu
│   │       └── uw
│   │           └── tcss559
│   │               ├── client
│   │               │   ├── AbstractClient.java
│   │               │   ├── MembersClient.java
│   │               │   └── NotificationClient.java
│   │               ├── common
│   │               │   ├── Calculate.java
│   │               │   ├── Constants.java
│   │               │   ├── Serializer.java
│   │               │   ├── Transformer.java
│   │               │   └── Validator.java
│   │               ├── controllers
│   │               │   ├── AbstractREST.java
│   │               │   ├── AnalyticsSOAP.java
│   │               │   ├── BookTransactionsREST.java
│   │               │   ├── BooksREST.java
│   │               │   ├── CorsFilter.java
│   │               │   ├── CriticREST.java
│   │               │   ├── HelloAppEngine.java
│   │               │   ├── LibraryBooksManagement.java
│   │               │   ├── MembersREST.java
│   │               │   ├── NotificationREST.java
│   │               │   ├── ProfileManagementREST.java
│   │               │   ├── RackRest.java
│   │               │   └── TopsisREST.java
│   │               ├── mcda
│   │               │   ├── CriticCore.java
│   │               │   └── TopsisCore.java
│   │               ├── store
│   │               │   ├── AbstractMySQLStore.java
│   │               │   ├── BookTransactionsMySQLStore.java
│   │               │   ├── BooksMySQLStore.java
│   │               │   ├── MembersMySQLStore.java
│   │               │   ├── OverdueFeesMySQLStore.java
│   │               │   └── RacksMySQLStore.java
│   │               └── structures
│   │                   ├── Book.java
│   │                   ├── BookCurrentLocation.java
│   │                   ├── BookIssueType.java
│   │                   ├── BookStatus.java
│   │                   ├── BooksStatistics.java
│   │                   ├── CheckoutVerify.java
│   │                   ├── Member.java
│   │                   ├── MemberStatus.java
│   │                   ├── MemberType.java
│   │                   ├── MembersStatistics.java
│   │                   ├── OverdueFees.java
│   │                   ├── Profile.java
│   │                   ├── Rack.java
│   │                   ├── mcda
│   │                   │   ├── Alternative.java
│   │                   │   ├── AlternativeResult.java
│   │                   │   ├── Attribute.java
│   │                   │   ├── AttributesResponse.java
│   │                   │   ├── DecisionInput.java
│   │                   │   └── TopsisScores.java
│   │                   └── notification
│   │                       ├── Email.java
│   │                       ├── EmailContent.java
│   │                       ├── EmailDestination.java
│   │                       └── EmailRequest.java
│   └── webapp
│       ├── Library\ Books\ Management\ Services.postman_collection.json
│       ├── Library\ Books\ Management\ Services.postman_collection_backup.json
│       ├── META-INF
│       │   └── MANIFEST.MF
│       ├── WEB-INF
│       │   ├── appengine-web.xml
│       │   ├── lib
│       │   ├── logging.properties
│       │   ├── sun-jaxws.xml
│       │   └── web.xml
│       ├── favicon.ico
│       ├── index.html
│       └── tcss559project.sql
└── test
    └── java
        └── uw
            └── tcss559
                └── controllers

## License
[MIT](https://choosealicense.com/licenses/mit/)
