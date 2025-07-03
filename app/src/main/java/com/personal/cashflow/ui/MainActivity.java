package com.personal.cashflow.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.cashflow.R;
import com.personal.cashflow.adapters.MonthSummaryAdapter;
import com.personal.cashflow.room.MonthSummary;
import com.personal.cashflow.room.TransactionModel;
import com.personal.cashflow.room.TransactionRepository;
import com.personal.cashflow.utils.MonthlyGrouper;
import com.personal.cashflow.utils.SmsReader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101;

    private RecyclerView rvMonthSummaries;
    private MonthSummaryAdapter adapter;
    private List<MonthSummary> summaryList = new ArrayList<>();

    private Button btnScanPastSms;
    private ProgressBar progressBar;
    private Spinner spinnerBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestSmsPermission();

        spinnerBank = findViewById(R.id.spinnerBank);
        btnScanPastSms = findViewById(R.id.btnScanPastSms);
        progressBar = findViewById(R.id.progressBar);
        rvMonthSummaries = findViewById(R.id.rvMonthSummaries);

        rvMonthSummaries.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MonthSummaryAdapter(this, summaryList);
        rvMonthSummaries.setAdapter(adapter);

        btnScanPastSms.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        SMS_PERMISSION_CODE);
            } else {
                startSmsScan();
            }
        });

        loadBankListAndSetupSpinner();
    }

    private void loadBankListAndSetupSpinner() {
        new Thread(() -> {
            List<String> banks = TransactionRepository.getInstance(this).getAllBankNames();
            banks.add(0, "All Banks");

            runOnUiThread(() -> {
                ArrayAdapter<String> bankAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        banks
                );
                spinnerBank.setAdapter(bankAdapter);

                spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedBank = parent.getItemAtPosition(position).toString();
                        LiveData<List<TransactionModel>> txnLiveData;

                        if (selectedBank.equals("All Banks")) {
                            txnLiveData = TransactionRepository.getInstance(MainActivity.this).getAllTransactions();
                        } else {
                            txnLiveData = TransactionRepository.getInstance(MainActivity.this).getTransactionsByBank(selectedBank);
                        }

                        txnLiveData.observe(MainActivity.this, transactions -> {
                            summaryList.clear();
                            summaryList.addAll(MonthlyGrouper.groupByMonth(transactions));
                            adapter.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            });
        }).start();
    }

    private void startSmsScan() {
        btnScanPastSms.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        SmsReader.scanSmsInBackground(this, count -> {
            progressBar.setVisibility(View.GONE);
            btnScanPastSms.setEnabled(true);
            Toast.makeText(this, count + " new transactions added", Toast.LENGTH_SHORT).show();

            // Refresh spinner again to update banks if needed
            loadBankListAndSetupSpinner();
        });
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                    SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                startSmsScan();
            } else {
                Toast.makeText(this, "SMS permissions are required to scan messages", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
