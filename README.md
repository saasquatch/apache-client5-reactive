# apache-client5-reactive

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/saasquatch/apache-client5-reactive.svg?branch=master)](https://travis-ci.org/saasquatch/apache-client5-reactive)
[![](https://jitpack.io/v/saasquatch/apache-client5-reactive.svg)](https://jitpack.io/#saasquatch/apache-client5-reactive)

Thin wrapper around [Apache HttpComponents](https://hc.apache.org/) HttpAsyncClient 5.x to expose [Reactive Streams](https://www.reactive-streams.org) interfaces.

## Sample usage

```java
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.ByteBuffer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import com.saasquatch.client5reactive.HttpReactiveClient;
import com.saasquatch.client5reactive.HttpReactiveClients;
import io.reactivex.rxjava3.core.Single;

public class Example {

  public static void main(String[] args) throws Exception {
    // Create a CloseableHttpAsyncClient first
    try (CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault()) {
      /*
       * HttpReactiveClient does not support lifecycle management, so the underlying
       * CloseableHttpAsyncClient needs to be started and closed.
       */
      asyncClient.start();
      // HttpReactiveClient is just a thin wrapper
      final HttpReactiveClient reactiveClient = HttpReactiveClients.create(asyncClient);
      // Execute a simple in-memory request
      Single
          .fromPublisher(reactiveClient.execute(SimpleHttpRequests.get("https://www.example.com")))
          .doOnSuccess(response -> {
            // Get the response status and body in memory
            System.out.println(response.getCode());
            System.out.println(response.getBodyText());
          })
          .blockingSubscribe();
      System.out.println("----------");
      // Execute a streaming request
      // In this case, the request is a simple in-memory request without a request body
      Single
          .fromPublisher(
              reactiveClient.streamingExecute(SimpleHttpRequests.get("https://www.example.com")))
          .flatMapPublisher(message -> {
            // Get the status before subscribing to the streaming body
            System.out.println(message.getHead().getCode());
            return message.getBody();
          })
          // Collect the streaming body chunks into a List
          .toList()
          .map(byteBuffers -> {
            // Concatenate the body chunks and decode with UTF-8
            final int totalLength = byteBuffers.stream().mapToInt(ByteBuffer::remaining).sum();
            final ByteBuffer combined = ByteBuffer.allocate(totalLength);
            byteBuffers.forEach(combined::put);
            combined.flip();
            return UTF_8.decode(combined).toString();
          })
          .doOnSuccess(System.out::println)
          .blockingSubscribe();
    }
  }

}
```

The source code is in package [`com.saasquatch.client5reactive.examples`](https://github.com/saasquatch/apache-client5-reactive/tree/master/src/test/java/com/saasquatch/client5reactive/examples).

## Adding it to your project

### Add the repository

Maven

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Gradle

```gradle
repositories {
  maven { url 'https://jitpack.io' }
}
```

### Add the dependency

Maven

```xml
<dependency>
  <groupId>com.github.saasquatch</groupId>
  <artifactId>apache-client5-reactive</artifactId>
  <version>0.0.1</version>
</dependency>
```

Gradle

```gradle
compile 'com.github.saasquatch:apache-client5-reactive:0.0.1'
```

## License

Unless explicitly stated otherwise all files in this repository are licensed under the Apache License 2.0.

License boilerplate:

```
Copyright 2020 ReferralSaaSquatch.com Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
