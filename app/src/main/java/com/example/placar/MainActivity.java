package com.example.placar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.adapter.PartidaAdapter;
import com.example.placar.database.AppDatabase;
import com.example.placar.model.Partida;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewJogosAndamento;
    private PartidaAdapter adapter;
    private List<Partida> listaPartidas = new ArrayList<>();

    // Variáveis para atualizar os números nos cards do topo
    private TextView textTotalPartidas, textPartidasAndamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Configurando o Botão Flutuante
        FloatingActionButton fabNovaPartida = findViewById(R.id.fabNovaPartida);
        fabNovaPartida.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NovaPartidaActivity.class);
            startActivity(intent);
        });

        // 2. Configurando o RecyclerView com o Adapter
        recyclerViewJogosAndamento = findViewById(R.id.recyclerViewJogosAndamento);
        // O LinearLayoutManager diz que a lista será na vertical, um item embaixo do outro
        recyclerViewJogosAndamento.setLayoutManager(new LinearLayoutManager(this));

        // Inicializando o Adapter e programando o que acontece ao clicar em uma partida
        adapter = new PartidaAdapter(listaPartidas, partida -> {
            Intent intent = new Intent(MainActivity.this, PlacarActivity.class);

            // Colocamos o ID e o Nome na "bagagem" da Intent para a próxima tela usar
            intent.putExtra("PARTIDA_ID", partida.id);
            intent.putExtra("NOME_JOGO", partida.nomeDoJogo);

            startActivity(intent);
        });

        recyclerViewJogosAndamento.setAdapter(adapter);

        // 3. Linkando os textos de estatísticas
        textTotalPartidas = findViewById(R.id.textTotalPartidas);
        textPartidasAndamento = findViewById(R.id.textPartidasAndamento);

        // =========================================================
        // MUDANÇA 1: Adicionado bem aqui, no final do onCreate!
        // =========================================================
        // Faz a área de estatísticas funcionar como botão para o Histórico
        findViewById(R.id.layoutStats).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoricoActivity.class));
        });
    }

    // O onResume é chamado toda vez que a tela volta a ficar visível
    @Override
    protected void onResume() {
        super.onResume();
        carregarPartidasDoBanco();
    }

    // =========================================================
    // MUDANÇA 2: O método inteiro carregarPartidasDoBanco foi
    // substituído para filtrar os jogos finalizados!
    // =========================================================
    private void carregarPartidasDoBanco() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // Busca a lista de todas as partidas no banco
            List<Partida> partidasDoBanco = db.placarDao().buscarTodasPartidas();

            // Separa apenas os jogos que ainda estão rolando para mostrar na lista principal
            List<Partida> partidasEmAndamento = new ArrayList<>();
            for (Partida p : partidasDoBanco) {
                if (p.emAndamento) {
                    partidasEmAndamento.add(p);
                }
            }

            // Voltamos à Thread Principal para atualizar a interface (UI)
            runOnUiThread(() -> {
                listaPartidas.clear();
                listaPartidas.addAll(partidasEmAndamento); // Agora alimenta só com as ativas!
                adapter.notifyDataSetChanged();

                atualizarEstatisticas(partidasDoBanco); // Manda todas para calcular os totais
            });
        }).start();
    }

    private void atualizarEstatisticas(List<Partida> partidas) {
        int total = partidas.size();
        int emAndamento = 0;

        for (Partida p : partidas) {
            if (p.emAndamento) {
                emAndamento++;
            }
        }

        textTotalPartidas.setText(String.valueOf(total));
        textPartidasAndamento.setText(String.valueOf(emAndamento));
    }
}