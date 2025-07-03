package com.personal.cashflow.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TransactionModel transaction);

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    LiveData<List<TransactionModel>> getAllLive();
    @Query("SELECT DISTINCT source FROM transactions ORDER BY source")
    List<String> getAllBankNames();

    @Query("SELECT * FROM transactions WHERE source = :bank ORDER BY id DESC")
    LiveData<List<TransactionModel>> getTransactionsByBank(String bank);

}
