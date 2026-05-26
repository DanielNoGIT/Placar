package com.example.placar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.R;
import com.example.placar.database.AppDatabase;
import com.example.placar.model.JogadorPartida;
import com.example.placar.model.Partida;

import java.util.List;

public class PartidaAdapter extends RecyclerView.Adapter<PartidaAdapter.PartidaViewHolder> {

    private List<Partida> listaPartidas;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Partida partida);
    }

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
        holder.textNomeJogo.setText(partida.getNomeDoJogo());

        if (partida.isEmAndamento()) {
            holder.textAoVivo.setVisibility(View.VISIBLE);
        } else {
            holder.textAoVivo.setVisibility(View.GONE);
        }

        // =======================================================================
        // CORREÇÃO DO BUG: Busca os jogadores reais no banco e joga nos TextViews
        // =======================================================================
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
            List<JogadorPartida> jogadores = db.placarDao().buscarJogadoresDaPartida(partida.getId());

            // Garante que existem pelo menos 2 jogadores para preencher o card de confronto
            if (jogadores != null && jogadores.size() >= 2) {
                // j.nome e j.pontuacao dependem de como estão escritas na classe JogadorPartida (ex: j.getNome() ou j.nome)
                String nomeJ1 = jogadores.get(0).nome;
                String pontosJ1 = String.valueOf(jogadores.get(0).pontuacao);

                String nomeJ2 = jogadores.get(1).nome;
                String pontosJ2 = String.valueOf(jogadores.get(1).pontuacao);

                // De volta à Thread principal para atualizar os textos na tela
                holder.itemView.post(() -> {
                    holder.textJogador1.setText(nomeJ1);
                    holder.textPontos1.setText(pontosJ1);
                    holder.textJogador2.setText(nomeJ2);
                    holder.textPontos2.setText(pontosJ2);
                });
            }
        }).start();

        holder.itemView.setOnClickListener(v -> listener.onItemClick(partida));
    }

    @Override
    public int getItemCount() {
        return listaPartidas.size();
    }

    // ========== VIEWHOLDER CORRIGIDO COM OS CAMPOS QUE FALTAVAM ==========
    static class PartidaViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeJogo, textAoVivo;
        TextView textJogador1, textJogador2, textPontos1, textPontos2;

        public PartidaViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeJogo = itemView.findViewById(R.id.textNomeJogo);
            textAoVivo = itemView.findViewById(R.id.textAoVivo);

            // ATENÇÃO: Verifique se esses IDs abaixo são EXATAMENTE os nomes que você usou no item_partida.xml
            textJogador1 = itemView.findViewById(R.id.textJogadorA);
            textPontos1 = itemView.findViewById(R.id.textPontuacaoA);
            textJogador2 = itemView.findViewById(R.id.textJogadorB);
            textPontos2 = itemView.findViewById(R.id.textPontuacaoB);
        }
    }
}