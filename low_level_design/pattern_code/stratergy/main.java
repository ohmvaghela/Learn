import java.util.Scanner;

interface Payment{
  public void askForDetails();
  public void Success();
}
class CardPayment implements Payment{
  private String cardNumber;
  private String cvv;
  private String expiryDate;
  private Scanner scanner;
  
  public CardPayment(){
    this.scanner = new Scanner(System.in);
  }

  public void askForDetails(){
    System.out.print("Enter Card Number: ");
    this.cardNumber = scanner.nextLine();

    System.out.print("Enter CVV: ");
    this.cvv = scanner.nextLine();

    System.out.print("Enter Expiry Date (MM/YY): ");
    this.expiryDate = scanner.nextLine();
  }

  public void Success(){
    System.out.println("Payment via Card success");
  }
}

class UPIPayment implements Payment{
  private Scanner scanner;
  private String email;
  private String upiId;
  public UPIPayment(){
    this.scanner = new Scanner(System.in);
  }
  public void askForDetails(){
    System.out.print("Enter Email: ");
    this.email = scanner.nextLine();
    
    System.out.print("Enter UPI ID: ");
    this.upiId = scanner.nextLine();
  }
  public void Success(){
    System.out.println("Payment via UPI success");
  }
}

class PaymentProcessor{
  private Payment payment;
  public void Create(int type){
    switch (type) {
        case 1:
          this.payment = new CardPayment();        
          break;
        case 2:
          this.payment = new UPIPayment();        
          break;
        default:
          this.payment = null;
    }
  }

  public void process(){
    if(this.payment == null){
      System.out.println("Transaction cancelled due to invalid payment type.");
    }else{
      payment.askForDetails();
      payment.Success();
    }
  }
}

public class main{
  
  public static void main(String[] args) {
    
    Scanner input = new Scanner(System.in);
    System.out.println("Select payment method :");
    System.out.println("1. Credit Card \n 2. UPI");
    int paymentType = input.nextInt();
    input.nextLine();

    PaymentProcessor paymentProcessor = new PaymentProcessor();
    paymentProcessor.Create(paymentType);
    paymentProcessor.process();


    input.close();      
  }
  
}
