
class B{}

class A implements Cloneable{
  B b;
  int intA;
  String stringA;
  public A(int intA, String stringA, B b){
    this.intA = intA; this.stringA = stringA; this.b = b;
  }
  @Override
  public A clone() throws CloneNotSupportedException {
    A newA = (A)super.clone();
    newA.b = new B();
    return newA;
  }

  @Override
  public boolean equals(Object obj){
    if(this == obj) return true;
    if(obj == null || obj.getClass() != getClass()) return false;
    A newObj = (A) obj;
    return (newObj.intA == this.intA) && (newObj.stringA.equals(this.stringA)) && (newObj.b.equals(this.b));
  }
}

class Main{
  public static void main(String[] args){
    
  }
}