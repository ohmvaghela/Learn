import com.sun.source.tree.SynchronizedTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class Logger{
  private List<String> messages;
  private Logger(){
    messages = Collections.synchronizedList(new ArrayList<>());
  }
  private static class LogHelper{
    private static final Logger INSTANCE = new Logger();
  }
  public static Logger getInstace(){
    return LogHelper.INSTANCE;
  }

  public synchronized void logMessage(String message){
    System.out.println(message);
    messages.add(message);
  }
  public List<String> getLogs(){
    return messages;
  }
}


public class Main {
  
}
