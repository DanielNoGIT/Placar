package com.example.placar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.adapter.JogadorPlacarAdapter;
import com.example.placar.database.AppDatabase;
import com.example.placar.model.JogadorPartida;
import com.example.placar.model.Partida;

import java.util.ArrayList;
import java.util.List;

public class PlacarActivity extends AppCompatActivity {

    private int partidaId;
    private TextView textNomeJogoHeader;
    private RecyclerView recyclerJogadoresPlacar;
    private Button btnEncerrarPartida;

    private JogadorPlacarAdapter adapter;
    private List<JogadorPartida> listaJogadores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placar);

        // 1. Recebe os dados da Intent
        partidaId = getIntent().getIntExtra("PARTIDA_ID", -1);
        String nomeDoJogo = getIntent().getStringExtra("NOME_JOGO");

        // ATENÇÃO: Primeiro usamos o findViewById para conectar as variáveis ao XML...
        textNomeJogoHeader = findViewById(R.id.textNomeJogoHeader);
        recyclerJogadoresPlacar = findViewById(R.id.recyclerJogadoresPlacar);
        btnEncerrarPartida = findViewById(R.id.btnEncerrarPartida);

        // ...Para SÓ DEPOIS usar o setText e alterar o texto!
        textNomeJogoHeader.setText("←  " + nomeDoJogo);

        // 2. Configura a lista e a interface de atualização de pontos
        recyclerJogadoresPlacar.setLayoutManager(new LinearLayoutManager(this));

        adapter = new JogadorPlacarAdapter(listaJogadores, jogadorAtualizado -> {
            // Isso roda toda vez que clicar no + ou - (Operação UPDATE)
            new Thread(() -> {
                AppDatabase.getInstance(this).placarDao().atualizarJogador(jogadorAtualizado);
            }).start();
        });

        recyclerJogadoresPlacar.setAdapter(adapter);

        // 3. Botões
        textNomeJogoHeader.setOnClickListener(v -> finish());

        // ATENÇÃO: Agora com apenas um setOnClickListener!
        btnEncerrarPartida.setOnClickListener(v -> {
            // Roda a atualização no banco em segundo plano
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                db.placarDao().encerrarPartida(partidaId); // Muda o status para 0 (Finalizada)

                // Volta pra tela principal e avisa o usuário
                runOnUiThread(() -> {
                    Toast.makeText(this, "Partida Encerrada com Sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });

        // 4. Carrega os dados
        if (partidaId != -1) {
            carregarDadosDoBanco();
        }
    }

    private void carregarDadosDoBanco() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // Busca apenas os jogadores DESSA partida específica
            List<JogadorPartida> dadosBanco = db.placarDao().buscarJogadoresDaPartida(partidaId);

            runOnUiThread(() -> {
                listaJogadores.clear();
                listaJogadores.addAll(dadosBanco);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
}