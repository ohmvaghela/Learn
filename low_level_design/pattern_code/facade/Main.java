
class Fan{
  public void On(){
    System.out.println("Turn On Fan");
  }
  public void Off(){
    System.out.println("Turn Off Fan");
  }
}
class AC{
  public void On(){
    System.out.println("Turn On AC");
  }
  public void Off(){
    System.out.println("Turn Off AC");
  }
}
class TV{
  public void On(){
    System.out.println("Turn On TV");
  }
  public void Off(){
    System.out.println("Turn Off TV");
  }
}

class Controller{
  private Fan fan;
  private TV tv;
  private AC ac;
  public Controller(Fan fan, TV tv, AC ac){this.ac=ac;this.fan=fan;this.tv=tv;}
  public void turnOnAll(){this.fan.On();this.tv.On();this.ac.On();}
  public void turnOffAll(){this.fan.Off();this.tv.Off();this.ac.Off();}
  public void turnOnFan(){this.fan.On();}
  public void turnOffFan(){this.fan.Off();}
  public void turnOnTv(){this.tv.On();}
  public void turnOffTv(){this.tv.Off();}
  public void turnOnAc(){this.ac.On();}
  public void turnOffAc(){this.ac.Off();}
}

public class Main {

    private static A A(int i, int i0, String ohm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
}
