package com.example.placar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.adapter.JogadorSetupAdapter;
import com.example.placar.database.AppDatabase;
import com.example.placar.model.JogadorPartida;
import com.example.placar.model.Partida;

import java.util.ArrayList;
import java.util.List;

public class NovaPartidaActivity extends AppCompatActivity {

    private EditText editNomeJogo;
    private Button btnIniciarPartida, btnAdicionarJogador;
    private TextView textVoltar;
    private RecyclerView recyclerJogadores;

    private JogadorSetupAdapter adapter;
    private List<String> nomesJogadores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_partida);

        // 1. Vinculando IDs
        editNomeJogo = findViewById(R.id.editNomeJogo);
        btnIniciarPartida = findViewById(R.id.btnIniciarPartida);
        btnAdicionarJogador = findViewById(R.id.btnAdicionarJogador);
        textVoltar = findViewById(R.id.textVoltar);
        recyclerJogadores = findViewById(R.id.recyclerJogadoresNovaPartida);

        // 2. Configurando o RecyclerView da lista de jogadores
        recyclerJogadores.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JogadorSetupAdapter(nomesJogadores);
        recyclerJogadores.setAdapter(adapter);

        // 3. Ações dos botões
        textVoltar.setOnClickListener(v -> finish());

        btnAdicionarJogador.setOnClickListener(v -> abrirDialogNovoJogador());

        btnIniciarPartida.setOnClickListener(v -> salvarNoBanco());
    }

    private void abrirDialogNovoJogador() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo Jogador");

        final EditText input = new EditText(this);
        input.setHint("Ex: Maria");
        builder.setView(input);

        builder.setPositiveButton("Adicionar", (dialog, which) -> {
            String nome = input.getText().toString().trim();
            if (!nome.isEmpty()) {
                nomesJogadores.add(nome);
                adapter.notifyItemInserted(nomesJogadores.size() - 1);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void salvarNoBanco() {
        String nomeJogo = editNomeJogo.getText().toString().trim();

        // CORREÇÃO: Removido o "Toast.Toast" duplicado das validações
        if (nomeJogo.isEmpty()) {
            Toast.makeText(this, "Digite o nome do jogo!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nomesJogadores.isEmpty()) {
            Toast.makeText(this, "Adicione pelo menos um jogador!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // CORREÇÃO: Agora o construtor da Partida existe e bate perfeitamente com o modelo!
            Partida novaPartida = new Partida(nomeJogo, true, System.currentTimeMillis());
            long idPartidaGerado = db.placarDao().inserirPartida(novaPartida);

            for (String nomeJogador : nomesJogadores) {
                JogadorPartida jogador = new JogadorPartida((int) idPartidaGerado, nomeJogador, 0);
                db.placarDao().inserirJogador(jogador);
            }

            runOnUiThread(() -> {
                // CORREÇÃO: Ajustado para Toast.LENGTH_SHORT simples
                Toast.makeText(this, "Partida criada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}