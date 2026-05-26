package com.example.placar.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_partidas")
public class Partida {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nomeDoJogo;
    private boolean emAndamento;
    private long dataCriacao;

    // Construtor Vazio exigido pelo Room
    public Partida() {
    }

    // Construtor completo para usarmos na hora de criar um novo jogo
    public Partida(String nomeDoJogo, boolean emAndamento, long dataCriacao) {
        this.nomeDoJogo = nomeDoJogo;
        this.emAndamento = emAndamento;
        this.dataCriacao = dataCriacao;
    }

    // ========== MÉTODOS GETTERS E SETTERS (Encapsulamento) ==========

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeDoJogo() {
        return nomeDoJogo;
    }

    public void setNomeDoJogo(String nomeDoJogo) {
        this.nomeDoJogo = nomeDoJogo;
    }

    public boolean isEmAndamento() {
        return emAndamento;
    }

    public void setEmAndamento(boolean emAndamento) {
        this.emAndamento = emAndamento;
    }

    public long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}