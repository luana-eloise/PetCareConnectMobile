package com.unip.petcareconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.Aviso;

import java.util.List;

/**
 * Adapter do RecyclerView para listagem de avisos e notificações do Tutor.
 */
public class AvisoAdapter extends RecyclerView.Adapter<AvisoAdapter.ViewHolder> {

    private final List<Aviso> avisos;
    private final OnAvisoActionListener listener;

    public interface OnAvisoActionListener {
        void onToggleLido(Aviso aviso);
        void onAvisoClick(Aviso aviso);
    }

    public AvisoAdapter(List<Aviso> avisos, OnAvisoActionListener listener) {
        this.avisos = avisos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aviso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Aviso a = avisos.get(position);
        if (a == null) return;

        holder.tvTitulo.setText(a.getTitulo() != null ? a.getTitulo() : "Aviso");
        holder.tvTexto.setText(a.getTexto() != null ? a.getTexto() : "");

        if (a.isLido()) {
            holder.tvStatus.setText("Lido");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_dark));
            holder.btnLido.setText("Marcar como não lido");
            holder.btnLido.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_muted));
        } else {
            holder.tvStatus.setText("Novo");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_yellow);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_yellow));
            holder.btnLido.setText("Marcar como lido");
            holder.btnLido.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAvisoClick(a);
        });
        holder.btnLido.setOnClickListener(v -> {
            if (listener != null) listener.onToggleLido(a);
        });
    }

    @Override
    public int getItemCount() {
        return avisos != null ? avisos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitulo, tvTexto, tvStatus;
        final Button btnLido;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvAvisoTitulo);
            tvTexto = itemView.findViewById(R.id.tvAvisoTexto);
            tvStatus = itemView.findViewById(R.id.tvAvisoStatus);
            btnLido = itemView.findViewById(R.id.btnMarcarLido);
        }
    }
}
