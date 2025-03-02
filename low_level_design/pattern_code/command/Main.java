// Step 1: Command Interface
interface Command {
  void execute() throws Exception;
}

// Step 2: Concrete Command Classes
class TurnOnCommand implements Command {
  private Device device;

  public TurnOnCommand(Device device) { this.device = device; }

  @Override
  public void execute() throws Exception { device.on(); }
}

class TurnOffCommand implements Command {
  private Device device;

  public TurnOffCommand(Device device) { this.device = device; }

  @Override
  public void execute() throws Exception { device.off(); }
}

class LockCommand implements Command {
  private Device device;

  public LockCommand(Device device) { this.device = device; }

  @Override
  public void execute() throws Exception { device.lock(); }
}

class UnlockCommand implements Command {
  private Device device;

  public UnlockCommand(Device device) { this.device = device; }

  @Override
  public void execute() throws Exception { device.unlock(); }
}

// Step 3: RemoteController (Invoker) 
class RemoteController {
  private Command command;

  public void setCommand(Command command) {
      this.command = command;
  }

  public void pressButton() throws Exception {
      if (command != null) {
          command.execute();
      } else {
          throw new Exception("No command set.");
      }
  }
}

// Device Interface (Same as Before)
interface Device {
  void on() throws Exception;
  void off() throws Exception;
  void lock() throws Exception;
  void unlock() throws Exception;
  boolean getState();
  String getName();
}

// Fan Class (Same as Before)
class Fan implements Device {
  private boolean state;

  public Fan() { this.state = false; }

  public String getName() { return "Fan"; }

  public void on() {
      if (state) {
          System.out.println("Fan is already on.");
      } else {
          state = true;
          System.out.println("Fan turned on.");
      }
  }

  public void off() {
      if (!state) {
          System.out.println("Fan is already off.");
      } else {
          state = false;
          System.out.println("Fan turned off.");
      }
  }

  public void lock() throws Exception {
      throw new UnsupportedOperationException("Locking is not applicable for Fan.");
  }

  public void unlock() throws Exception {
      throw new UnsupportedOperationException("Unlocking is not applicable for Fan.");
  }

  public boolean getState() {
      return state;
  }
}

// Lock Class (Same as Before)
class Lock implements Device {
  private boolean state;

  public Lock() { this.state = false; }

  public String getName() { return "Lock"; }

  public void on() throws Exception {
      throw new UnsupportedOperationException("Turning on is not applicable for Lock.");
  }

  public void off() throws Exception {
      throw new UnsupportedOperationException("Turning off is not applicable for Lock.");
  }

  public void lock() {
      if (state) {
          System.out.println("Already locked.");
      } else {
          state = true;
          System.out.println("Locked.");
      }
  }

  public void unlock() {
      if (!state) {
          System.out.println("Already unlocked.");
      } else {
          state = false;
          System.out.println("Unlocked.");
      }
  }

  public boolean getState() {
      return state;
  }
}

// Main Method to Demonstrate Command Pattern
public class Main {
  public static void main(String[] args) {
      try {
          RemoteController remote = new RemoteController();

          // Controlling a Fan
          Fan fan = new Fan();
          System.out.println("Device: " + fan.getName());

          remote.setCommand(new TurnOnCommand(fan));
          remote.pressButton();

          remote.setCommand(new TurnOffCommand(fan));
          remote.pressButton();

          // Controlling a Lock
          Lock lock = new Lock();
          System.out.println("\nDevice: " + lock.getName());

          remote.setCommand(new LockCommand(lock));
          remote.pressButton();

          remote.setCommand(new UnlockCommand(lock));
          remote.pressButton();

      } catch (Exception e) {
          System.out.println("Error: " + e.getMessage());
      }
  }
}
