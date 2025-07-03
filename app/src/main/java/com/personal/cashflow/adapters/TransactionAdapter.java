package com.personal.cashflow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.cashflow.R;
import com.personal.cashflow.room.TransactionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<TransactionModel> transactions = new ArrayList<>();

    // Call from MonthViewHolder.bind()
    public void updateData(List<TransactionModel> newData) {
        transactions.clear();
        if (newData != null) transactions.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TransactionViewHolder holder, int position) {
        holder.bind(transactions.get(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    // ---------- ViewHolder ----------
    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tvAmount, tvType, tvMerchant;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount   = itemView.findViewById(R.id.tvAmount);
            tvType     = itemView.findViewById(R.id.tvType);
            tvMerchant = itemView.findViewById(R.id.tvMerchant);
        }

        void bind(TransactionModel txn) {
            tvAmount.setText(String.format(
                    Locale.getDefault(), "₹%.2f", txn.amount));
            tvType.setText(txn.type);
            tvMerchant.setText(txn.merchant != null ? txn.merchant : "-");

            // Color‑code debit vs credit
            int color = txn.type.equalsIgnoreCase("debited")
                    ? android.R.color.holo_red_dark
                    : android.R.color.holo_green_dark;

            tvAmount.setTextColor(
                    ContextCompat.getColor(itemView.getContext(), color));
            tvType.setTextColor(
                    ContextCompat.getColor(itemView.getContext(), color));
        }
    }
}

