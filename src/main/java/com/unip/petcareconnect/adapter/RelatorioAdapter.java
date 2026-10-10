package com.unip.petcareconnect.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.Relatorio;

import java.util.List;

public class RelatorioAdapter extends RecyclerView.Adapter<RelatorioAdapter.ViewHolder> {
    private final List<Relatorio> relatorios;
    private final OnRelatorioActionListener listener;

    // Listener interface for item actions
    public interface OnRelatorioActionListener {
        void onRelatorioClick(Relatorio relatorio);
        void onExportarClick(Relatorio relatorio);
    }

    public RelatorioAdapter(List<Relatorio> relatorios, OnRelatorioActionListener listener) {
        this.relatorios = relatorios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_relatorio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Relatorio r = relatorios.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvTitulo.setText("Relatório Clínico — " + r.getPetNome());
        holder.tvTexto.setText(r.getTexto());
        holder.tvAtualizacao.setText("Última Atualização: " + (r.getAtualizacao() != null ? r.getAtualizacao() : "Recente"));
        holder.tvStatus.setText(r.getStatus() != null ? r.getStatus() : "Disponivel");

        // Click on whole item to view details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRelatorioClick(r);
            }
        });

        // Export button action
        holder.btnExportar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExportarClick(r);
            }
        });
    }

    @Override
    public int getItemCount() {
        return relatorios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvTexto, tvAtualizacao, tvStatus;
        Button btnExportar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvRelatorioTitulo);
            tvTexto = itemView.findViewById(R.id.tvRelatorioTexto);
            tvAtualizacao = itemView.findViewById(R.id.tvRelatorioAtualizacao);
            tvStatus = itemView.findViewById(R.id.tvRelatorioStatus);
            btnExportar = itemView.findViewById(R.id.btnExportarRelatorio);
        }
    }
}
