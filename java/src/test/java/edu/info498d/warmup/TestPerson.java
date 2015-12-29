package edu.info498d.warmup;

import java.beans.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestPerson {
  @Test
  public void exerciseTheBasics() {
    Person p = new Person();
    p.setAge(20);
    p.setName("Alice Lee");
    p.setSalary(195750.22);
    
    assertEquals(20, p.getAge());
    assertEquals("Alice Lee", p.getName());
    
    assertEquals(215325.242, p.calculateBonus(), 0.01);
    assertEquals("The Honorable Alice Lee", p.becomeJudge());

    p.timeWarp();
    assertEquals(30, p.getAge());
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void setAgeToBeNegative() {
    Person p = new Person();
    p.setAge(-20);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void setNameToNull() {
    Person p = new Person();
    p.setName(null);
  }
  
  @Test
  public void viewReadabletoString() {
    Person p = new Person();
    p.setAge(20);
    p.setName("Alice Lee");
    p.setSalary(195750.22);
    
    assertEquals("Person[name:Alice Lee;age:20;salary:195750.22]", p + "");
  }
  
  @Test
  public void exercisePesonEquals() {
    Person p1 = new Person("Alice", 43, 250000);
    Person p2 = p1;
    assertEquals(p1, p2);
    
    Person p3 = new Person("Alice", 43, 250000);
    assertEquals(p1, p3);
    
    Person p4 = new Person("Alice", 43, 500000);
    assertEquals(p1, p4);
    
    Person p5 = new Person("Alice", 45, 250000);
    assertFalse(p1.equals(p5));
    
    Person p6 = new Person();
    assertFalse(p1.equals(p6));
    
    assertFalse(p1.equals(null));
    assertFalse(p1.equals(new Integer(27)));
  }

  @Test
  public void useNaturalComparison() {
    List<Person> people = Person.createFamily();
    Collections.sort(people);
    
    assertEquals(new Person("Padme", 46, 1000000), people.get(0));
    assertEquals(new Person("Anakin", 41, 75000), people.get(1));
    assertEquals(new Person("Leia", 19, 10000), people.get(2));
    assertEquals(new Person("Luke", 19, 0), people.get(3));
  }

  @Test
  public void useSalaryComparator() {
    List<Person> people = Person.createFamily();
    Collections.sort(people, new Person.SalaryComparator());
    
    assertEquals(new Person("Luke", 19, 0), people.get(0));
    assertEquals(new Person("Leia", 19, 10000), people.get(1));
    assertEquals(new Person("Anakin", 41, 75000), people.get(2));
    assertEquals(new Person("Padme", 46, 1000000), people.get(3));
  }

  
  @Test
  public void catchPropertyChange() {
    Person alice = new Person("Alice", 43, 250000);

    // ============ YOUR CHANGES BEGIN HERE
    // Call addPropertyChangeListener with an *anonymous* PropertyChangedListener object
    // that runs the following code on a PropertyChangeEvent:
    // that has the following code in it:
    /*
    assertEquals("ssn", evt.getPropertyName());
    assertEquals("", evt.getOldValue());
    assertEquals("012-34-5678", evt.getNewValue());
    */

    // ============ YOUR CHANGES END HERE
    
    assertEquals(false, alice.getPropertyChangeFired());
    alice.setSSN("012-34-5678");
    assertEquals(true, alice.getPropertyChangeFired());
  }
}
