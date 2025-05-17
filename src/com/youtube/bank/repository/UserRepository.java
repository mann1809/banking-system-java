package com.youtube.bank.repository;

import com.youtube.bank.entity.Transaction;
import com.youtube.bank.entity.User;
import com.youtube.bank.utility.Database;

import java.sql.*;
import java.sql.Date;
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

        /*return users.stream()
                .filter(user-> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);*/
        String sql="select * from users where username=? and password=?";
        String role="";
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

    public boolean addCustomer(String username, String password, String contact) throws SQLException {
        //User user =new User(username,password,contact,"user",500.0);
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

    public Double checkAccountBalance(String userId){
//        List<User> result= users.stream().filter(user -> user.getUsername().equals(userId)).collect(Collectors.toList());
//        if(!result.isEmpty()){
//            return result.getFirst().getAccountBalance();
//        }else{
//            return null;
//        }

        /*return users.stream()
                .filter(user -> user.getUsername().equals(userId))
                .map(User::getAccountBalance)
                .findFirst()
                .orElse(0.0);*/

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

    public String transferFunds(User Payer,User Payee,Double amount) {
        double balance = checkAccountBalance(Payer.getUsername());
        if(amount>balance){
            System.out.println("Insufficient Funds");
            return null;
        }else{
            LocalDateTime date=LocalDateTime.now();
            Double payeeBalance=Payee.getAccountBalance();
            Double payerBalance=Payer.getAccountBalance();
            /*Payee.setAccountBalance(payeeBalance+amount);
            Payer.setAccountBalance(payerBalance-amount);*/

            Double payee= Payee.getAccountBalance()+amount;
            Double payer= Payer.getAccountBalance()-amount;

            String sql= "UPDATE users SET \"accountBalance\" =? where \"username\"=?";

            try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
                pst.setDouble(1,payee);
                pst.setString(2, Payee.getUsername());
                pst.executeUpdate();

            }catch (SQLException e){
                e.printStackTrace();
            }

            try(Connection conn=Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
                pst.setDouble(1,payer);
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
                pst.setDouble(4,payerBalance);
                pst.setDouble(5,payer);
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
                pst.setDouble(4,payeeBalance);
                pst.setDouble(5,payee);
                pst.setString(6,Payee.getUsername());
                pst.executeUpdate();

            }catch (SQLException e){
                e.printStackTrace();
            }
            return "Funds Transferred Successfully";

            /*Transaction transaction1 =new Transaction(date,amount,"transferred to ",Payee.getUsername(),payerBalance, Payer.getAccountBalance(), Payer.getUsername());
            Transaction transaction2 =new Transaction(date,amount,"received from ",Payer.getUsername(), payeeBalance, Payee.getAccountBalance(), Payee.getUsername());
            transactionHistory.add(transaction1);
            transactionHistory.add(transaction2);*/

        }

    }

    public User checkId(String payeeUserId)  {
        /*return users.stream()
                .filter(user-> user.getUsername().equals(payeeUserId))
                .findFirst()
                .orElse(null);*/

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

    /*public List<Transaction> transactionHistory(String userId){*/
    public void transactionHistory(String userId){
        //List<Transaction> result= transactionHistory.stream().filter(transaction -> transaction.getTransactionPerformedBy().equals(userId)).collect(Collectors.toList());
        /*System.out.println("      Date \t\t        Amount \t Type \t Initial Balance \t Final Balance");
        System.out.println("-------------------------------------------------------------------------");*/
        // dd mm yyyy hh mm ss am
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a");
        for(Transaction t: result){
            System.out.println(t.getTransactionDate().format(formatter)
                + "\t" + t.getTransactionAmount()
                    + "\t" + (t.getTransactionType().equals("transferred to ")?"Debit":"Credit")
                    + "\t\t" + t.getInitialBalance()
                    + "\t       "
                    + t.getFinalBalance()
            );
            System.out.println("-------------------------------------------------------------------------");
        }*/
        System.out.println(userId);
        System.out.println("      Date \t\t  Amount \t Type \t User Type \t Initial Balance \t Final Balance\t Transaction Performed By");
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

        /*Collections.reverse(result);
        return result;*/
        }

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
                int r=pt.executeUpdate();
                return "Request Raised Successfully";
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
        /*if(requests.containsKey(userId) && requests.get(userId).equals("Pending")){
            return "Request Already Raised and Pending for Approval";
        }else if(requests.containsKey(userId) && requests.get(userId).equals("Approved")){
            return "Request is Approved";
        }
        else{
            requests.put(userId,"Pending");
            return "Request Raised Successfully";
        }*/



    public void approveChequeBookRequests(){
        /*if(requests.containsKey(userId)){
            requests.put(userId,"Approved");
            System.out.println("Request Approved Successfully!");
        }*/
        String sql="select * from \"chequebookrequest\" where \"requestStatus\" = 'Pending'";
        try(Connection conn= Database.getConnection();PreparedStatement pst=conn.prepareStatement(sql)){
            ResultSet result=pst.executeQuery();
            while(result!=null && result.next()){
                System.out.println(result.getString("userId"));
            }
            System.out.println("Please Select User Id from below:");
            Scanner scanner= new Scanner(System.in);
            String id = scanner.next();
            sql="update chequebookrequest set \"requestStatus\" = 'Approved' where \"userId\" = ?";
            try(PreparedStatement pt=conn.prepareStatement(sql)){
                pt.setString(1,id);
                pt.executeUpdate();
                System.out.println("Approved Successfully....");
            }catch (SQLException e){
                e.printStackTrace();
            }

        }catch (SQLException e){
            e.printStackTrace();
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

