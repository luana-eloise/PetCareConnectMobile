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

/**
 * Adapter do RecyclerView para listagem de consultas do Tutor.
 */
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
        if (c == null) return;

        holder.tvPet.setText("🐾 " + (c.getPetNome() != null ? c.getPetNome() : "Pet"));
        holder.tvDataHora.setText("📅 " + c.getDataHoraFormatada());
        
        String vetNome = c.getVeterinarioNome() != null && !c.getVeterinarioNome().trim().isEmpty() 
                ? c.getVeterinarioNome() 
                : "Clínica PetCare";
        holder.tvVet.setText("Veterinário: " + vetNome);
        
        holder.tvObs.setText(c.getObservacoes() != null && !c.getObservacoes().trim().isEmpty() 
                ? c.getObservacoes() 
                : "Sem observações adicionais.");
        holder.tvStatus.setText(c.getStatus() != null ? c.getStatus() : Consulta.STATUS_MARCADA);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onConsultaClick(c);
        });

        // Configuração visual do status usando métodos de modelo
        if (c.isMarcada()) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_dark));
            holder.btnCancelar.setVisibility(View.VISIBLE);
        } else if (c.isRealizada()) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_yellow);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.navy_primary));
            holder.btnCancelar.setVisibility(View.GONE);
        } else {
            // Cancelada ou Não Compareceu
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_red);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_red));
            holder.btnCancelar.setVisibility(View.GONE);
        }

        holder.btnCancelar.setOnClickListener(v -> {
            if (listener != null) listener.onCancelar(c);
        });
    }

    @Override
    public int getItemCount() {
        return consultas != null ? consultas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPet, tvDataHora, tvVet, tvObs, tvStatus;
        final Button btnCancelar;

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
