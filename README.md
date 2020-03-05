# apache-client5-reactive

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/saasquatch/apache-client5-reactive.svg?branch=master)](https://travis-ci.org/saasquatch/apache-client5-reactive)
[![](https://jitpack.io/v/saasquatch/apache-client5-reactive.svg)](https://jitpack.io/#saasquatch/apache-client5-reactive)

Thin wrapper around Apache HttpAsyncClient 5.x to expose Reactive Streams interfaces.

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
  <version>REPLACEME</version>
</dependency>
```

Gradle

```gradle
compile 'com.github.saasquatch:apache-client5-reactive:REPLACEME'
```

## License

Unless explicitly stated otherwise all files in this repository are licensed under the Apache License 2.0.

License boilerplate:

```
Copyright 2019 ReferralSaaSquatch.com Inc.

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
