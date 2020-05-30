package org.unitils.reflectionassert;

import static com.github.reflectionassert.MoreAssertions.assertFailing;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.unitils.reflectionassert.ReflectionAssert.assertAccessablePropertiesNotNull;

import org.junit.jupiter.api.Test;
import org.unitils.core.UnitilsException;


/**
 * Test the {@link ReflectionAssert}: method assertAccessablePropertiesNotNullTest.
 *
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
class ReflectionAssertAssertPropertiesNotNullTest {

  @Test
  void assertAccessablePropertiesNotNullTest_missingPublicProtected_and_PrivateVariables() {
    TestObject childObject1 = new TestObject("child");

    assertFailing(() ->
        assertAccessablePropertiesNotNull(
            "Accessable properties childobject ar not fully set",
            childObject1
        )
    );
  }

  @Test
  void assertAccessablePropertiesNotNull_missingPrivateVariable() {
    TestObject childObject1 = new TestObject("child");
    TestObject parentObject1 = new TestObject("name", 1L, 40, childObject1);

    assertAccessablePropertiesNotNull(
        "Accessable properties parentobject ar not fully set",
        parentObject1
    );

  }

  @Test
  void assertAccessablePropertiesNotNull_exceptionInvoke() {
    TestObject2 obj = new TestObject2(25);
    assertThrows(UnitilsException.class, () ->
        assertAccessablePropertiesNotNull("Fields are not accessible", obj)
    );
  }

  @SuppressWarnings({"unused", "PublicConstructorInNonPublicClass"})
  private class TestObject {

    public String name;
    public int age;
    protected Long id;
    TestObject childObject1;
    private String sickness;

    public TestObject(String name, Long id, int age) {
      this.name = name;
      this.id = id;
      this.age = age;
    }

    public TestObject(String name, Long id, int age, TestObject childObject) {
      super();
      this.name = name;
      this.id = id;
      this.age = age;
      this.childObject1 = childObject;
    }

    public TestObject(String name) {
      super();
      this.name = name;
    }

    public void setSickness(String sickness) {
      this.sickness = sickness;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }

    public TestObject getChildObject() {
      return childObject1;
    }

    public void setChildObject(TestObject childObject) {
      this.childObject1 = childObject;
    }
  }

  @SuppressWarnings({"PublicConstructorInNonPublicClass", "unused"})
  private class TestObject2 {

    private int age;

    public TestObject2(int age) {
      this.age = age;
    }

    private int getAge() {
      return age;
    }

    private void setAge(int age) {
      this.age = age;
    }
  }

}
