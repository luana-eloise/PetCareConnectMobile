package com.unip.petcareconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.Relatorio;

import java.util.List;

/**
 * Adapter do RecyclerView para exibição de relatórios e laudos clínicos do Tutor.
 */
public class RelatorioAdapter extends RecyclerView.Adapter<RelatorioAdapter.ViewHolder> {

    private final List<Relatorio> relatorios;
    private final OnRelatorioActionListener listener;

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
        if (r == null) return;

        String petNome = r.getPetNome() != null ? r.getPetNome() : "Pet";
        holder.tvTitulo.setText(r.getTitulo() != null ? r.getTitulo() : "Relatório Clínico — " + petNome);
        holder.tvTexto.setText(r.getTexto() != null ? r.getTexto() : "");
        holder.tvAtualizacao.setText("Data: " + (r.getDataEmissao() != null ? r.getDataEmissao() : "Recente"));
        holder.tvStatus.setText(r.getStatus() != null ? r.getStatus() : Relatorio.STATUS_DISPONIVEL);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRelatorioClick(r);
        });

        holder.btnExportar.setOnClickListener(v -> {
            if (listener != null) listener.onExportarClick(r);
        });
    }

    @Override
    public int getItemCount() {
        return relatorios != null ? relatorios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitulo, tvTexto, tvAtualizacao, tvStatus;
        final Button btnExportar;

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
