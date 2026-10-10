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
import com.unip.petcareconnect.model.Consulta;

import java.util.List;

public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.ViewHolder> {
    private final List<Consulta> consultas;
    private final OnConsultaActionListener listener;

    public interface OnConsultaActionListener {
        void onConsultaClick(Consulta consulta);
        void onCancelar(Consulta consulta);
    }

    public ConsultaAdapter(List<Consulta> consultas, OnConsultaActionListener listener) {
        this.consultas = consultas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consulta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Consulta c = consultas.get(position);
        holder.tvPet.setText("🐾 " + c.getPetNome());
        holder.tvDataHora.setText("📅 " + c.getData() + " às " + c.getHora());
        holder.tvVet.setText("Veterinário: " + (c.getVetNome() != null ? c.getVetNome() : "Dr. Carlos Mendes"));
        holder.tvObs.setText(c.getObservacoes() != null && !c.getObservacoes().isEmpty() ? c.getObservacoes() : "Sem observações adicionais.");
        holder.tvStatus.setText(c.getStatus());

        holder.itemView.setOnClickListener(v -> listener.onConsultaClick(c));

        if ("Marcada".equalsIgnoreCase(c.getStatus()) || "Agendada".equalsIgnoreCase(c.getStatus()) || "Confirmada".equalsIgnoreCase(c.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_dark));
            holder.btnCancelar.setVisibility(View.VISIBLE);
        } else if ("Realizada".equalsIgnoreCase(c.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_yellow);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.navy_primary));
            holder.btnCancelar.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_red);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_red));
            holder.btnCancelar.setVisibility(View.GONE);
        }

        holder.btnCancelar.setOnClickListener(v -> listener.onCancelar(c));
    }

    @Override
    public int getItemCount() {
        return consultas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPet, tvDataHora, tvVet, tvObs, tvStatus;
        Button btnCancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPet = itemView.findViewById(R.id.tvConsultaPet);
            tvDataHora = itemView.findViewById(R.id.tvConsultaDataHora);
            tvVet = itemView.findViewById(R.id.tvConsultaVet);
            tvObs = itemView.findViewById(R.id.tvConsultaObs);
            tvStatus = itemView.findViewById(R.id.tvConsultaStatus);
            btnCancelar = itemView.findViewById(R.id.btnCancelarConsulta);
        }
    }
}
