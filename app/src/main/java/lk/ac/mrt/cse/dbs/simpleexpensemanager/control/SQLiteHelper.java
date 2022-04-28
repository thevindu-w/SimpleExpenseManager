/*
 * Copyright 2022 Thevindu Wijesekera.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper implements Serializable {
    private static final String ACCOUNT_TABLE = "accounts";
    private static final String ACCOUNT_NO_FIELD = "accountNo";
    private static final String BANK_NAME_FIELD = "bankName";
    private static final String ACCOUNT_HOLDER_NAME_FIELD = "accountHolderName";
    private static final String BALANCE_FIELD = "balance";
    private static final String TRANSACTION_TABLE = "transactions";
    private static final String EXPENSE_TYPE_FIELD = "expenseType";
    private static final String AMOUNT_FIELD = "amount";
    private static final String DATE_FIELD = "date";

    public SQLiteHelper(Context context, String dbName) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " + ACCOUNT_TABLE + " (" + ACCOUNT_NO_FIELD + " varchar(30) primary key, " + BANK_NAME_FIELD + " varchar(50), " + ACCOUNT_HOLDER_NAME_FIELD + " varchar(80), " + BALANCE_FIELD + " real)"
        );
        sqLiteDatabase.execSQL(
                "create table " + TRANSACTION_TABLE + " (transactionID integer primary key, " + ACCOUNT_NO_FIELD + " varchar(30), " + EXPENSE_TYPE_FIELD + " varchar(30), " + AMOUNT_FIELD + " real, "+  DATE_FIELD + " varchar(20))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void addAccount(Account account) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACCOUNT_NO_FIELD, account.getAccountNo());
            contentValues.put(BANK_NAME_FIELD, account.getBankName());
            contentValues.put(ACCOUNT_HOLDER_NAME_FIELD, account.getAccountHolderName());
            contentValues.put(BALANCE_FIELD, account.getBalance());
            db.insert(ACCOUNT_TABLE, null, contentValues);
        } catch (RuntimeException ignored) {
        }
    }

    public boolean removeAccount(String accountNo) {
        if (accountNo==null) return false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(ACCOUNT_TABLE, ACCOUNT_NO_FIELD+"=?", new String[]{accountNo}) > 0;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public Account getAccount(String accountNo) {
        if (accountNo == null) return null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(ACCOUNT_TABLE, new String[]{BANK_NAME_FIELD, ACCOUNT_HOLDER_NAME_FIELD, BALANCE_FIELD}, ACCOUNT_NO_FIELD + "=?",
                    new String[]{accountNo}, null, null, null, null);
            if (cursor == null) return null;
            if (!cursor.moveToFirst()) return null;
            Account account = new Account(accountNo, cursor.getString(0), cursor.getString(1), Double.parseDouble(cursor.getString(2)));
            cursor.close();
            return account;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public List<Account> getAccountsList() {
        List<Account> accountList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(ACCOUNT_TABLE, new String[]{ACCOUNT_NO_FIELD, BANK_NAME_FIELD, ACCOUNT_HOLDER_NAME_FIELD, BALANCE_FIELD}, null,
                    null, null, null, null, null);
            if (cursor == null) return accountList;
            if (cursor.moveToFirst()) {
                do {
                    Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)));
                    accountList.add(account);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return accountList;
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    public List<String> getAccountNumbersList() {
        List<String> accountNumbersList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(ACCOUNT_TABLE, new String[]{ACCOUNT_NO_FIELD}, null,
                    null, null, null, null, null);
            if (cursor == null) return accountNumbersList;
            if (cursor.moveToFirst()) {
                do {
                    accountNumbersList.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return accountNumbersList;
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    public void updateBalance(String accountNo, double newBalance){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(BALANCE_FIELD, newBalance);
            db.update(ACCOUNT_TABLE, contentValues, ACCOUNT_NO_FIELD+"=?", new String[]{accountNo});
        } catch (RuntimeException ignored) {
        }
    }

    public void logTransaction(Transaction transaction){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACCOUNT_NO_FIELD, transaction.getAccountNo());
            contentValues.put(EXPENSE_TYPE_FIELD, transaction.getExpenseType().name());
            contentValues.put(AMOUNT_FIELD, transaction.getAmount());
            contentValues.put(DATE_FIELD, transaction.getDate().getTime());
            db.insert(TRANSACTION_TABLE, null, contentValues);
        } catch (RuntimeException ignored) {
        }
    }

    public List<Transaction> getAllTransactionLogs(){
        return getPaginatedTransactionLogs(null);
    }

    public List<Transaction> getPaginatedTransactionLogs(String limit){
        if (limit!=null) Log.d("Limit", limit);
        List<Transaction> transactionList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TRANSACTION_TABLE, new String[]{ACCOUNT_NO_FIELD, EXPENSE_TYPE_FIELD, AMOUNT_FIELD, DATE_FIELD}, null,
                    null, null, null, DATE_FIELD+" DESC", limit);
            if (cursor == null) return transactionList;
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction(new Date(cursor.getLong(3)), cursor.getString(0), ExpenseType.valueOf(cursor.getString(1)), cursor.getDouble(2));
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
            cursor.close();
            Collections.reverse(transactionList);
            return transactionList;
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }
}
