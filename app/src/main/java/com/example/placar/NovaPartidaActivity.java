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

    // Variáveis para a lista na tela
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

    // Cria um Pop-up nativo do Android para digitar o nome
    private void abrirDialogNovoJogador() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo Jogador");

        // Cria um campo de texto dentro do pop-up
        final EditText input = new EditText(this);
        input.setHint("Ex: Maria");
        builder.setView(input);

        // Botão de confirmar
        builder.setPositiveButton("Adicionar", (dialog, which) -> {
            String nome = input.getText().toString().trim();
            if (!nome.isEmpty()) {
                nomesJogadores.add(nome);
                adapter.notifyItemInserted(nomesJogadores.size() - 1); // Avisa a lista para atualizar
            }
        });

        // Botão de cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void salvarNoBanco() {
        String nomeJogo = editNomeJogo.getText().toString().trim();

        // Validações básicas
        if (nomeJogo.isEmpty()) {
            Toast.makeText(this, "Digite o nome do jogo!", Toast.Toast.LENGTH_SHORT).show();
            return;
        }

        if (nomesJogadores.isEmpty()) {
            Toast.makeText(this, "Adicione pelo menos um jogador!", Toast.Toast.LENGTH_SHORT).show();
            return;
        }

        // Operação CREATE no banco (em Thread separada)
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Passo A: Salva a Partida e recupera o ID gerado pelo banco
            Partida novaPartida = new Partida(nomeJogo, true, System.currentTimeMillis());
            long idPartidaGerado = db.placarDao().inserirPartida(novaPartida);

            // Passo B: Salva cada jogador associando-o ao ID da partida criada
            for (String nomeJogador : nomesJogadores) {
                JogadorPartida jogador = new JogadorPartida((int) idPartidaGerado, nomeJogador, 0);
                db.placarDao().inserirJogador(jogador);
            }

            // Volta para a tela principal
            runOnUiThread(() -> {
                Toast.makeText(this, "Partida criada com sucesso!", Toast.Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}