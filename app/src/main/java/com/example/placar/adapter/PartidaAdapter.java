package com.example.placar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.R;
import com.example.placar.model.Partida;

import java.util.List;

public class PartidaAdapter extends RecyclerView.Adapter<PartidaAdapter.PartidaViewHolder> {

    private List<Partida> listaPartidas;
    private OnItemClickListener listener;

    // 1. Criamos a interface para ouvir o clique (POO na prática!)
    public interface OnItemClickListener {
        void onItemClick(Partida partida);
    }

    // 2. Atualizamos o construtor para receber o ouvinte
    public PartidaAdapter(List<Partida> listaPartidas, OnItemClickListener listener) {
        this.listaPartidas = listaPartidas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PartidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partida, parent, false);
        return new PartidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidaViewHolder holder, int position) {
        Partida partida = listaPartidas.get(position);
        holder.textNomeJogo.setText(partida.nomeDoJogo);

        if (partida.emAndamento) {
            holder.textAoVivo.setVisibility(View.VISIBLE);
        } else {
            holder.textAoVivo.setVisibility(View.GONE);
        }

        // 3. Quando tocar no card, avisamos a MainActivity e passamos qual partida foi clicada
        holder.itemView.setOnClickListener(v -> listener.onItemClick(partida));
    }

    @Override
    public int getItemCount() {
        return listaPartidas.size();
    }

    static class PartidaViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeJogo, textAoVivo;

        public PartidaViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeJogo = itemView.findViewById(R.id.textNomeJogo);
            textAoVivo = itemView.findViewById(R.id.textAoVivo);
        }
    }
}