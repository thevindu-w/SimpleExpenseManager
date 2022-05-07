package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class expenseManagerTest {
    private ExpenseManager expenseManager;

    @Before
    public void setSQLiteHelper(){
        Context context = ApplicationProvider.getApplicationContext();
        this.expenseManager = new PersistentExpenseManager(context);
    }

    @Test
    public void addAccountTest(){
        String accountNumber = "91827Q";
        String bankName = "sampleBank";
        String accountHolderName = "sampleHolderName";
        double balance = 567.;
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        try{
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }

        expenseManager.addAccount(accountNumber, bankName, accountHolderName, balance);

        Account retrievedAccount = null;
        try {
            retrievedAccount = accountDAO.getAccount(accountNumber);
        }catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        assertNotNull(retrievedAccount);

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
    public void getAccountNumbersTest(){
        String accountNumber = "51627W";
        String bankName = "exampleBank";
        String accountHolderName = "sampleHolderName";
        double balance = 2134.;
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        try{
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }

        expenseManager.addAccount(accountNumber, bankName, accountHolderName, balance);

        List<String> accountNumbers = expenseManager.getAccountNumbersList();
        assertNotNull(accountNumbers);
        assertFalse(accountNumbers.isEmpty());
    }

    @Test
    public void updateBalanceTest(){
        String accountNumber = "72839U";
        String bankName = "Bank001";
        String accountHolderName = "Holder001";
        double balance = 1000.;
        double increment = 2000.;
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        try{
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }

        expenseManager.addAccount(accountNumber, bankName, accountHolderName, balance);

        try {
            expenseManager.updateAccountBalance(accountNumber, 1,1,2022, ExpenseType.INCOME, String.valueOf(increment));
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }

        Account retrievedAccount = null;
        try {
            retrievedAccount = accountDAO.getAccount(accountNumber);
        }catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        assertNotNull(retrievedAccount);

        double retBalance = retrievedAccount.getBalance();
        assertEquals(balance+increment, retBalance, 0.0001);
    }

    @Test
    public void getNonExistingAccount(){
        String accountNumber = "notExist";
        AccountDAO accountDAO = expenseManager.getAccountsDAO();

        try{
            accountDAO.removeAccount(accountNumber);
        } catch (InvalidAccountException ignored) {
        }

        Account retrievedAccount = null;
        try {
            retrievedAccount = accountDAO.getAccount(accountNumber);
        }catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        assertNull(retrievedAccount);
    }
}
