package com.example.indoconcertfix;

public class TicketOrder {
    private String id;
    private String judul;
    private String lokasi;
    private String harga;
    private int jumlah;
    private String encodedImage;
    private int totalHarga;

    public TicketOrder() {

    }

    public TicketOrder(String judul, String lokasi, String harga, int jumlah, String encodedImage, int totalHarga) {
        this.judul = judul;
        this.lokasi = lokasi;
        this.harga = harga;
        this.jumlah = jumlah;
        this.encodedImage = encodedImage;
        this.totalHarga = totalHarga;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public int getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(int totalHarga) {
        this.totalHarga = totalHarga;
    }
}
