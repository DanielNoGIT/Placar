package com.example.placar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.R;

import java.util.List;

public class JogadorSetupAdapter extends RecyclerView.Adapter<JogadorSetupAdapter.ViewHolder> {

    private List<String> nomesJogadores;

    // Construtor que recebe a nossa lista temporária de nomes
    public JogadorSetupAdapter(List<String> nomesJogadores) {
        this.nomesJogadores = nomesJogadores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jogador_setup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nome = nomesJogadores.get(position);
        holder.textNomeJogadorSetup.setText(nome);

        // Lógica para remover o jogador da lista caso o usuário clique no "X"
        holder.btnRemoverJogador.setOnClickListener(v -> {
            nomesJogadores.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, nomesJogadores.size());
        });
    }

    @Override
    public int getItemCount() {
        return nomesJogadores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeJogadorSetup, btnRemoverJogador;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeJogadorSetup = itemView.findViewById(R.id.textNomeJogadorSetup);
            btnRemoverJogador = itemView.findViewById(R.id.btnRemoverJogador);
        }
    }
}