class Content {
  private String text;

  public void update(String text) {
      this.text = text;
  }

  public String get() {
      return this.text;
  }
}

// Context Class (Manages State)
class DocHandle {
  private Content content;
  private State state;

  public DocHandle(Content content) {
      this.content = content;
      this.state = new DraftState(this, content);
  }

  public void setState(State state) {
      this.state = state;
  }

  public void editContent(String text) {
      state.edit(text);
  }

  public void sendForModeration() {
      state.sendForModeration();
  }

  public void sendForDraft() {
      state.sendForDraft();
  }

  public void sendForPublish() {
      state.sendForPub();
  }

  public String getContent() {
      return state.getText();
  }
}

// Abstract State Class
abstract class State {
  protected Content content;
  protected DocHandle docHandle;

  public State(DocHandle doc, Content content) {
      this.docHandle = doc;
      this.content = content;
  }

  public abstract void edit(String text);

  public abstract void sendForModeration();

  public abstract void sendForDraft();

  public abstract void sendForPub();

  public String getText() {
      return content.get();
  }
}

// Draft State (Allows Editing, Can Move to Moderation)
class DraftState extends State {

  public DraftState(DocHandle doc, Content content) {
      super(doc, content);
  }

  @Override
  public void edit(String text) {
      content.update(text);
      System.out.println("Content updated in Draft: " + text);
  }

  @Override
  public void sendForModeration() {
      System.out.println("Document sent for moderation.");
      docHandle.setState(new ModerationState(docHandle, content));
  }

  @Override
  public void sendForDraft() {
      System.out.println("Already in draft state.");
  }

  @Override
  public void sendForPub() {
      System.out.println("Cannot send from Draft to Publish state directly.");
  }
}

// Moderation State (No Editing, Can Be Approved/Rejected)
class ModerationState extends State {

  public ModerationState(DocHandle doc, Content content) {
      super(doc, content);
  }

  @Override
  public void edit(String text) {
      System.out.println("Cannot edit content in Moderation state.");
  }

  @Override
  public void sendForModeration() {
      System.out.println("Already in Moderation state.");
  }

  @Override
  public void sendForDraft() {
      System.out.println("Document returned to Draft state.");
      docHandle.setState(new DraftState(docHandle, content));
  }

  @Override
  public void sendForPub() {
      System.out.println("Document moved to Published state.");
      docHandle.setState(new PublishState(docHandle, content));
  }
}

// Published State (No Editing, Cannot Go Back)
class PublishState extends State {

  public PublishState(DocHandle doc, Content content) {
      super(doc, content);
  }

  @Override
  public void edit(String text) {
      System.out.println("Cannot edit content in Published state.");
  }

  @Override
  public void sendForModeration() {
      System.out.println("Cannot send content to Moderation after publishing.");
  }

  @Override
  public void sendForDraft() {
      System.out.println("Cannot send content back to Draft after publishing.");
  }

  @Override
  public void sendForPub() {
      System.out.println("Already in Published state.");
  }
}

public class Main {
  public static void main(String[] args) {
      Content content = new Content();
      DocHandle doc = new DocHandle(content);

      // Draft State - can edit content
      doc.editContent("Initial Draft Content");

      // Move to Moderation State
      doc.sendForModeration();

      // Moderation State - cannot edit content
      doc.editContent("Trying to Edit in Moderation");

      // Move to Published State
      doc.sendForPublish();

      // Published State - cannot edit content
      doc.editContent("Trying to Edit in Published");
      System.out.println("Current Document Content: " + doc.getContent());
  }
}
