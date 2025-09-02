package com.bank.repository;

import com.bank.entity.User;
import com.bank.utility.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class UserRepository {
    private static Set<User> users= new HashSet<>();

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

    //checks login info of user
    public User login(String username, String password){
        String sql="select * from users where username=? and password=?";
        try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setString(1,username);
            pst.setString(2,password);
            ResultSet result=pst.executeQuery();
            if(result.next()){
                return new User(username,password,result.getString("contactNumber"),result.getString("role"), result.getDouble("accountBalance"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //To create new customer account
    public boolean addCustomer(String username, String password, String contact) throws SQLException {
        String sql= "INSERT INTO users (username,password,\"contactNumber\",\"role\",\"accountBalance\") " +
                "VALUES (?, ?, ?, 'user', '500.0')";
        try(Connection conn= Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setString(1,username);
            pst.setString(2,password);
            pst.setString(3,contact);
            pst.executeUpdate();
            return true;
        }catch(SQLException e){
            return false;
        }
    }

    //To get account balance for given user
    public Double checkAccountBalance(String userId){

        String sql="select \"accountBalance\" from users where username = ?";
        try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)) {
            pst.setString(1,userId);
            ResultSet result=pst.executeQuery();
            if(result.next()){
                Double balance = result.getDouble("accountBalance");
                return balance;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    //To transfer funds from one account to another
    public String transferFunds(User Payer,User Payee,Double amount) {
        double balance = checkAccountBalance(Payer.getUsername());
        if(amount>balance){
            System.out.println("Insufficient Funds");
            return null;
        }else{
            LocalDateTime date=LocalDateTime.now();
            Double payeeFunds =Payee.getAccountBalance();
            Double payerFunds=Payer.getAccountBalance();

            Double payeeBalance= Payee.getAccountBalance()+amount;
            Double payerBalance= Payer.getAccountBalance()-amount;

            String sql= "UPDATE users SET \"accountBalance\" =? where \"username\"=?";

            try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
                pst.setDouble(1,payeeBalance);
                pst.setString(2, Payee.getUsername());
                pst.executeUpdate();

            }catch (SQLException e){
                e.printStackTrace();
            }

            try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
                pst.setDouble(1,payerBalance);
                pst.setString(2, Payer.getUsername());
                pst.executeUpdate();

            }catch (SQLException e){
                e.printStackTrace();
            }

            sql = "INSERT INTO transaction(\"transactionDate\",\"transactionAmount\",\"transactionType\",\"userType\",\"initialBalance\",\"finalBalance\",\"transactionPerformedBy\")" +
                    " VALUES (?,?,'Debit',?,?,?,?)";

            try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
                pst.setTimestamp(1,Timestamp.valueOf(date));
                pst.setDouble(2, amount);
                pst.setString(3, Payee.getUsername());
                pst.setDouble(4,payerFunds);
                pst.setDouble(5,payerBalance);
                pst.setString(6,Payer.getUsername());
                pst.executeUpdate();

            }catch (SQLException e){
                e.printStackTrace();
            }

            sql = "INSERT INTO transaction(\"transactionDate\",\"transactionAmount\",\"transactionType\",\"userType\",\"initialBalance\",\"finalBalance\",\"transactionPerformedBy\")" +
                    " VALUES (?,?,'Credit',?,?,?,?)";

            try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
                pst.setTimestamp(1,Timestamp.valueOf(date));
                pst.setDouble(2, amount);
                pst.setString(3, Payer.getUsername());
                pst.setDouble(4,payeeFunds);
                pst.setDouble(5,payeeBalance);
                pst.setString(6,Payee.getUsername());
                pst.executeUpdate();

            }catch (SQLException e){
                e.printStackTrace();
            }
            return "Funds Transferred Successfully";
        }
    }

    //To check if user account exists
    public User checkId(String payeeUserId)  {
        String sql="select * from users where username=?";
        try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setString(1,payeeUserId);
            ResultSet result= pst.executeQuery();
            if(result.next()){
                return new User(payeeUserId,result.getString("password"),result.getString("contactNumber"),result.getString("role"),result.getDouble("accountBalance"));
            }
        }catch (SQLException e){
            e.printStackTrace();;
        }
        return null;
    }

    //To get transaction history for given user
    public void transactionHistory(String userId){

        System.out.println("  Date \t\t  Amount \t Type \t User Id \t Initial Balance \t Final Balance\t Transaction Performed By");
        System.out.println("-------------------------------------------------------------------------");
        String sql="select * from transaction where \"transactionPerformedBy\"=?";
        try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setString(1,userId);
            ResultSet result= pst.executeQuery();

            while(result != null && result.next()){
                System.out.println(result.getDate("transactionDate")
                        + "\t\t" + result.getDouble("transactionAmount")
                        + "\t" + result.getString("transactionType")
                        + "\t" + result.getString("userType")
                        + "\t\t" + result.getDouble("initialBalance")
                        + "\t\t\t" + result.getDouble("finalBalance")
                        + "\t\t\t" + result.getString("transactionPerformedBy")
                );
                System.out.println("-------------------------------------------------------------------------");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    //To raise request for Cheque Book
    public String raiseChequeBookRequest(String userId) {
        String sql = "select * from chequebookrequest where \"userId\"=?";
        try (Connection conn = Database.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, userId);
            ResultSet result = pst.executeQuery();
            if (result != null && result.next()) {
                if (result.getString("requestStatus").equals("Pending")) {
                    return "Request Already Raised and Pending for Approval";
                } else if (result.getString("requestStatus").equals("Approved")) {
                    return "Request is Approved";
                }
            }

            sql = "insert into chequebookrequest(\"userId\",\"requestStatus\") values(?,'Pending')";
            try(PreparedStatement pt=conn.prepareStatement(sql)) {
                pt.setString(1, userId);
                pt.executeUpdate();
                return "Request Raised Successfully";
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //To Approve Pending Cheque Book Requests
    public void approveChequeBookRequests(){
        Scanner scanner= new Scanner(System.in);
        String sql="select * from \"chequebookrequest\" where \"requestStatus\" = 'Pending'";
        try(Connection conn= Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
            ResultSet result=pst.executeQuery();
            while(result!=null && result.next()){
                System.out.println(result.getString("userId"));
            }
            System.out.println("Please Select User Id from below:");
            String userId = scanner.next();
            sql="update chequebookrequest set \"requestStatus\" = 'Approved' where \"userId\" = ?";
            try(PreparedStatement pt=conn.prepareStatement(sql)){
                pt.setString(1,userId);
                pt.executeUpdate();
                System.out.println("Approved Successfully....");
            }catch (SQLException e){
                e.printStackTrace();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}

