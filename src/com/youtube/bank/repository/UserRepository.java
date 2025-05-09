package com.youtube.bank.repository;

import com.youtube.bank.entity.Transaction;
import com.youtube.bank.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class UserRepository {
    private static Set<User> users= new HashSet<>();
    List<Transaction> transactionHistory= new ArrayList<>();
    Map<String,String> requests=new HashMap<>();
    static{
        User user1=new User("admin", "admin", "1230",  "admin", 0.0);
        User user2=new User("user2", "user2", "3456",  "user", 1000.0);
        User user3=new User("user3", "user3", "3445",  "user", 2000.0);
        users.add(user1);
        users.add(user2);
        users.add(user3);
    }

    public void printUsers(){
        System.out.println(users);
    }

    public User login(String username, String password){
//        List<User> finalList= users.stream().filter(user-> user.getUsername().equals(username) && user.getPassword().equals(password)).collect(Collectors.toList());
//        if(!finalList.isEmpty()){
//            return finalList.get(0);
//        }else{
//            return null;
//        }

        return users.stream()
                .filter(user-> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);

    }

    public boolean addCustomer(String username, String password, String contact){
        User user =new User(username,password,contact,"user",500.0);
        return users.add(user);
    }

    public Double checkAccountBalance(String userId){
//        List<User> result= users.stream().filter(user -> user.getUsername().equals(userId)).collect(Collectors.toList());
//        if(!result.isEmpty()){
//            return result.getFirst().getAccountBalance();
//        }else{
//            return null;
//        }

        return users.stream()
                .filter(user -> user.getUsername().equals(userId))
                .map(User::getAccountBalance)
                .findFirst()
                .orElse(0.0);

    }

    public String transferFunds(User Payer,User Payee,Double amount){
        double balance = checkAccountBalance(Payer.getUsername());
        if(amount>balance){
            System.out.println("Insufficient Funds");
            return null;
        }else{
            LocalDateTime date=LocalDateTime.now();
            Double payeeBalance=Payee.getAccountBalance();
            Double payerBalance=Payer.getAccountBalance();
            Payee.setAccountBalance(payeeBalance+amount);
            Payer.setAccountBalance(payerBalance-amount);

            Transaction transaction1 =new Transaction(date,amount,"transferred to ",Payee.getUsername(),payerBalance, Payer.getAccountBalance(), Payer.getUsername());
            Transaction transaction2 =new Transaction(date,amount,"received from ",Payer.getUsername(), payeeBalance, Payee.getAccountBalance(), Payee.getUsername());
            transactionHistory.add(transaction1);
            transactionHistory.add(transaction2);
            return "Funds Transferred Successfully";
        }

    }

    public User checkId(String payeeUserId){
        return users.stream()
                .filter(user-> user.getUsername().equals(payeeUserId))
                .findFirst()
                .orElse(null);
    }

    /*public List<Transaction> transactionHistory(String userId){*/
    public void transactionHistory(String userId){
        List<Transaction> result= transactionHistory.stream().filter(transaction -> transaction.getTransactionPerformedBy().equals(userId)).collect(Collectors.toList());
        System.out.println("      Date \t\t        Amount \t Type \t Initial Balance \t Final Balance");
        System.out.println("-------------------------------------------------------------------------");
        // dd mm yyyy hh mm ss am
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a");
        for(Transaction t: result){
            System.out.println(t.getTransactionDate().format(formatter)
                + "\t" + t.getTransactionAmount()
                    + "\t" + (t.getTransactionType().equals("transferred to ")?"Debit":"Credit")
                    + "\t\t" + t.getInitialBalance()
                    + "\t       "
                    + t.getFinalBalance()
            );
            System.out.println("-------------------------------------------------------------------------");
        }

        /*Collections.reverse(result);
        return result;*/
        }

    public String raiseChequeBookRequest(String userId) {
        if(requests.containsKey(userId) && requests.get(userId).equals("Pending")){
            return "Request Already Raised and Pending for Approval";
        }else if(requests.containsKey(userId) && requests.get(userId).equals("Approved")){
            return "Request is Approved";
        }
        else{
            requests.put(userId,"Pending");
            return "Request Raised Successfully";
        }

    }

    public void approveChequeBookRequests(String userId){
        if(requests.containsKey(userId)){
            requests.put(userId,"Approved");
            System.out.println("Request Approved Successfully!");
        }
    }

    public List<String> getUserIdForChequeBookRequests(){
//        List<String> result=new ArrayList<>();
//        for(Map.Entry<String,String> m : requests.entrySet()){
//            if(m.getValue().equals("Pending")){
//                result.add(m.getKey());
//            }
//        }

        List<String> result = requests.entrySet()
                .stream()
                .filter(m -> m.getValue().equals("Pending"))
                .map(m -> m.getKey())
                .collect(Collectors.toList());

        return result;
    }
}

