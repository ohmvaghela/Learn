class Computer{
  private String CPU;
  private String RAM;
  private String Storage;
  private String Graphics_Card;
  private String Operating_System;

  private Computer(ComputerBuilder cpb){
    this.CPU = cpb.CPU;
    this.RAM = cpb.RAM;
    this.Storage = cpb.Storage;
    this.Graphics_Card = cpb.Graphics_Card;
    this.Operating_System = cpb.Operating_System;    
  }

  public static class ComputerBuilder{
    private String CPU;
    private String RAM;
    private String Storage;
    private String Graphics_Card;
    private String Operating_System;
      
    public ComputerBuilder setCPU(String CPU){
      this.CPU = CPU;
      return this;
    }
    public ComputerBuilder setRAM(String RAM){
      this.RAM = RAM;
      return this;
    }
    public ComputerBuilder setStorage(String Storage){
      this.Storage = Storage;
      return this;
    }
    public ComputerBuilder setGraphics_Card(String Graphics_Card){
      this.Graphics_Card = Graphics_Card;
      return this;
    }
    public ComputerBuilder setOperating_System(String Operating_System){
      this.Operating_System = Operating_System;
      return this;
    }
    public Computer build(){
      return new Computer(this);
    }
  }
}

public class Main {
  
}
