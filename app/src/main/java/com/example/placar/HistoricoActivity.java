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
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recyclerHistorico;
    private TextView textTotalFinalizadas, textVoltarHistorico;

    private HistoricoAdapter adapter;
    private List<ResultadoFinal> listaResultados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        textVoltarHistorico = findViewById(R.id.textVoltarHistorico);
        textTotalFinalizadas = findViewById(R.id.textTotalFinalizadas);
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

            for (Partida p : todasPartidas) {
                if (!p.emAndamento) { // Se for falsa (0), está finalizada
                    finalizadasCount++;
                    List<JogadorPartida> jogadores = db.placarDao().buscarJogadoresDaPartida(p.id);

                    String vencedor = "Empate";
                    int maiorPontuacao = -1;
                    StringBuilder detalhes = new StringBuilder();

                    // Descobre o vencedor e monta o texto "Nome — 10pts"
                    for (JogadorPartida j : jogadores) {
                        detalhes.append(j.nome).append(" — ").append(j.pontuacao).append("pts\n");
                        if (j.pontuacao > maiorPontuacao) {
                            maiorPontuacao = j.pontuacao;
                            vencedor = j.nome;
                        }
                    }

                    resultadosProcessados.add(new ResultadoFinal(p.nomeDoJogo, vencedor, detalhes.toString().trim()));
                }
            }

            final int totalFin = finalizadasCount;
            runOnUiThread(() -> {
                textTotalFinalizadas.setText(String.valueOf(totalFin));
                listaResultados.clear();
                listaResultados.addAll(resultadosProcessados);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
}
