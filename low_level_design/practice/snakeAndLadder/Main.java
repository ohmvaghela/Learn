import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Dice{
  static private final Random random;
  static{
    random = new Random();
  }
  public static int getRoll(){
    return random.nextInt(1,7);
  }
}

abstract class Mover{
  private Box start;
  private Box end;
  public Mover(Box start, Box end){this.start = start; this.end = end;}
  public boolean matches(Box box){ return this.start.equals(box);}
  public Box newBox(){return end;}
}
class Snake extends Mover{
  public Snake(Box start, Box end){super(start, end);}
}
class Ladder extends Mover{
  public Ladder(Box start, Box end){super(start, end);}
}

class Box{
  private int val;
  private Mover mover; 
  public Box(int val){this.val = val;}
  @Override
  public boolean equals(Object obj){
    if(this == obj) return true;
    if(obj == null || obj.getClass() != getClass()) return false;
    Box objBox = (Box)obj;
    return this.val == objBox.val;
  }

    public Mover getMover() {
        return mover;
    }

    public void setMover(Mover mover) {
        this.mover = mover;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}

class Player{
  private Box box;
  public Player(Box box){this.box = box;}

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }
  
}

class MasterController{
  private List<Player> players;
  private List<Snake> snakesList;
  private List<Ladder> laddersList;
  private List<Box> boxesList;
  private Player winner;
  private void createBox(){
    for(int i = 1;i<=100;i++){
      boxesList.add(new Box(i));
    }
  }
  private void createSnakes(List<ArrayList<Integer>> snakesLoc)throws Exception{
    for(ArrayList<Integer> snake_itr : snakesLoc){
      if(snake_itr.get(0) <= 0 || snake_itr.get(0) > 100) throw new Exception("Invalid start box");
      if(snake_itr.get(1) <= 0 || snake_itr.get(1) > 100) throw new Exception("Invalid end box");
      Snake snake = new Snake(boxesList.get(snake_itr.get(0)), boxesList.get(snake_itr.get(1)));
      snakesList.add(snake);
      boxesList.get(snake_itr.get(0)).setMover(snake);
    }
  }
  private void createLadders(List<ArrayList<Integer>> laddersLoc)throws Exception{
    for(ArrayList<Integer> ladder_itr : laddersLoc){
      if(ladder_itr.get(0) <= 0 || ladder_itr.get(0) > 100) throw new Exception("Invalid start box");
      if(ladder_itr.get(1) <= 0 || ladder_itr.get(1) > 100) throw new Exception("Invalid end box");
      Ladder ladder = new Ladder(boxesList.get(ladder_itr.get(0)), boxesList.get(ladder_itr.get(1)));
      laddersList.add(ladder);
      boxesList.get(ladder_itr.get(0)).setMover(ladder);
    }
  }
  private void createPlayers(int n){
    for(int i = 0;i<n;i++){
      this.players.add(new Player(this.boxesList.get(0)));
    }
  }
  public MasterController(int players, List<ArrayList<Integer>> snakesList, List<ArrayList<Integer>> laddersList)throws Exception{
    createBox();
    createPlayers(players);
    createSnakes(snakesList);
    createLadders(laddersList);
  }
  public Player getWinner(){
    return this.winner;
  }
  public void simulateGame(){
    int numberOfPlayers = players.size();
    int counter = 0;
    while(winner == null){
      counter++;
      for(int i =0;i<numberOfPlayers;i++){
        System.out.println("Player "+(i+1)+"turn");
        makeMove(players.get(i));
        if(players.get(i).getBox().getVal() == 100){
          this.winner = players.get(i);
          return;
        }
      }
      if(counter > 100){
        System.err.println("Game went for too long");
        return;
      }
    }
  }
  private void makeMove(Player player){
    int roll = Dice.getRoll();
    if(player.getBox().getVal()+roll > 100){
      System.out.println("Bad luck: no move");
      System.out.println("Current box"+player.getBox().getVal());
    }else{
      player.setBox(  boxesList.get( player.getBox().getVal() + roll ) );
      System.out.println("New box : "+player.getBox().getVal());
      Mover mover = player.getBox().getMover();
      if(mover != null){
        if(mover instanceof Ladder){
          System.out.println("Good luck: got on ladder");
          player.setBox(mover.newBox());
          System.out.println("New box : "+player.getBox().getVal());
        }else{
          System.out.println("Bad luck: got on snake");
          player.setBox(mover.newBox());
          System.out.println("New box : "+player.getBox().getVal());
        }
      }
    }
  }
}

class InputReader{
  static private final Scanner sc;
  static {
    sc = new Scanner(System.in);
  }
  public static int fillInt() throws Exception{
    Integer val = null;
    while(val == null){
      try{
        val = sc.nextInt();
        sc.nextLine();
      }catch(Exception e){
        System.out.println(e);
        sc.nextLine();
        System.out.println("Try again");
      }
    }
    return val.intValue();
  }
  public static String fillString() throws Exception{
    String val = null;
    while(val == null){
      try{
        val = sc.nextLine();
      }catch(Exception e){
        System.out.println(e);
        sc.nextLine();
        System.out.println("Try again");
      }
    }
    return val;
  }
  public static List<ArrayList<Integer>> fill2DList(int n, int m) throws Exception{
    List<ArrayList<Integer>> val = new ArrayList<ArrayList<Integer>>(); 
    for(int i = 0;i<n;i++){
      val.add(fillList(m));
    }
    return val;
  }
  public static ArrayList<Integer> fillList(int n) throws Exception{
    ArrayList<Integer> val = new ArrayList<>();
    while(val.size() == 0){
      try{
        for(int i = 0;i<n;i++){ 
          val.add(sc.nextInt()); 
        }
      }catch(Exception e){
        System.err.println(e);
        val = new ArrayList<>();
        sc.nextLine();
        System.out.println("Try again");
      }
    }
    return val;
  }  
}

class Main{

  public static void main(String[] args) throws Exception {
    
    
    System.out.println("Number of Snakes :  ");
    int snakes = InputReader.fillInt();
    System.out.println("Fill Snakes :  ");
    List<ArrayList<Integer>> snake_list = InputReader.fill2DList(snakes, 2);
    
    System.out.println("Number of Ladders :  ");
    int ladders = InputReader.fillInt();
    System.out.println("Fill Snakes :  ");
    List<ArrayList<Integer>> laddersList = InputReader.fill2DList(ladders, 2);
    
    System.out.println("Number of Players :  ");
    int players = InputReader.fillInt();

    MasterController mc = new MasterController(players, snake_list, laddersList);

    mc.simulateGame();

    System.out.println("Winner is"+mc.getWinner().toString());

  }
}