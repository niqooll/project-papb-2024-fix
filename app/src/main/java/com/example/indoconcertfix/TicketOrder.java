package com.example.indoconcertfix;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ticket_orders")
public class TicketOrder {
    @PrimaryKey (autoGenerate = true)
    private int id;

    private String judul;
    private String lokasi;
    private String harga;
    private int quantity;
    private int totalHarga;
    private String imageBase64;

    public String getImageBase64() {
        return imageBase64;
    }
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public TicketOrder(String judul, String lokasi, String harga, int quantity, int totalHarga) {
        this.judul = judul;
        this.lokasi = lokasi;
        this.harga = harga;
        this.quantity = quantity;
        this.totalHarga = totalHarga;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getJudul() { return judul; }
    public String getLokasi() { return lokasi; }
    public String getHarga() { return harga; }
    public int getQuantity() { return quantity; }
    public int getTotalHarga() { return totalHarga; }
}
