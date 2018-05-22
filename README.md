Reflection Assert for JUnit 5
=======

[![Release](https://jitpack.io/v/hpple/reflection-assert.svg)](https://jitpack.io/#hpple/reflection-assert)
[![Build Status](https://travis-ci.org/hpple/reflection-assert.svg?branch=master)](https://travis-ci.org/hpple/reflection-assert)

Fork of reflection assert from Unitils (http://www.unitils.org) compatible with JUnit 5.

Usage
----------------
Assert that two objects are equal using reflection for field-by-field comparision:

```java
assertReflectiveThat(actual).isEqualTo(expected);
```
Same, but ignoring order for collections:

```java
assertLenientThat(actual).isEqualTo(expected);
```
Or assert that two objects are not equal providing custom message:
```java
assertReflective().withMessage("message").that(actual).isNotEqualTo(unexpected);
```

Dependency
----------------

First of all, add [JitPack](https://jitpack.io/) to your project:

#### Gradle
```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}
```
#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Then add the dependency:

#### Gradle
```gradle
testCompile 'com.github.hpple:reflection-assert:0.1'
```

#### Maven
```xml
<dependency>
    <groupId>com.github.hpple</groupId>
    <artifactId>reflection-assert</artifactId>
    <version>0.1</version>
</dependency>
```

