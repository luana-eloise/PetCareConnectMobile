package com.unip.petcareconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.Vacina;

import java.util.List;

/**
 * Adapter do RecyclerView para exibição da carteira de vacinas do Tutor.
 */
public class VacinaAdapter extends RecyclerView.Adapter<VacinaAdapter.ViewHolder> {

    private final List<Vacina> vacinas;
    private final OnVacinaClickListener listener;

    public interface OnVacinaClickListener {
        void onVacinaClick(Vacina vacina);
    }

    public VacinaAdapter(List<Vacina> vacinas, OnVacinaClickListener listener) {
        this.vacinas = vacinas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vacina, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vacina v = vacinas.get(position);
        if (v == null) return;

        holder.tvNome.setText("💉 " + (v.getNome() != null ? v.getNome() : "Vacina"));
        String petNome = v.getPetNome();
        holder.tvPet.setText("🐾 Pet: " + (petNome != null && !petNome.trim().isEmpty() ? petNome : "—"));
        holder.tvData.setText("Data de Aplicação: " + (v.getDataAplicacao() != null ? v.getDataAplicacao() : "Pendente"));
        
        String obs = v.getObservacao() != null && !v.getObservacao().trim().isEmpty()
                ? "Obs: " + v.getObservacao()
                : "Sem observações.";
        holder.tvObs.setText(obs);

        if (v.isAplicada()) {
            holder.tvStatus.setText("Aplicada");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_dark));
        } else {
            holder.tvStatus.setText("Pendente");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_yellow);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_yellow));
        }

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onVacinaClick(v);
        });
    }

    @Override
    public int getItemCount() {
        return vacinas != null ? vacinas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNome, tvStatus, tvData, tvObs, tvPet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvVacinaNome);
            tvStatus = itemView.findViewById(R.id.tvVacinaStatus);
            tvPet = itemView.findViewById(R.id.tvVacinaPet);
            tvData = itemView.findViewById(R.id.tvVacinaData);
            tvObs = itemView.findViewById(R.id.tvVacinaObs);
        }
    }
}
