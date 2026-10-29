package com.unip.petcareconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.Pet;

import java.util.List;

/**
 * Adapter do RecyclerView para listagem de pets do Tutor.
 */
public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder> {

    private final List<Pet> pets;
    private final OnPetActionListener listener;

    public interface OnPetActionListener {
        void onPetClick(Pet pet);
        void onEdit(Pet pet);
        void onDelete(Pet pet);
    }

    public PetAdapter(List<Pet> pets, OnPetActionListener listener) {
        this.pets = pets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pet pet = pets.get(position);
        if (pet == null) return;

        holder.tvNome.setText(pet.getNome() != null ? pet.getNome() : "Sem nome");
        holder.tvIdadeBadge.setText(pet.getIdadeFormatada());
        
        String raca = (pet.getRaca() != null && !pet.getRaca().trim().isEmpty()) ? pet.getRaca() : "SRD";
        String especie = pet.getEspecie() != null ? pet.getEspecie() : "Pet";
        holder.tvTipoRaca.setText(especie + " • Raça: " + raca);
        holder.tvPeso.setText("Peso: " + pet.getPesoFormatado());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPetClick(pet);
        });
        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(pet);
        });
        holder.btnExcluir.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(pet);
        });
    }

    @Override
    public int getItemCount() {
        return pets != null ? pets.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNome, tvIdadeBadge, tvTipoRaca, tvPeso;
        final Button btnEditar, btnExcluir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvPetNome);
            tvIdadeBadge = itemView.findViewById(R.id.tvPetIdadeBadge);
            tvTipoRaca = itemView.findViewById(R.id.tvPetTipoRaca);
            tvPeso = itemView.findViewById(R.id.tvPetPeso);
            btnEditar = itemView.findViewById(R.id.btnEditarPet);
            btnExcluir = itemView.findViewById(R.id.btnExcluirPet);
        }
    }
}
