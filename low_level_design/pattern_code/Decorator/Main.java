
class Bill{
  private int value = 0;

  public void add(int val){
    this.value = this.value + val;
  }
  
  public int get(){
    return this.value;
  }
}

abstract class Coffee{
  public Bill bill;
  public int getBill(){
    return bill.get();
  }
}
class Coffee1 extends Coffee{
  public Coffee1(){
    bill = new Bill();
    bill.add(10);
  }
}
class Coffee2 extends Coffee{
  public Coffee2(){
    bill = new Bill();
    bill.add(20);
  }
}

abstract class Topping extends Coffee{
  public Coffee coffee;
  Topping(Coffee coffee){this.coffee = coffee;}
  public int getPrice(){return 0;};
}

class ToppingBase extends Topping{
  public ToppingBase(Coffee coffee){
    super(coffee);
  }
  public int getBill(){
    return super.coffee.getBill() + getPrice();
  }
}

class Topping1 extends ToppingBase{
  public Topping1(Coffee coffee){
    super(coffee);
  }
  public int getPrice(){
    return 12;
  }
}
class Topping2 extends ToppingBase{
  public Topping2(Coffee coffee){
    super(coffee);
  }
  public int getPrice(){
    return 11;
  }
}

public class Main {
  
  public static void main(String[] args) {

    Coffee coffee1 = new Coffee1();
    coffee1 = new Topping1(coffee1);  
    coffee1 = new Topping2(coffee1);
    System.out.println(coffee1.getBill());  
  }
}
