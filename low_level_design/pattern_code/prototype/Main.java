import java.util.HashMap;
import java.util.Map;

abstract class Document implements Cloneable{
  private String title;
  private String author;
  private String content;
  public Document(String title, String author, String content){
    this.title = title;
    this.author = author;
    this.content = content;
  }
  public void setTitle(String title){this.title = title;}
  public void setAuthor(String author){this.author = author;}
  public void setContent(String content){this.content = content;}

  public String getTitle(String title){return this.title;}
  public String getAuthor(String author){return this.author;}
  public String getContent(String content){return this.content;}
  
  public Document clone()throws CloneNotSupportedException{
    return (Document) super.clone();
  }
}
class WordDocument extends Document{
    public WordDocument(String title, String author, String content){super(title, author, content);}
} 
class PDFDocument extends Document{
    public PDFDocument(String title, String author, String content){super(title, author, content);}
}
class SpreadsheetDocument extends Document{
    public SpreadsheetDocument(String title, String author, String content){super(title, author, content);}
}

class DocumentBuilder{
  private static Map<String, Document> mapper = new HashMap<>();;
  static{
    mapper.put("Word", new WordDocument(null, null, null));
    mapper.put("PDF", new PDFDocument(null, null, null));
    mapper.put("Spreadsheet", new SpreadsheetDocument(null, null, null));
  }
  public static Document getDocument(String docType) throws DocException, CloneNotSupportedException {
    if(mapper.containsKey(docType)){
      return mapper.get(docType).clone();
    }else{
      throw new DocException("No matching Doc Type found");
    }
  }
}

class DocException extends Exception{
  public DocException(String message){
    super(message);
  }
}

public class Main{
  public static void main(String[] args) throws Exception{
    PDFDocument pdf = (PDFDocument) DocumentBuilder.getDocument("PDF");  
      
  }
}
