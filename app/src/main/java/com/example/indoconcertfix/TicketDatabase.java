package com.example.indoconcertfix;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {TicketOrder.class}, version = 2) // Tingkatkan versi
public abstract class TicketDatabase extends RoomDatabase {
    public abstract TicketDao ticketOrderDao();

    private static volatile TicketDatabase INSTANCE;

    public static TicketDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TicketDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TicketDatabase.class, "ticket_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Definisikan migrasi dari versi 1 ke versi 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ticket_orders ADD COLUMN imageBase64 TEXT");
        }
    };
}
