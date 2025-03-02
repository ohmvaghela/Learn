import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

interface Chatroom{
  public void sendMessage(User user, String message);
  public void addUser(User user);
}
class ConcreteChatRoom implements Chatroom{
  private List<User> users;
  public ConcreteChatRoom(){
    users = new ArrayList<>();
  }
  public void sendMessage(User user, String message){
    for(User user_itr : users){
      if(!user_itr.equals(user)){
        user_itr.addMessage(message);
      }
    }
  }
  public void addUser(User user){
    users.add(user);
  }
}

abstract class User{
  private String name;
  public User(String name){this.name = name;}
  public abstract void sendMessage(Chatroom chatroom, String message);
  public abstract void addMessage(String message);
  public abstract void renderMessage(String message);
  @Override
  public boolean equals(Object obj){
    if(obj == this) return true;
    if(obj == null || obj.getClass() != getClass()) return false;
    User cur_user = (User)obj;
    return Objects.equals(cur_user.getName(), this.getName());
  }  

  public String getName() {return name;}
  public void setName(String name) {this.name = name;}
}
class ConcreteUser extends User{
  public ConcreteUser(String name){super(name); }
  public void sendMessage(Chatroom chatroom, String message){
    chatroom.sendMessage(this, message);
    System.out.println("cur: "+message);
  }
  public void addMessage(String message){
    renderMessage(message);
  }
  public void renderMessage(String message){
    System.out.println(message);
  }
}

public class Main {
  
}
