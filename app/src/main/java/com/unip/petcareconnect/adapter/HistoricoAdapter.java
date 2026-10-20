package com.unip.petcareconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.Historico;

import java.util.List;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {
    private final List<Historico> historicos;

    public HistoricoAdapter(List<Historico> historicos) {
        this.historicos = historicos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historico, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Historico h = historicos.get(position);
        holder.tvData.setText("📅 " + h.getDataEvento());
        holder.tvDesc.setText(h.getDescricao());
    }

    @Override
    public int getItemCount() {
        return historicos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvData, tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tvHistData);
            tvDesc = itemView.findViewById(R.id.tvHistDesc);
        }
    }
}
