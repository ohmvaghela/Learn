// Step 1: Define the 'Shape' interface.
interface Shape {
  void accept(ShapeVisitor visitor);
}

// Step 2: Concrete Shape classes implementing the Shape interface.
class Circle implements Shape {
  private float radius;
  
  public Circle(float radius) {
      this.radius = radius;
  }

  public float getRadius() {
      return radius;
  }

  @Override
  public void accept(ShapeVisitor visitor) {
      visitor.visit(this);  // Accept the visitor
  }
}

class Square implements Shape {
  private float side;
  
  public Square(float side) {
      this.side = side;
  }

  public float getSide() {
      return side;
  }

  @Override
  public void accept(ShapeVisitor visitor) {
      visitor.visit(this);  // Accept the visitor
  }
}

// Step 3: Define the Visitor interface that will define operations for each shape.
interface ShapeVisitor {
  void visit(Circle circle);
  void visit(Square square);
}

// Step 4: Concrete Visitor classes implementing the ShapeVisitor interface.
class AreaCalculator implements ShapeVisitor {
  @Override
  public void visit(Circle circle) {
      float area = (float)(Math.PI * circle.getRadius() * circle.getRadius());
      System.out.println("Area of Circle: " + area);
  }

  @Override
  public void visit(Square square) {
      float area = square.getSide() * square.getSide();
      System.out.println("Area of Square: " + area);
  }
}

class PerimeterCalculator implements ShapeVisitor {
  @Override
  public void visit(Circle circle) {
      float perimeter = (float)(2 * Math.PI * circle.getRadius());
      System.out.println("Perimeter of Circle: " + perimeter);
  }

  @Override
  public void visit(Square square) {
      float perimeter = 4 * square.getSide();
      System.out.println("Perimeter of Square: " + perimeter);
  }
}

// Step 5: Main class to run the Visitor pattern example.
public class Main {
  public static void main(String[] args) {
      Shape circle = new Circle(5.0f);
      Shape square = new Square(4.0f);

      // Create visitors
      ShapeVisitor areaVisitor = new AreaCalculator();
      ShapeVisitor perimeterVisitor = new PerimeterCalculator();

      // Apply the visitors to the shapes
      circle.accept(areaVisitor);
      circle.accept(perimeterVisitor);

      square.accept(areaVisitor);
      square.accept(perimeterVisitor);
  }
}
