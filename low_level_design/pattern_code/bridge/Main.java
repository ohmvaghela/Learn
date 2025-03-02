interface MediaPlayer{
  public void play();
}
class MP4 implements MediaPlayer{
  public void play(){
    System.err.println("Play on MP4");
  }
}
class MP3 implements MediaPlayer{
  public void play(){
    System.err.println("Play on MP4");
  }
}
class AVI implements MediaPlayer{
  public void play(){
    System.err.println("Play on MP4");
  }
}

interface OS{}
class Windows implements OS{
  private MediaPlayer mediaPlayer;

  public Windows(MediaPlayer mediaPlayer){
    this.mediaPlayer = mediaPlayer;
  }
  public void UsePlayer(){
    System.out.println("Windows");
    mediaPlayer.play();
  }
}
class Linux implements OS{
  private MediaPlayer mediaPlayer;

  public Linux(MediaPlayer mediaPlayer){
    this.mediaPlayer = mediaPlayer;
  }
  public void UsePlayer(){
    System.out.println("Linux");
    mediaPlayer.play();
  }
}

public class Main {
  
}
