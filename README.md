# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+uB5afJCIJqTsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3l5d5LsKMBihKboynKZbvEqgW8sFlnWZFW4qsGGoAOreA4MBNPofw7NuHmWf6zLTJeUYxnGhRaUmlSiWmGYETmqh5vM0FFiW9SbNljjGQCDb0UVgrDvy9SHnIKDPvE56Xteo2LpUy4BmuAZzVu7ntjppaOeKGSqABmDbSB1S6YR+nzCRqHfBRVH1ldtHofCybIKmMC4fhoznbFxGkTdl53chD1oUNnjeH4-heCg6AxHEiRQzDjm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDLdiHoE92nmf6ZNIUd1Pwr6zpdjZQnIw5bOns5aiucNnkJWNMCMmA02zfB5NoHOQUOiF9ThU+63yLK8q0xTC0CnVHZWS6a0vhtzMjbegscCg3DHpeYuURLUsC4tQr1BkMwQDQutXvrnZbQz9RUBASAMcdmvozAIyaadz31a92HvXhWZ0U2YPMf4KLrv42Dihq-FojAADiSoaKjJWlg02e4wT9hKqTAMS5TH5e8aVd0wHTOdvUyA5LnOYOWiHdqNzJJ81SRv0kLTKi6rkvxQuGtLaF8uu1FKsN2rQ8a0lOvTa+Bv81Prfd3nGI21P96hRkFioC7JoIDnSoeurJ0twJaIADw9zy5R87XsL+pn7d5-th0B0Li8MY5ccwFgaOMUBKAACS0gFggk2PEXUKA3Sci+CCZIoA1SoMgoZZYUCAByuDaKdFDhJcO8BI5gBwjHL6UDVDgMgUqWB8DliIOQTggyMFliYJANgoi7VuEgKVEQrhlxSGNgYgnCGHAADsbgnAoCcDECMwQ4BcQAGzwAnIYHuMAihULRmHDGrQOhlwruVcWSEsyEKVGQxMlRtr1HHgRWxYiGxNy1tZCa6Ie4YjgDonufdeabW3t5EeIsLbj0PvuY+ctxQKz1kraK49J6xMDszeoG93baw8oPaWw8fEoD8VA+aK84krXXK-HJg5P5fnqAEo8xSlT-wQIBBm98ajAKgSw2o9iMKawatHT6TC5i9LjlIpiENLCm1spsWGSAEhgBmX2CA8yABSvs0DXzmDEXhaoDElDEs3LpjQmjMhkj0KBlcrHoCzNgBAwAZlQDgBAWyUA1g9OkP0xmDwv6lhcV9B5TzKCvPeaw4RYy4F9Ppv8oxD8ABWWy-GbPFEElAhIeZuS3vk22o4IljyXhPO+sswoJPntoZW9dbmFBJWvFm2T5ADxkCvHyYASnMOkDE4KM94kSmqck+UXy0mJROdZAVwBmXq3qH4LQvilQYlKdoblMteWSmwHK3RN8anvkcXXH2ftYVfk6bpH5mEqE0JGVcSRjFwYBC8E8hZSyHXykQMGWAwBsAPMIHkAo+iC4nMkljHGeMCbGBrnq-53tfb+w6fS8a3A8AHylaymAIBE1QGTSStVbq8CVivnKyVoS6lwmjYawBgawJmsGRa4ZscbVAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
