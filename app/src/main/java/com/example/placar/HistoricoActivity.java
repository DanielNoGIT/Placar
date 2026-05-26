package com.example.placar;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.adapter.HistoricoAdapter;
import com.example.placar.database.AppDatabase;
import com.example.placar.model.JogadorPartida;
import com.example.placar.model.Partida;
import com.example.placar.model.ResultadoFinal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recyclerHistorico;
    // CORREÇÃO: Adicionado o textMaisVitorias para sumir com o nome estático
    private TextView textTotalFinalizadas, textVoltarHistorico, textMaisVitorias;

    private HistoricoAdapter adapter;
    private List<ResultadoFinal> listaResultados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        textVoltarHistorico = findViewById(R.id.textVoltarHistorico);
        textTotalFinalizadas = findViewById(R.id.textTotalFinalizadas);
        // VINCULAÇÃO DO ID: Certifique-se de que no seu XML o ID é exatamente este!
        textMaisVitorias = findViewById(R.id.textMaiorVencedor);
        recyclerHistorico = findViewById(R.id.recyclerHistorico);

        textVoltarHistorico.setOnClickListener(v -> finish());

        recyclerHistorico.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoricoAdapter(listaResultados);
        recyclerHistorico.setAdapter(adapter);

        carregarHistorico();
    }

    private void carregarHistorico() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Partida> todasPartidas = db.placarDao().buscarTodasPartidas();
            List<ResultadoFinal> resultadosProcessados = new ArrayList<>();
            int finalizadasCount = 0;

            // Estrutura de dados para computar a quantidade de vitórias de cada jogador
            HashMap<String, Integer> contagemVitorias = new HashMap<>();

            for (Partida p : todasPartidas) {
                if (!p.isEmAndamento()) {
                    finalizadasCount++;

                    List<JogadorPartida> jogadores = db.placarDao().buscarJogadoresDaPartida(p.getId());

                    String vencedor = "Empate";
                    int maiorPontuacao = -1;
                    boolean houveEmpate = false;
                    StringBuilder detalhes = new StringBuilder();

                    // Descobre o vencedor real da partida
                    for (JogadorPartida j : jogadores) {
                        detalhes.append(j.nome).append(" — ").append(j.pontuacao).append("pts\n");
                        if (j.pontuacao > maiorPontuacao) {
                            maiorPontuacao = j.pontuacao;
                            vencedor = j.nome;
                            houveEmpate = false; // Novo maior ponto, limpa o empate anterior
                        } else if (j.pontuacao == maiorPontuacao && maiorPontuacao != -1) {
                            houveEmpate = true; // Pontuação igual, sinaliza empate técnico
                        }
                    }

                    if (houveEmpate) {
                        vencedor = "Empate";
                    }

                    // Se houve um vencedor de fato, soma 1 ponto de vitória para ele no ranking geral
                    if (!vencedor.equals("Empate")) {
                        int vitoriasAtuais = contagemVitorias.containsKey(vencedor) ? contagemVitorias.get(vencedor) : 0;
                        contagemVitorias.put(vencedor, vitoriasAtuais + 1);
                    }

                    resultadosProcessados.add(new ResultadoFinal(p.getNomeDoJogo(), vencedor, detalhes.toString().trim()));
                }
            }

            // Descobre qual chave do HashMap possui o maior valor de vitórias acumuladas
            String jogadorCampeao = "Nenhum";
            int maxVitorias = 0;
            for (Map.Entry<String, Integer> entry : contagemVitorias.entrySet()) {
                if (entry.getValue() > maxVitorias) {
                    maxVitorias = entry.getValue();
                    jogadorCampeao = entry.getKey();
                }
            }

            final int totalFin = finalizadasCount;
            final String nomeCampeaoGeral = jogadorCampeao;

            runOnUiThread(() -> {
                textTotalFinalizadas.setText(String.valueOf(totalFin));

                // Exibe dinamicamente na UI o nome do verdadeiro maior vencedor
                if (textMaisVitorias != null) {
                    textMaisVitorias.setText(nomeCampeaoGeral);
                }

                listaResultados.clear();
                listaResultados.addAll(resultadosProcessados);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
}