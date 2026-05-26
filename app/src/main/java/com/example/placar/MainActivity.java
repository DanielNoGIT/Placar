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

            // CORREÇÃO: Usando getId() e getNomeDoJogo() para respeitar o encapsulamento POO
            intent.putExtra("PARTIDA_ID", partida.getId());
            intent.putExtra("NOME_JOGO", partida.getNomeDoJogo());

            startActivity(intent);
        });

        recyclerViewJogosAndamento.setAdapter(adapter);

        // 3. Linkando os textos de estatísticas
        textTotalPartidas = findViewById(R.id.textTotalPartidas);
        textPartidasAndamento = findViewById(R.id.textPartidasAndamento);

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

    private void carregarPartidasDoBanco() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // Busca a lista de todas as partidas no banco
            List<Partida> partidasDoBanco = db.placarDao().buscarTodasPartidas();

            // Separa apenas os jogos que ainda estão rolando para mostrar na lista principal
            List<Partida> partidasEmAndamento = new ArrayList<>();
            for (Partida p : partidasDoBanco) {
                // CORREÇÃO: Usando isEmAndamento() em vez do acesso direto
                if (p.isEmAndamento()) {
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
            // CORREÇÃO: Usando isEmAndamento() para a contagem correta das estatísticas
            if (p.isEmAndamento()) {
                emAndamento++;
            }
        }

        textTotalPartidas.setText(String.valueOf(total));
        textPartidasAndamento.setText(String.valueOf(emAndamento));
    }
}