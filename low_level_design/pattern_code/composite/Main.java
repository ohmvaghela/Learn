
import java.util.ArrayList;

interface UIComponent{
  public boolean isContainer();
  public String type();
  public void resize(int size);
  public void render();
  public void add(UIComponent label);
  }
  
  abstract class element implements UIComponent {
    public int compoenent_size;
    public boolean isContainer(){return false;}
    public void resize(int size){
      this.compoenent_size = size;
    }
    public void add(UIComponent label){
      System.out.println("Not supported for elements");
    };
  }
  class Button extends element{
    public Button(int size){super.compoenent_size = size;}
    public String type(){return "Button";}
    public void render(){System.err.print("Button-");}
  }
  class Label extends element{
    public Label(int size){super.compoenent_size = size;}
    public String type(){return "Label";}
    public void render(){System.err.print("Label-");}
  }
  
  abstract class container implements UIComponent{
    public boolean isContainer(){return true;}
    public ArrayList<UIComponent> list = new ArrayList<>();
    public void add(UIComponent componenet){list.add(componenet);}
    public void resize(int size){
      this.compoenent_size = size;
      for(UIComponent componenet : list){
        componenet.resize(size);
      }
    }
    public void render(){
      for(UIComponent componenet : list){
        componenet.render();
      }
    }
    int compoenent_size;
  }
  class Panel extends container{
    public Panel(int size){super.compoenent_size = size;}
    public String type(){return "Panel";}
    public void render(){
      System.err.print("Panel-");  
      super.render();
    }
  }
  class Window extends container{
    public Window(int size){super.compoenent_size = size;}
    public String type(){return "Window";}
    public void render(){
      System.err.print("Window-");  
      super.render();
    }
  }
  
  public class Main {
    public static void main(String[] args) {
      UIComponent button = new Button(10);
      UIComponent label = new Label(5);
      
      UIComponent panel = new Panel(15);
      panel.add(button);
      panel.add(label);
    
    UIComponent window = new Window(20);
    window.add(panel);
    
    window.render(); // Window-20 Panel-15 Button-10 Label-5 
    System.out.println();
    window.resize(30);
    window.render(); // Window-30 Panel-30 Button-30 Label-30 
  }
}
