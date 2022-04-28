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

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.SQLiteHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    private final List<Transaction> transactions;
    private final SQLiteHelper sqLiteHelper;

    public PersistentTransactionDAO(SQLiteHelper sqLiteHelper) {
        transactions = new LinkedList<>();
        this.sqLiteHelper = sqLiteHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        sqLiteHelper.logTransaction(transaction);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return sqLiteHelper.getAllTransactionLogs();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        return sqLiteHelper.getPaginatedTransactionLogs(String.valueOf(limit));
    }

}
