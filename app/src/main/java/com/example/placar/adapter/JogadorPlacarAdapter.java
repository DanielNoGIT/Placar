package com.example.placar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.R;
import com.example.placar.model.JogadorPartida;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class JogadorPlacarAdapter extends RecyclerView.Adapter<JogadorPlacarAdapter.ViewHolder> {

    private List<JogadorPartida> jogadores;
    private OnScoreChangeListener listener;

    // Interface (POO) para avisar a tela principal que a pontuação mudou
    public interface OnScoreChangeListener {
        void onScoreChange(JogadorPartida jogador);
    }

    public JogadorPlacarAdapter(List<JogadorPartida> jogadores, OnScoreChangeListener listener) {
        this.jogadores = jogadores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jogador_placar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JogadorPartida jogador = jogadores.get(position);

        holder.textNomeJogador.setText(jogador.nome);
        holder.textPontos.setText(String.valueOf(jogador.pontuacao));

        // Pega a primeira letra do nome para o ícone
        if (jogador.nome != null && !jogador.nome.isEmpty()) {
            holder.textInicial.setText(jogador.nome.substring(0, 1).toUpperCase());
        }

        // Lógica do Botão +
        holder.btnMais.setOnClickListener(v -> {
            jogador.pontuacao++;
            notifyItemChanged(position); // Atualiza só este retângulo na tela
            listener.onScoreChange(jogador); // Manda salvar no banco
        });

        // Lógica do Botão -
        holder.btnMenos.setOnClickListener(v -> {
            if (jogador.pontuacao > 0) { // Evita pontuação negativa, se desejar
                jogador.pontuacao--;
                notifyItemChanged(position);
                listener.onScoreChange(jogador);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jogadores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeJogador, textPontos, textInicial;
        FloatingActionButton btnMais, btnMenos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeJogador = itemView.findViewById(R.id.textNomeJogador);
            textPontos = itemView.findViewById(R.id.textPontos);
            textInicial = itemView.findViewById(R.id.textInicial);
            btnMais = itemView.findViewById(R.id.btnMais);
            btnMenos = itemView.findViewById(R.id.btnMenos);
        }
    }
}