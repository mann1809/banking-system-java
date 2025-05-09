package com.youtube.bank.main;

import com.youtube.bank.entity.Transaction;
import com.youtube.bank.entity.User;
import com.youtube.bank.service.UserService;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner= new Scanner(System.in);
    static UserService userService = new UserService();
    static Main main=new Main();
    public static void main(String[] args){
        while(true){
            System.out.println("Enter Username");
            String username= scanner.next();
            System.out.println("Enter Password");
            String password= scanner.next();

            User user = userService.login(username,password);
            if(user!= null && user.getRole().equals("admin")){
                main.initAdmin();
            }else if(user!= null && user.getRole().equals("user")){
                main.initCustomer(user);
            }else{
                System.out.println("Logged in failed");
            }
        }
    }

    public void initAdmin(){
        String userId="";
        boolean flag = true;
        while(flag){
            System.out.println("1. Exit/Logout");
            System.out.println("2. Create a Customer Account");
            System.out.println("3. Show Transaction History");
            System.out.println("4. Check Bank Balance");
            System.out.println("5. Approve Cheque Book Request");

            int selectedOption= scanner.nextInt();

            switch(selectedOption){
                case 1:
                    flag=false;
                    System.out.println("You are successfully logged out");
                    break;
                case 2:
                    main.addCustomer();
                    break;
                case 3:
                    System.out.println("Enter the user id you want to check transaction history for");
                    userId = scanner.next();
                    main.transactionHistory(userId);
                    break;
                case 4:
                    System.out.println("Enter the user id you want to check bank balance for");
                    userId = scanner.next();
                    Double balance = main.checkAccountBalance(userId);
                    System.out.println("Account Balance for "+ userId+ ": " + balance);
                    break;
                case 5:
                    List<String> result = main.getUserIdForChequeBookRequests();
                    System.out.println("Please Select User Id from below:");
                    System.out.println(result);
                    String id = scanner.next();
                    main.approveChequeBookRequests(id);
                    break;
                default:
                    System.out.println("Please choose correct option!");
            }
        }

    }

    private void initCustomer(User user){
        boolean flag = true;
        System.out.println("You are successfully logged in as Customer....");
        while(flag){
            System.out.println("1. Exit/Logout");
            System.out.println("2. Check account balance");
            System.out.println("3. Transfer Funds");
            System.out.println("4. Show Transaction History");
            System.out.println("5. Raise Cheque Book Request");


            try {
                int selectedOption= scanner.nextInt();
                String result="";
                switch(selectedOption){
                    case 1:
                        flag=false;
                        System.out.println("You are successfully logged out");
                        break;
                    case 2:
                        Double balance=main.checkAccountBalance(user.getUsername());
                        if(balance!=null)
                        {
                            System.out.println("Your bank balance is " + balance);
                        }else{
                            System.out.println("Check your username....");
                        }
                        break;
                    case 3:
                        result= main.transferFunds(user);
                        if(result!=null){
                            System.out.println(result);
                        }
                        break;
                    case 4:
                        main.transactionHistory(user.getUsername());
                        //System.out.println(history);
                        break;
                    case 5:
                        result = main.raiseChequeBookRequest(user.getUsername());
                        System.out.println(result);
                        break;
                    default:
                        System.out.println("Please choose correct option!");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addCustomer(){

        System.out.println("Enter Username");
        String username=scanner.next();

        System.out.println("Enter Password");
        String password=scanner.next();

        System.out.println("Enter Contact Number");
        String contact=scanner.next();

        boolean result = userService.addCustomer(username,password,contact);

        if(result){
            System.out.println("Customer account is created successfully....");
        }else{
            System.out.println("Customer account creation failed....");
        }
    }

    private Double checkAccountBalance(String userId){
        return userService.checkAccountBalance(userId);
    }

    private String transferFunds(User Payer){
        System.out.println("Enter the payee UserId");
        String payeeUserId=scanner.next();
        //check if the payee id exists
        User Payee= main.checkId(payeeUserId);

        if(Payee == null){
            System.out.println("Payee doesn't exist");
            return null;
        }
        else if(Payee.getUsername().equals(Payer.getUsername())){
            System.out.println("Payer and Payee can't be same");
            return null;
        }



        System.out.println("Enter the amount you want to transfer");
        Double amount=scanner.nextDouble();
        //Used checkaccountbalance method first to know if amount>balance
        //you wrote that logic in user repository
        return userService.transferFunds(Payer,Payee,amount);

    }

    private User checkId(String payee){
        return userService.checkId(payee);
    }

    private void transactionHistory(String userId){
        userService.transactionHistory(userId);
    }

    private String raiseChequeBookRequest(String userId){
        return userService.raiseChequeBookRequest(userId);
    }

    private void approveChequeBookRequests(String userId){
        userService.approveChequeBookRequests(userId);
    }

    private List<String> getUserIdForChequeBookRequests(){
        return userService.getUserIdForChequeBookRequests();
    }
}



