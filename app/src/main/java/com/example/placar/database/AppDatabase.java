package com.example.placar.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.placar.dao.PlacarDao;
import com.example.placar.model.JogadorPartida;
import com.example.placar.model.Partida;

// Aqui listamos todas as tabelas que pertencem a este banco e a versão dele
@Database(entities = {Partida.class, JogadorPartida.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // O Room vai gerar o código do DAO automaticamente a partir daqui
    public abstract PlacarDao placarDao();

    // Instância única do banco de dados (Padrão Singleton)
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "banco_placar")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}