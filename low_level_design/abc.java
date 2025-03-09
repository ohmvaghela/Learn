
import java.util.ArrayList;

class A implements Cloneable{
  int a;
  int b;
  String c;
  public ArrayList<Integer> arr;

  public A(int a, int b, String c, ArrayList<Integer> arr){
    this.a = a;this.b = b; this.c = c; this.arr = arr;
  }

  @Override
  public A clone() throws CloneNotSupportedException {
    A newA = (A) super.clone();
    newA.arr = new ArrayList<Integer> (this.arr);
    return newA;
  }

  @Override
  public String toString(){
    return this.a+"-"+this.b+"-"+this.c + "-" +arr.size();
  }
}

class Main{
  public static void main(String[] args) throws  CloneNotSupportedException {
    A oldA = new A(1,2,"ohm", new ArrayList<>());
    oldA.arr.add(1);
    A newA = oldA.clone();

    newA.arr.add(2);
    System.out.println(oldA);
    System.out.println(newA);
  }
}