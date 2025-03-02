import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap;



interface Item{
  public String name();
}
class Mobile implements Item{
  public String name(){ return "mobile";}
}
class TV implements Item{
  public String name(){ return "TV";}
}

class Store{

  private static ArrayList<Item> items;
  private static ArrayList<Integer> stock;
  private static Map<Item, AbstractMap.SimpleEntry<Consumer, Communication>> com = new HashMap<>();

  static {
    items = new ArrayList<Item>();
    stock = new ArrayList<Integer>();
  }
  private Store(){}
  public static void add(Item item){
    items.add(item);
    stock.add(0);
  }
  public static Item getItem(String item){
    for (Item item_itr : items) {
        if(item_itr.name() == item){
          return item_itr;
        }
    }
    return null;
  }
  public static void createSubscriber(Item item, String mode, Consumer consumer){
      Communication communication_mode;
      switch (mode) {
        case "SMS":
          communication_mode = new SMS();
          break;
        case "Call":
          communication_mode = new Call();
          break;
        default:
          communication_mode = null;
          break;
      }
      com.put(item, new AbstractMap.SimpleEntry<>(consumer, communication_mode));
  }
}

interface Communication{}
class SMS implements Communication{
  private Item item;
  
}
class Call implements Communication{
  private Item item;

}

class Consumer{
  public Optional<Item> request(String item, String mode){
    Item itemObj = Store.getItem(item);
    if(true){
      return Optional.of(itemObj);
    }else{
      Store.createSubscriber(itemObj, mode, this);
      return Optional.empty();
    }

  }
}

class Main{
  public static void main(String[] args) {
    
    Mobile mobile = new Mobile();
    TV tv = new TV();
    
    Store.add(mobile);
    Store.add(tv);

    Consumer c1 = new Consumer();
    Consumer c2 = new Consumer();
    
    Optional<Item> item = c1.request("mobile","SMS");
  }

}