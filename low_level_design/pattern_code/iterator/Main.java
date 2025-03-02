import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


class Song {
  private String name;
  public Song(String name) {this.name = name;}
  public String getName() {return name;}
  @Override
  public String toString() {return name;}
  @Override
  public boolean equals(Object song){
    if(this==song) return true;
    if(song==null || song.getClass() != getClass()) return false;
    Song in_song = (Song)song;
    return Objects.equals(name, in_song.name);
  }
}

interface SongIterator{
  public boolean hasNext();
  public Song next() throws Exception;
}

class PlaylistIterator implements SongIterator{
  private List<Song> songs;
  private int index;
  public PlaylistIterator(List<Song> songs){
    this.songs = songs;
    index = 0;
  }
  public boolean hasNext(){return index<songs.size();};
  public Song next()throws Exception{
    if(index<songs.size()){
      return songs.get(index++);
    }else{
      throw new NullPointerException("reached end of list");
    }
  }
}

interface SongCollection{
  public void addSong(Song song);
  public void deleteSong(Song song);
  public SongIterator getSongIterator();
}

class Playlist implements SongCollection{
  private List<Song> songs;
  public Playlist(){songs = new ArrayList<>();}
  public void addSong(Song song){
    songs.add(song);
  };
  public void deleteSong(Song song){

    Iterator<Song> iterator = songs.iterator();
    while(iterator.hasNext()){
      Song song_itr = iterator.next();
      if(song_itr.equals(song)){
        iterator.remove();
      }
    }
  };
  public SongIterator getSongIterator(){
    return new PlaylistIterator(songs);
  }
}

public class Main {
  public static void main(String[] args) throws Exception {
      Playlist playlist = new Playlist();
      playlist.addSong(new Song("Song A"));
      playlist.addSong(new Song("Song B"));
      playlist.addSong(new Song("Song C"));

      System.out.println("Iterating over Playlist:");
      SongIterator iterator = playlist.getSongIterator();
      while (iterator.hasNext()) {
          System.out.println(iterator.next().getName());
      }

      System.out.println("\nRemoving 'Song B':");
      playlist.deleteSong(new Song("Song B"));

      System.out.println("\nIterating again:");
      iterator = playlist.getSongIterator();
      while (iterator.hasNext()) {
          System.out.println(iterator.next().getName());
      }
  }
}


