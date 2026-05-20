package com.example.placar.model;

public class ResultadoFinal {
    public String nomeJogo;
    public String vencedor;
    public String detalhesPontuacao;

    public ResultadoFinal(String nomeJogo, String vencedor, String detalhesPontuacao) {
        this.nomeJogo = nomeJogo;
        this.vencedor = vencedor;
        this.detalhesPontuacao = detalhesPontuacao;
    }
}