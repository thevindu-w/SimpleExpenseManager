package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class expenseManagerTest {
    private ExpenseManager expenseManager;

    @Before
    public void setSQLiteHelper() {
        Context context = ApplicationProvider.getApplicationContext();
        this.expenseManager = new PersistentExpenseManager(context);
    }

    @Test
    public void addAccountTest() {
        String accountNumber = "91827Q";
        String bankName = "sampleBank";
        String accountHolderName = "sampleHolderName";
        double balance = 567.;
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        try {
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }
        //add account to test
        expenseManager.addAccount(accountNumber, bankName, accountHolderName, balance);

        Account retrievedAccount = null;
        try {
            retrievedAccount = accountDAO.getAccount(accountNumber);
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        // the added account should be returned
        assertNotNull(retrievedAccount);

        // check if the account details are same
        String retAccountNumber = retrievedAccount.getAccountNo();
        assertEquals(accountNumber, retAccountNumber);
        String retBankName = retrievedAccount.getBankName();
        assertEquals(bankName, retBankName);
        String retAccountHolderName = retrievedAccount.getAccountHolderName();
        assertEquals(accountHolderName, retAccountHolderName);
        double retBalance = retrievedAccount.getBalance();
        assertEquals(balance, retBalance, 0.0001);
    }

    @Test
    public void getAccountNumbersTest() {
        String accountNumber = "51627W";
        String bankName = "exampleBank";
        String accountHolderName = "sampleHolderName";
        double balance = 2134.;
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        try {
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }
        // add account to check
        expenseManager.addAccount(accountNumber, bankName, accountHolderName, balance);

        List<String> accountNumbers = expenseManager.getAccountNumbersList();
        assertNotNull(accountNumbers);
        // newly added account number should be in the list
        assertTrue(accountNumbers.contains(accountNumber));
    }

    @Test
    public void updateBalanceTest() {
        String accountNumber = "72839U";
        String bankName = "Bank001";
        String accountHolderName = "Holder001";
        double balance = 1000.; // initial balance
        double increment = 2000.;
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        try {
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }
        //make the account to update balance
        expenseManager.addAccount(accountNumber, bankName, accountHolderName, balance);

        try {
            expenseManager.updateAccountBalance(accountNumber, 1, 1, 2022, ExpenseType.INCOME, String.valueOf(increment));
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }

        Account retrievedAccount = null;
        try {
            retrievedAccount = accountDAO.getAccount(accountNumber);
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        assertNotNull(retrievedAccount);

        double newBalance = retrievedAccount.getBalance();
        // new balance should be balance + increment
        assertEquals(balance + increment, newBalance, 0.0001);
    }

    @Test
    public void getNonExistingAccount() {
        String accountNumber = "notExist";
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        // remove the account if exists
        try {
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }

        Account retrievedAccount = null;
        try {
            retrievedAccount = accountDAO.getAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }
        // account should be null since it doesn't exist
        assertNull(retrievedAccount);
    }

    @Test
    public void getTransactionLogsTest() {
        String accountNumber = "43210T";
        String bankName = "Bank002";
        String accountHolderName = "Holder002";
        double balance = 1000.;
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        try {
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }

        //create an account to make a transaction
        expenseManager.addAccount(accountNumber, bankName, accountHolderName, balance);

        TransactionDAO transactionDAO = expenseManager.getTransactionsDAO();
        // get the number of transactions before making a transaction
        List<Transaction> beforeTransactions = expenseManager.getTransactionLogs();
        assertNotNull(beforeTransactions);
        int beforeCnt = beforeTransactions.size();

        //make a transaction
        Date date = new Date();
        double amount = 500.;
        transactionDAO.logTransaction(date, accountNumber, ExpenseType.INCOME, amount);

        // get the number of transactions after making a transaction
        List<Transaction> afterTransactions = expenseManager.getTransactionLogs();
        assertNotNull(afterTransactions);
        int afterCnt = afterTransactions.size();

        // number of transactions in the log should have been increased by 1 after transaction
        assertEquals(afterCnt, beforeCnt+1);
    }
}
