#include <bits/stdc++.h>
#include <ctime> 

using namespace std;


// time_t now;
// now = time(0);
// customer_id =  name + to_string(now);


// enum Car_type{
//   SMALL,
//   MID,
//   LARGE
// };

enum Box{
  EMPTY = ' ',
  X = 'X',
  O = 'O'
};

class Board{
  private:
    vector<vector<Box>> boxes;
    bool win;
  public:
    Board(){
      boxes = vector<vector<Box>>(3,vector<Box>(3,EMPTY));
      win = false;
    }
    bool FillBox(int x, int y, Box b){
      if(x < 0 || y < 0 || x > 2 || y > 2){
        cout<<"invalid box";
        return false;
      }
      else if(boxes[y][x] != EMPTY){
        cout<<"box already filled\n";
        return false;
      }
      else{
        boxes[y][x] = b;
        checkWin(y, x, b);
        return true;
      }
    }
    void checkWin(int y, int x, Box b){
      int count = 0;
      // horizontal
      for(int i = 0;i<3;i++){
        count += (boxes[y][i] == b);
      }
      if(count == 3) {win = 1;return;}
      // vertical
      count = 0;
      for(int i = 0;i<3;i++){
        count += (boxes[i][x] == b);
      }
      if(count == 3) {win = 1;return;}

      count = 1;
      if(x-1 >= 0 && y-1 >= 0){
        count += (boxes[y-1][x-1] == b);
        if(x-2 >= 0 && y-2 >= 0){
          count += (boxes[y-2][x-2] == b);
        }
      }
      if(x+1 < 3 && y+1 < 3){
        count += (boxes[y+1][x+1] == b);
        if(x+2 < 3 && y+2 < 3){
          count += (boxes[y+2][x+2] == b);
        }
      }
      if(count == 3) {win = 1;return;}

      count = 1;
      if(x-1 >= 0 && y+1 < 3){
        count += (boxes[y+1][x-1] == b);
        if(x-2 >= 0 && y+2 < 3){
          count += (boxes[y+2][x-2] == b);
        }
      }
      if(x-1 >= 0 && y+1 < 3){
        count += (boxes[y+1][x-1] == b);
        if(x-2 >= 0 && y+2 < 3){
          count += (boxes[y+2][x-2] == b);
        }
      }
      if(count == 3) {win = 1;return;}
    }
    bool getWin(){
      return win;
    }
    void display(){
      for(int i = 0;i<3;i++){
        for(int j = 0;j<3;j++){
          if(j != 2) cout<<" "<<(char)boxes[i][j]<<" |";
          else cout<<" "<<(char)boxes[i][j]<<" ";
        }
        if(i != 2) cout<<"\n---+---+---\n";
      }
      cout<<endl;
    }
};

class Player{
  private:
    string name;
    Box b;
  public:
    Player(string name, Box b): name(name), b(b){}
    Box getBox(){
      return b;
    }
};


class Controller{
  private:
    vector<Player*> players;
    Board *gameBoard;
    bool turn; 
    Controller(){}
  public:
    Controller(string name1, string name2){
      players.push_back(new Player(name1,X));
      players.push_back(new Player(name2,O));
      gameBoard = new Board();
      turn = 1;
    }
    void playGame(){
      while(!gameBoard->getWin()){
        
        if(turn){
          cout<<"player 1 turn : ";
          while(1){
            int x,y;
            cin>>y>>x;
            if(gameBoard->FillBox(x,y,players[0]->getBox()))break;
            else cout<<"wront input, try again : ";
          }
          if(gameBoard->getWin()){
            gameBoard->display();
            cout<<"Player 1 won";
            break;
          }
          turn = !turn;
          gameBoard->display();
        }
        else{
          cout<<"player 2 turn : ";
          while(1){
            int x,y;
            cin>>y>>x;
            if(gameBoard->FillBox(x,y,players[1]->getBox()))break;
            else cout<<"wront input, try again : ";
          }
          if(gameBoard->getWin()){
            gameBoard->display();
            cout<<"Player 2 won";
            break;
          }
          turn = !turn;
          gameBoard->display();
        }
      }
    }

};

int main(){
  Controller *game = new Controller("ohm","ohm1");
  game->playGame();
  return 0;
}
