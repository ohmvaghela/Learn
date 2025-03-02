abstract class FileProcessor {
  protected String file;  // Store file name at the base level

  // Template method (Final to enforce execution steps)
  public final void executeProcessing(String file) {
      this.file = file;  // Store the file name
      readFile();
      processFile();
      saveFileToHDFS();
  }

  // Abstract methods to be overridden by subclasses
  protected abstract void readFile();
  protected abstract void processFile();
  protected abstract void saveFileToHDFS();
}

// JSON File Processing
class JSONProcessor extends FileProcessor {
  @Override
  protected void readFile() {
      System.out.println("Reading JSON file: " + file);
  }

  @Override
  protected void processFile() {
      System.out.println("Processing JSON file: " + file);
  }

  @Override
  protected void saveFileToHDFS() {
      System.out.println("Saving JSON file to HDFS: " + file);
  }
}

// CSV File Processing
class CSVProcessor extends FileProcessor {
  @Override
  protected void readFile() {
      System.out.println("Reading CSV file: " + file);
  }

  @Override
  protected void processFile() {
      System.out.println("Processing CSV file: " + file);
  }

  @Override
  protected void saveFileToHDFS() {
      System.out.println("Saving CSV file to HDFS: " + file);
  }
}

// FileController to trigger processing
class FileController {
  public static void processFile(FileProcessor fileProcessor, String file) {
      fileProcessor.executeProcessing(file);
  }
}

// Main Class to Run Code
public class Main {
  public static void main(String[] args) {
      FileController.processFile(new CSVProcessor(), "data.csv");
      FileController.processFile(new JSONProcessor(), "data.json");
  }
}
