Reflection Assert for JUnit 5
=======

[![Release](https://jitpack.io/v/hpple/reflection-assert.svg)](https://jitpack.io/#hpple/reflection-assert)
[![Build Status](https://travis-ci.org/hpple/reflection-assert.svg?branch=master)](https://travis-ci.org/hpple/reflection-assert)

Fork of reflection assert from Unitils (http://www.unitils.org) compatible with JUnit 5.

Usage
----------------
#### reflection-assert
Assert that two objects are equal using reflection for field-by-field comparision:

```
assertReflectiveThat(actual).isEqualTo(expected);
```
Same, but ignoring order for collections:

```
assertLenientThat(actual).isEqualTo(expected);
```
Or assert that two objects are not equal providing custom message:
```
assertReflective().withMessage("message").that(actual).isNotEqualTo(unexpected);
```

A report for a failed assertion will be look like:

```
Expected: User<name="John", title="CTO", address=Address<city="NY", street="2th">>
  Actual: User<name="John", title="Officer", address=Address<city="NY", street="1th">>

--- Found following differences ---
address.street: expected: "2th", actual: "1th"
title: expected: "CTO", actual: "Officer"

--- Difference detail tree ---
 expected: User<name="John", title="CTO", address=Address<city="NY", street="2th">>
   actual: User<name="John", title="Officer", address=Address<city="NY", street="1th">>

address expected: Address<city="NY", street="2th">
address   actual: Address<city="NY", street="1th">

address.street expected: "2th"
address.street   actual: "1th"

title expected: "CTO"
title   actual: "Officer"
```

#### reflection-assert-vintage
Provides reflection assertions in conformance with vintage Unitils API for smooth migration.

You may treat this module as a drop-in replacement for Unitils if the reflection assert was the only package used from Unitils in your system.

#### reflection-assert-comparator
Additionally, you may want to compare two objects reflectively out of the test scope without getting dependency on JUnit and other test-related libs. 
Or maybe just get the difference report without any assertions involved.

You may achieve it by depending on this module only & directly using ReflectionComparator that could be obtained with opt-in leniency modes:
```
Difference diff = ReflectionComparatorFactory.createReflectionComparator().getDifference(a, b)
```
```
ReflectionComparatorFactory.createReflectionComparator(LENIENT_ORDER, LENIENT_DATES).isEqual(a, b)
```
Reports could be generated in the same format as for assertions:
```
new DefaultDifferenceReport().createReport(difference)
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
##### reflection-assert
```gradle
testCompile 'com.github.hpple.reflection-assert:reflection-assert:0.2'
```
##### reflection-assert-vintage
```gradle
testCompile 'com.github.hpple.reflection-assert:reflection-assert-vintage:0.2'
```
##### reflection-comparator
```gradle
compile 'com.github.hpple.reflection-assert:reflection-comparator:0.2'
```
or
```gradle
testCompile 'com.github.hpple.reflection-assert:reflection-comparator:0.2'
```

#### Maven
##### reflection-assert
```xml
<dependency>
    <groupId>com.github.hpple.reflection-assert</groupId>
    <artifactId>reflection-assert</artifactId>
    <version>0.2</version>
    <scope>test</scope>
</dependency>
```
##### reflection-assert-vintage
```xml
<dependency>
    <groupId>com.github.hpple.reflection-assert</groupId>
    <artifactId>reflection-assert-vintage</artifactId>
    <version>0.2</version>
    <scope>test</scope>
</dependency>
```
##### reflection-comparator
```xml
<dependency>
    <groupId>com.github.hpple.reflection-assert</groupId>
    <artifactId>reflection-comparator</artifactId>
    <version>0.2</version>
</dependency>
```
or
```xml
<dependency>
    <groupId>com.github.hpple.reflection-assert</groupId>
    <artifactId>reflection-comparator</artifactId>
    <version>0.2</version>
    <scope>test</scope>
</dependency>
```
