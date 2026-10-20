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
        holder.tvTitulo.setText(a.getTitulo());
        holder.tvTexto.setText(a.getTexto());

        if (a.isLido()) {
            holder.tvStatus.setText("Lido");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_dark));
            holder.btnLido.setText("Marcar como não lido");
            holder.btnLido.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_muted));
            holder.btnLido.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setText("Novo");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_yellow);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_yellow));
            holder.btnLido.setText("Marcar como lido");
            holder.btnLido.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_primary));
            holder.btnLido.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> listener.onAvisoClick(a));
        holder.btnLido.setOnClickListener(v -> listener.onToggleLido(a));
    }

    @Override
    public int getItemCount() {
        return avisos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvTexto, tvStatus;
        Button btnLido;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvAvisoTitulo);
            tvTexto = itemView.findViewById(R.id.tvAvisoTexto);
            tvStatus = itemView.findViewById(R.id.tvAvisoStatus);
            btnLido = itemView.findViewById(R.id.btnMarcarLido);
        }
    }
}
