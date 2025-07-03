package com.personal.cashflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.cashflow.R;
import com.personal.cashflow.room.MonthSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthSummaryAdapter
        extends RecyclerView.Adapter<MonthSummaryAdapter.MonthViewHolder> {

    private final List<MonthSummary> monthSummaries;
    private final Context context;

    public MonthSummaryAdapter(Context context, List<MonthSummary> monthSummaries) {
        this.context = context;
        this.monthSummaries = monthSummaries != null
                ? monthSummaries
                : new ArrayList<>();
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_month_summary, parent, false);
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        holder.bind(monthSummaries.get(position));
    }

    @Override
    public int getItemCount() {
        return monthSummaries.size();
    }

    // ---------- ViewHolder ----------
    class MonthViewHolder extends RecyclerView.ViewHolder {

        TextView tvMonth, tvDebit, tvCredit;
        RecyclerView rvTransactions;
        TransactionAdapter transactionAdapter;

        MonthViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMonth        = itemView.findViewById(R.id.tvMonth);
            tvDebit        = itemView.findViewById(R.id.tvDebit);
            tvCredit       = itemView.findViewById(R.id.tvCredit);
            rvTransactions = itemView.findViewById(R.id.rvTransactions);

            // Nested list
            rvTransactions.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext()));
            transactionAdapter = new TransactionAdapter();
            rvTransactions.setAdapter(transactionAdapter);
        }

        void bind(MonthSummary summary) {
            tvMonth.setText(summary.monthYear);
            tvDebit.setText(String.format(
                    Locale.getDefault(), "Debit: ₹%.2f", summary.totalDebit));
            tvCredit.setText(String.format(
                    Locale.getDefault(), "Credit: ₹%.2f", summary.totalCredit));

            transactionAdapter.updateData(summary.transactions);
        }
    }
}
