interface UIFactory{
  public Component createButton();
  public Component createCheckbox();
}

class WindowsUIFactory implements UIFactory{
  private String sys = "Windows"; 
  public Component createButton(){return new Button(sys);}
  public Component createCheckbox(){return new CheckBox(sys);}
}
class MACUIFactory implements UIFactory{
  private String sys = "MAC"; 
  public Component createButton(){return new Button(sys);}
  public Component createCheckbox(){return new CheckBox(sys);}
}
class LinuxUIFactory implements UIFactory{
  private String sys = "Linux"; 
  public Component createButton(){return new Button(sys);}
  public Component createCheckbox(){return new CheckBox(sys);}
}

interface Component{
  public void render();
}
class Button implements Component{
  private String sys;
  public Button(String sys){
    this.sys = sys;
  }
  public void render(){
    System.out.println(sys+" : Button");
  }
}
class CheckBox implements Component{
  private String sys;
  public CheckBox(String sys){
    this.sys = sys;
  }
  public void render(){
    System.out.println(sys+" : Checkbox");
  }
}

public class Main {
  public static void main(String[] args) {

    UIFactory factory = new WindowsUIFactory();
    Component btn = factory.createButton();
    Component chk = factory.createCheckbox();
    
    btn.render();
    chk.render();
  }
  
}
