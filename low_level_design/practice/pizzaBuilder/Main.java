
abstract class Pizza{
  private int price;
  private String name;
  public Pizza(String name, int price){this.price = price;this.name = name;}
  public int getBill(){return this.price;}
  public void setPrice(int price){this.price = price;}
  public String showOrder(){return this.name;};
  public int getPrice(){return this.price;}
  public String getName(){return this.name;}
}
class ThinCrust extends Pizza{
  public ThinCrust(){super("Thin Crust", 40);}
}
class ThickCrust extends Pizza{
  public ThickCrust(){super("Thick Crust", 50);}
}


abstract class Topping extends Pizza{
  private Pizza pizza;
  public Topping(int price, String name,Pizza pizza){super(name, price);this.pizza = pizza;}
  public int getBill(){return getPrice() + pizza.getBill(); }
  public void setPizza(Pizza pizza){this.pizza = pizza;}
  public String showOrder(){ return pizza.showOrder() + "-" + getName();}
}
class Tomato extends Topping{
  public Tomato(Pizza pizza){super(40, "Tomato", pizza);}
}
class Onion extends Topping{
  public Onion(Pizza pizza){super(50, "Onion", pizza);}
}


public class Main {
  public static void main(String[] args) {
      
    Pizza myPizza = new ThinCrust();
    myPizza = new Tomato(myPizza);
    myPizza = new Onion(myPizza);
    System.out.println(myPizza.getBill());
    System.out.println(myPizza.showOrder());
    
  }

}
