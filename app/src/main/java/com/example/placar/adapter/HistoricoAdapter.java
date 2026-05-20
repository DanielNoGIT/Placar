package com.example.placar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placar.R;
import com.example.placar.model.ResultadoFinal;

import java.util.List;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {

    private List<ResultadoFinal> resultados;

    public HistoricoAdapter(List<ResultadoFinal> resultados) {
        this.resultados = resultados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historico, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultadoFinal resultado = resultados.get(position);

        holder.textNomeJogoHistorico.setText(resultado.nomeJogo);
        holder.textVencedor.setText("🏆 Vencedor: " + resultado.vencedor);
        holder.textResultados.setText(resultado.detalhesPontuacao);
    }

    @Override
    public int getItemCount() {
        return resultados.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeJogoHistorico, textVencedor, textResultados;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeJogoHistorico = itemView.findViewById(R.id.textNomeJogoHistorico);
            textVencedor = itemView.findViewById(R.id.textVencedor);
            textResultados = itemView.findViewById(R.id.textResultados);
        }
    }
}