
enum LogLevel{INFO, DEBUG, ERROR}

class LogHandler{

  private Logger logger;
  public LogHandler(){
    this.logger = new BaseLogger();
  }
  public void createLogger(LogLevel level){
    switch (level) {
        case INFO:
            this.logger = new InfoLogger(this.logger);
            break;
        case DEBUG:
            this.logger = new DebugLogger(this.logger);
            break;
        case ERROR:
            this.logger = new ErrorLogger(this.logger);
            break;
        default:
            throw new AssertionError();
    }
  }
  public void logMessage(LogLevel level, String message){
    this.logger.logMessage(level, message);
  }
}
abstract class Logger{
  private Logger nextLogger = null;
  Logger(Logger nextLogger){
    this.nextLogger = nextLogger; 
  }
  public void logMessage(LogLevel level, String message){}
  public void handle(LogLevel level, String message){
    if(nextLogger != null){
      nextLogger.logMessage(level, message);
    }else{
      System.out.println("No suitable logger found");
    }
  }
}
class BaseLogger extends Logger{
  public BaseLogger(){super(null);}
  public void logMessage(LogLevel level, String message){
    System.out.println("No suitable logger found");
  }
}

class InfoLogger extends Logger{
  public InfoLogger(Logger next){super(next);}

  public void logMessage(LogLevel level, String message){
    if(level == LogLevel.INFO){
      System.out.println("INFO : "+message);
    }else{
      handle(level, message);   
    }
  }
}
class DebugLogger extends Logger{
  public DebugLogger(Logger next){super(next);}

  public void logMessage(LogLevel level, String message){
    if(level == LogLevel.DEBUG){
      System.out.println("DEBUG : "+message);
    }else{
      handle(level, message);   
    }
  }  
}
class ErrorLogger extends Logger{
  public ErrorLogger(Logger next){super(next);}

  public void logMessage(LogLevel level, String message){
    if(level == LogLevel.ERROR){
      System.out.println("ERROR : "+message);
    }else{
      handle(level, message);   
    }
  }  
}

public class Main {
  public static void main(String[] args) {
    LogHandler logHandler = new LogHandler();
    logHandler.createLogger(LogLevel.INFO);
    logHandler.createLogger(LogLevel.DEBUG);
    logHandler.createLogger(LogLevel.ERROR);
    
    logHandler.logMessage(LogLevel.INFO, "This is an info message.");
    logHandler.logMessage(LogLevel.DEBUG, "This is a debug message.");
    logHandler.logMessage(LogLevel.ERROR, "This is an error message.");
  }
}
