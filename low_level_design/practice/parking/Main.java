import java.util.ArrayList;
import java.util.List;

class SlotOccupiedException extends Exception{
  public SlotOccupiedException(String message){
    super(message);
  }
}

class Slot{
  private int number;
  private Car car;
  public Slot(int number){
    this.number = number;
  }
  public void setCar(Car car){
    this.car = car;
  }
  public Car getCar(){return this.car;}
  public boolean isEmpty(){
    return (this.car==null);
  }
  public void removeCar(){
    this.car = null;
  }
  @Override
  public String toString(){
    return "Slot : "+number+" | car : "+car;
  }
} 
class Car{
  private String numberPlate;
  private String color;
  public Car(String numberPlate, String color){
    this.color = color;
    this.numberPlate = numberPlate;
  }
  @Override
  public String toString(){return numberPlate+","+color;}

    public String getColor() {
        return color;
    }

  @Override
  public boolean equals(Object obj){
    if(obj == this) return true;
    if(obj == null || obj.getClass() != getClass()) return false;
    Car objCar = (Car)obj;
    return objCar.numberPlate.equals(numberPlate) && objCar.color.equals(color);
  }
}


class ParkingManagementSystem{
  private List<Slot> slots;
  int numberOfSlots;
  int numberOfEmptySlots;
  public ParkingManagementSystem(int numberOfSlots){
    slots = new ArrayList<>();
    this.numberOfSlots = numberOfSlots;
    this.numberOfEmptySlots = numberOfSlots;
    for(int i = 1;i<=numberOfSlots;i++){
      slots.add(new Slot(i));
    }
  }
  public boolean parkCar(Car car)throws Exception{ 
    if(getAvailableSpaces() == 0){
      return false;
    }else{
      for(Slot slot:slots){
        if(slot.isEmpty()){
          slot.setCar(car);
          numberOfEmptySlots--;
          return true;
        }
      }
    }
    throw new Exception("Cannot find slot even if slots are available");
  }
  public void listSpaces(){
    for(Slot slot:slots){
      if(!slot.isEmpty()){
        System.out.println(slot);
      }
    }
  }
  public int getAvailableSpaces(){return numberOfEmptySlots;}
  public void carsWithColor(String color){
    for(Slot slot : slots){
      if(!slot.isEmpty() && slot.getCar().getColor().equals(color)){
        System.out.println(slot.getCar());
      }
    }
  }
  public void slotsWithColor(String color){
    for(Slot slot : slots){
      if(!slot.isEmpty() && slot.getCar().getColor().equals(color)){
        System.out.println(slot);
      }
    }    
  }
  public Slot slotForCar(Car car){
    for(Slot slot : slots){
      if(!slot.isEmpty() && slot.getCar().equals(car)){
        return slot;
      }
    }
    System.out.println("No car found");
    return null;
  }
  public void leave(Car car){
    for(Slot slot : slots){
      if(!slot.isEmpty() && slot.getCar().equals(car)){
        slot.removeCar();
        numberOfEmptySlots++;
        return;
      }
    }
    System.out.println("No car found");
  }
}

public class Main {
  public static void main(String[] args) {
    try {
      // Initialize the parking management system with 5 slots
      ParkingManagementSystem parkingSystem = new ParkingManagementSystem(5);

      // Create some cars
      Car car1 = new Car("KA01AB1234", "Red");
      Car car2 = new Car("KA02CD5678", "Blue");
      Car car3 = new Car("KA03EF9101", "Red");
      Car car4 = new Car("KA04GH1122", "White");
      Car car5 = new Car("KA05IJ1313", "Black");

      // Park the cars
      System.out.println("Parking car 1: " + car1);
      parkingSystem.parkCar(car1);

      System.out.println("Parking car 2: " + car2);
      parkingSystem.parkCar(car2);

      System.out.println("Parking car 3: " + car3);
      parkingSystem.parkCar(car3);

      System.out.println("Parking car 4: " + car4);
      parkingSystem.parkCar(car4);

      System.out.println("Parking car 5: " + car5);
      parkingSystem.parkCar(car5);

      // Try parking one more car, which should fail as the parking is full
      Car car6 = new Car("KA06KL1414", "Green");
      boolean parked = parkingSystem.parkCar(car6);
      if (!parked) {
        System.out.println("Parking failed! No available slots for car 6.");
      }

      // List all occupied slots
      System.out.println("\nList of occupied parking slots:");
      parkingSystem.listSpaces();

      // List available spaces
      System.out.println("\nAvailable spaces: " + parkingSystem.getAvailableSpaces());

      // Find cars with a particular color (Red)
      System.out.println("\nCars with color Red:");
      parkingSystem.carsWithColor("Red");

      // Find slots with a particular color (Red)
      System.out.println("\nSlots with color Red:");
      parkingSystem.slotsWithColor("Red");

      // Find the slot where a particular car is parked
      System.out.println("\nFinding slot for car with number plate KA02CD5678:");
      Slot slot = parkingSystem.slotForCar(car2);
      System.out.println("Car parked in slot: " + slot);

      // Car leaves the parking
      System.out.println("\nCar leaves: " + car2);
      parkingSystem.leave(car2);

      // List available spaces after car leaves
      System.out.println("\nAvailable spaces after car leaves: " + parkingSystem.getAvailableSpaces());

      // List all occupied slots after a car leaves
      System.out.println("\nList of occupied parking slots after car leaves:");
      parkingSystem.listSpaces();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
