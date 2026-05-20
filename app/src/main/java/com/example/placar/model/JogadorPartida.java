package com.example.placar.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// Essa tabela guarda o jogador e se conecta com a tabela de partidas
@Entity(tableName = "tabela_jogadores",
        foreignKeys = @ForeignKey(entity = Partida.class,
                parentColumns = "id",
                childColumns = "partidaId",
                onDelete = ForeignKey.CASCADE))
public class JogadorPartida {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int partidaId; // ID da partida à qual este jogador pertence
    public String nome;
    public int pontuacao;

    // Construtor vazio
    public JogadorPartida() {
    }

    // Construtor principal
    public JogadorPartida(int partidaId, String nome, int pontuacao) {
        this.partidaId = partidaId;
        this.nome = nome;
        this.pontuacao = pontuacao;
    }
}