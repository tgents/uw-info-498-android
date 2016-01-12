package edu.info498d.warmup;

import java.beans.*;
import java.util.*;

public class Person implements Comparable<Person>{
  private int age;
  private String name;
  private double salary;
  private String ssn;
  private boolean propertyChangeFired;
  
  public Person() {
    this("", 0, 0.0);
  }
  
  public Person(String n, int a, double s) {
    name = n;
    age = a;
    salary = s;
    ssn = null;
    propertyChangeFired = false;
  }

  public String getName() {
    return name;
  }
    
  public String getSSN() {
    return ssn;
  }
  public int getAge(){
    return age;
  }
  public double getSalary() {
    return salary;
  }
  public double setSalary(double newSalary) {
    salary = newSalary;
    return salary;
  }
  public int setAge(int newAge) {
    if(newAge < 0){
      throw new IllegalArgumentException();
    }
    age = newAge;
    return age;
  }
  public String setName(String newName){
    if(newName == null){
      throw new IllegalArgumentException();
    }
    name = newName;
    return name;
  }
  public void setSSN(String value) {
    String old = ssn == null? "" : ssn;
    ssn = value;
    
    this.pcs.firePropertyChange("ssn", old, value);
    propertyChangeFired = true;
  }
  public boolean getPropertyChangeFired() {
    return propertyChangeFired;
  }

  public double calculateBonus() {
    return salary * 1.10;
  }
  
  public String becomeJudge() {
    return "The Honorable " + name;
  }
  
  public int timeWarp() {
    age += 10;
    return age + 10;
  }

  public String toString(){
    return "Person[name:" + name + ";age:" + age + ";salary:" + salary + "]";
  }
  
  public boolean equals(Object obj) {
    if(obj == null){
      return false;
    }
    if(getClass() != obj.getClass()){
      return false;
    }
    Person other = (Person) obj;
    return this.name.equals(other.name) && this.age == other.age;
  }

  public int compareTo(Person other){
    if(this.age - other.age == 0){
      return this.name.compareTo(other.name);
    }
    return other.age - this.age;
  }

  static class SalaryComparator implements Comparator<Person>{
    public int compare(Person o1, Person o2){
      return (int) (o1.getSalary() - o2.getSalary());
    }
  }

  public static List<Person> createFamily(){
    List<Person> al = new ArrayList<Person>();
    al.add(new Person("Anakin", 41, 75000));
    al.add(new Person("Padme", 46, 1000000));
    al.add(new Person("Luke", 19, 0));
    al.add(new Person("Leia", 19, 10000));
    return al;
  }

  // PropertyChangeListener support; you shouldn't need to change any of
  // these two methods or the field
  //
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  public void addPropertyChangeListener(PropertyChangeListener listener) {
      this.pcs.addPropertyChangeListener(listener);
  }
  public void removePropertyChangeListener(PropertyChangeListener listener) {
      this.pcs.removePropertyChangeListener(listener);
  }
}
