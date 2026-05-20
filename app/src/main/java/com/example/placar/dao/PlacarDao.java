package com.example.placar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.placar.model.JogadorPartida;
import com.example.placar.model.Partida;

import java.util.List;

@Dao
public interface PlacarDao {

    // --- CREATE (Inserir) ---
    @Insert
    long inserirPartida(Partida partida); // Retorna o ID gerado para a partida

    @Insert
    void inserirJogador(JogadorPartida jogador);

    // --- READ (Ler/Recuperar) ---
    @Query("SELECT * FROM tabela_partidas ORDER BY dataCriacao DESC")
    List<Partida> buscarTodasPartidas();

    @Query("SELECT * FROM tabela_jogadores WHERE partidaId = :partidaId")
    List<JogadorPartida> buscarJogadoresDaPartida(int partidaId);

    // --- UPDATE (Atualizar) ---
    @Update
    void atualizarJogador(JogadorPartida jogador); // Usaremos ao clicar no botão "+" ou "-"

    @Update
    void atualizarPartida(Partida partida); // Usaremos ao encerrar o jogo
    @Query("UPDATE tabela_partidas SET emAndamento = 0 WHERE id = :idDaPartida")
    void encerrarPartida(int idDaPartida);

    // --- DELETE (Excluir) ---
    @Delete
    void deletarPartida(Partida partida);
}