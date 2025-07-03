package com.personal.cashflow.room;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TransactionRepository {
    private static TransactionRepository instance;
    private final TransactionDao transactionDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private TransactionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        transactionDao = db.transactionDao();
    }

    public static synchronized TransactionRepository getInstance(Context context) {
        if (instance == null) {
            instance = new TransactionRepository(context.getApplicationContext());
        }
        return instance;
    }

    public void insertTransaction(TransactionModel txn) {
        executor.execute(() -> transactionDao.insert(txn));
    }

    public LiveData<List<TransactionModel>> getAllTransactions() {
        return transactionDao.getAllLive();
    }

    public List<String> getAllBankNames() {
        return transactionDao.getAllBankNames();
    }

    public LiveData<List<TransactionModel>> getTransactionsByBank(String bank) {
        return transactionDao.getTransactionsByBank(bank);
    }
}
