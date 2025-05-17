package com.youtube.bank.service;

import com.youtube.bank.entity.Transaction;
import com.youtube.bank.entity.User;
import com.youtube.bank.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserRepository userRepository= new UserRepository();

    public void printUsers(){
        userRepository.printUsers();
    }

    public User login(String username, String password){
        return userRepository.login(username,password);
    }

    public boolean addCustomer(String username, String password, String contact) throws SQLException {
        return userRepository.addCustomer(username,password,contact);
    }

    public Double checkAccountBalance(String userId){
        return userRepository.checkAccountBalance(userId);
    }

    public String transferFunds(User Payer, User Payee, Double amount){
        return userRepository.transferFunds(Payer,Payee,amount);
    }

    public User checkId(String payeeUserId){
        return userRepository.checkId(payeeUserId);
    }

    public void transactionHistory(String userId){
        userRepository.transactionHistory(userId);
    }

    public String raiseChequeBookRequest(String userId){
        return userRepository.raiseChequeBookRequest(userId);
    }

    public void approveChequeBookRequests(){
        userRepository.approveChequeBookRequests();
    }

    public List<String> getUserIdForChequeBookRequests(){
        return userRepository.getUserIdForChequeBookRequests();
    }
}
