import java.util.Stack;

class CareTaker{
  Stack<Content> editor_undo;
  Stack<Content> editor_redo;
  public CareTaker(){
    editor_undo = new Stack<>();
    editor_redo = new Stack<>();
  }
  public void saveState(Editor editor){
    editor_undo.push(editor.getState());
    editor_redo.clear();
  }
  public void undoState(Editor editor){
    Content tempContent = editor_undo.peek();
    editor_redo.push(tempContent);
    editor_undo.pop();
    editor.setState(tempContent);
  }
  public void redoState(Editor editor){
    Content tempContent = editor_redo.peek();
    editor_undo.push(tempContent);
    editor_redo.pop();
    editor.setState(tempContent);
  }
}

class Content{  
  String value;
  public Content getState(){return this;}
  public String getValue(){return this.value;}
  public void setValue(String value){this.value = value;}
  public void appendValue(String value){this.value += value;}

}

class Editor{
  private Content content;
  public Editor(){this.content = new Content();}
  public String getValue(){return content.getValue();}
  public void setValue(String value){content.setValue(value);}
  public void appendValue(String value){content.appendValue(value);}
  public Content getState(){return content.getState();}
  public void setState(Content content){this.content = content;}
}


public class Main {

}
