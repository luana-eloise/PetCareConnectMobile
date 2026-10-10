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
        holder.tvNome.setText(pet.getNome());
        holder.tvIdadeBadge.setText(pet.getIdade() + " ano(s)");
        holder.tvTipoRaca.setText(pet.getTipo() + " • Raça: " + (pet.getRaca() != null ? pet.getRaca() : "SRD"));
        holder.tvPeso.setText("Peso: " + pet.getPeso() + " kg");

        holder.itemView.setOnClickListener(v -> listener.onPetClick(pet));
        holder.btnEditar.setOnClickListener(v -> listener.onEdit(pet));
        holder.btnExcluir.setOnClickListener(v -> listener.onDelete(pet));
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvIdadeBadge, tvTipoRaca, tvPeso;
        Button btnEditar, btnExcluir;

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
