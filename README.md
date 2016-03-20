# thistle
Thistle is a JUnit runner designed to allopw developers to make their test more readable and more structured. It does not 
enforce a particular specification test style but allows the developer to have the tools to express them in a better way.

A thistle test looks like this:
```java
@RunWith(ThistleRunner.class)
@Describe
public class TestClassTest {

    TestClass instance;
    @Describe
    public class C1 {
        @When("TestClass is initialised with true")
        public void w() {
            instance = new TestClass(true);
        }

        @Then("a multiplies by 2")
        public void t1(){
            assertThat(instance.a(2),equalTo(4));
        }

        @Then("b multiplies by 5")
        public void t2() {
            assertThat(instance.b(3), equalTo(15));
        }
    }
}
```

The project has been deeply influenced by [Jasmine](http://jasmine.github.io/) and [Oleaster](https://github.com/mscharhag/oleaster).

## Why thistle
I love unit tests. They are the foundation for good quality code and I see them as a safety net: it doesn't matter how much I refactor
the code, as long as my suite passes I'm fine! Having unit tests that are easy to read is a little bit complicated though.
If you use the "should" approach in JUnit you will end up writing tons of methods like the following:
```java
@Test
public void shouldBehaveInACertainManner() {
  // init object
  // execute testee
  // verify property
}
```

When the code gets complicated however this approach, even if good in theory, causese some problems:
```java
@Test
public void shouldBehaveThisWayAssumingThatThisConditionAndAlsoThisConditionAreMet() {
  //long initialisation part
  ...
  // execute things
  ...
  // long verification part
}
```

This is caused by the fact that there is no clear separation between precondition, test actions and verifications. Another big 
problem is that the name of the methods become quickly hard to read and interpret by humans, so you don't really understand
the beahviour that the test is trying to describe.

Thistle tries to solve all of these problems at once using an approach inspired by Jasmine: define the test description, define the
test precondition and describe them, describe and execute the action of the tests and finally verify conditions.

In the example above, once executed in JUnit (or in the surefire report in Maven), the tests will have the following names:

- "When TestClass is initialised with true, then a multiplies by 2"
- "When TestClass is initialised with true, then b multiplies by 5"

And this is only the beginning.

## Test styles
Thistle is very flexible and allows the developer to use it at will either to improve his/hers tests or to use a more sophisticated 
way of describing tests.

Have a look at [ImprovedJUnitTest](https://github.com/fburato/thistle/blob/master/src/test/java/thistleexamples/ImprovedJUnitTest.java) 
included in the repository.

```java
@RunWith(ThistleRunner.class)
@Describe
public class ImprovedJUnitTest {

    private int i;

    @When("i is initialised to 2")
    public void setUp() {
        i = 2;
    }

    @Then("i times 3 is 6")
    public void t1() {
        assertThat(i * 3, equalTo(6));
    }

    @Then("i times 4 is 8")
    public void t2() {
        assertThat(i * 4, equalTo(8));
    }

    @Then("i times 5 is 10")
    public void t3() {
        assertThat(i * 5, equalTo(10));
    }
}
```

This doesn't look particularly different from a JUnit style of specification. However the thistle runner will process the annotation
and will produce the following tests:
- When i is initialised to 2, then i times 3 is 6
- When i is initialised to 2, then i times 4 is 8
- When i is initialised to 2, then i times 5 is 10

However thistle allows you to improve your test organisation significantly.

Have a look at [JasmineStyleTests](https://github.com/fburato/thistle/blob/master/src/test/java/thistleexamples/JasmineStyleTests.java) 
included in the repository.

```java
@RunWith(ThistleRunner.class)
@Describe
public class JasmineStyleTests {
    private int i = 1;

    @Describe("even number tests")
    public class EvenNumber {

        @When("i is an even number")
        public void w() {
            i = 2;
        }

        @Then("multiply i by an even number")
        public void t1() {
            i *= 2;
        }

        @Then("multiply i by an odd number")
        public void t2() {
            i *= 3;
        }

        @Finally("i is still even")
        public void f() {
            assertThat(i % 2 == 0, is(true));
        }
    }

    @Describe("odd number times even number")
    public class OddNumber1 {
        @When("i is an odd number")
        public void w() {
            i = 3;
        }

        @Then("multiply i by an even number")
        public void t() {
            i *= 2;
        }

        @Finally("i is even")
        public void f() {
            assertThat(i % 2 == 0, is(true));
        }
    }

    @Describe("odd number times odd number")
    public class OddNumber2 {
        @When("i is an odd number")
        public void w() {
            i = 3;
        }

        @Then("multiply i by an odd number")
        public void t() {
            i *= 5;
        }

        @Finally("i is odd")
        public void f() {
            assertThat(i % 2 == 0, is(false));
        }
    }
}
```

Thistle will parse this test specification and produce the following tests:
- odd number times odd number: When i is an odd number, then multiply i by an odd number. Finally i is odd
- odd number times even number: When i is an odd number, then multiply i by an even number. Finally i is even
- even number tests: When i is an even number, then multiply i by an even number. Finally i is still even
- even number tests: When i is an even number, then multiply i by an odd number. Finally i is still even

## Installation
The project has not been hosted in a maven repository yet, however if you want to try thistle out:

1. Download the source code
2. Run in the project directory ```mvn clean install```
3. Include thistle in your project adding the dependency:
```
  <dependency>
     <groupId>thistle</groupId>
     <artifactId>thistle</artifactId>
     <version>1.0-SNAPSHOT</version>
     <scope>test</scope>
   </dependency>
```

Profit!

## Usage
In order for a test class to be run with thistle, it must be annotated with ```@Runwith(ThistleRunner.class)``` and ```@Describe```
and it must be public.

The thistle blocks are identified by annotations ```@Describe```, ```@When```, ```@Then```, ```@Finally```. ```@Describe``` is a class
only type of annotation while the others are method only annotations. Everything annotated with thistle
annotation must be public.

The number of test cases run is the number of ```@Then``` annotation present in the root test class or any of the 
inner classes annotated with ```@Described``` (at any level of nesting). Each test cases will execute:

1. The initialisation of the super class
2. All the ```@When``` annotated methods that are present in the super class in the order they are defined
3. The initialisation and all the ```@When``` annotated methods that are present int all nested classes to reach the test case
4. The ```@Then``` annotated method
5. All ```@Finally``` annotated methods from the innermost class to the root class.

```@Then``` is the only method that requires a String value in specification, all the other annotation values are optional.

## License

This software is licensed under the Apache 2 license, quoted below.

Copyright 2016 Francesco Burato

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
