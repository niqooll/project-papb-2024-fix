package com.example.indoconcertfix;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter {
    private final Context ctx;
    private final List<Event> daftar;

    public EventAdapter(Context ctx, List<Event> daftar) {
        this.ctx = ctx;
        this.daftar = daftar;
    }

    private class VH extends RecyclerView.ViewHolder {

        private final TextView judul;
        private final ImageView imageUrl;
        private final TextView harga;
        private final TextView lokasi;
        private final TextView deskripsi;
        private final Button btPesan;

        private VH(@NonNull View itemView) {
            super(itemView);
            this.judul = itemView.findViewById(R.id.Judul);
            this.imageUrl = itemView.findViewById(R.id.gambar1);
            this.harga = itemView.findViewById(R.id.Harga);
            this.lokasi = itemView.findViewById(R.id.Lokasi);
            this.deskripsi = itemView.findViewById(R.id.Deskripsi);
            this.btPesan = itemView.findViewById(R.id.btPesan);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.ctx).inflate(R.layout.item_event,parent,false);
        VH vh = new VH(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = this.daftar.get(position);
        VH vh = (VH) holder;
        vh.judul.setText(event.judul);
        vh.lokasi.setText(event.lokasi);

        // Mengubah format harga
        String harga = event.harga;
        harga = harga.replace("IDR", "").replace("K", "000").trim(); // Menghapus "IDR" dan mengganti "K" dengan "000"
        int hargaInt = 0;
        try {
            hargaInt = Integer.parseInt(harga); // Mengonversi harga ke integer
        } catch (NumberFormatException e) {
            Log.e("Error", "Format harga tidak valid: " + harga);
        }

        // Menampilkan harga
        vh.harga.setText("IDR " + String.format("%,d", hargaInt).replace(",", "."));
        vh.deskripsi.setText(event.deskripsi);
        Glide.with(ctx).load(event.imageUrl).into(vh.imageUrl);

        // Jika tiket habis, ubah status tampilan
        if (event.isSoldOut) {
            vh.harga.setText("Tiket Habis");
            vh.harga.setTextColor(ContextCompat.getColor(ctx, R.color.red));
            vh.btPesan.setBackgroundColor(ContextCompat.getColor(ctx, R.color.gray));  // Warna abu-abu untuk tombol
            vh.btPesan.setEnabled(false);  // Nonaktifkan tombol
        } else {
            vh.btPesan.setBackgroundColor(ContextCompat.getColor(ctx, R.color.blue));  // Warna biru untuk tombol
            vh.btPesan.setEnabled(true);  // Aktifkan tombol
        }

        vh.itemView.setOnClickListener(v ->
                Toast.makeText(ctx, "Tiket: " + event.judul + " di " + event.lokasi, Toast.LENGTH_SHORT).show()
        );

        vh.btPesan.setOnClickListener(view -> {
            PemesananFragment pemesananFragment = new PemesananFragment();
            Bundle args = new Bundle();
            args.putString("judul", event.judul);
            args.putString("lokasi", event.lokasi);
            args.putString("harga", event.harga);
            args.putString("deskripsi", event.deskripsi);
            args.putString("imageUrl", event.imageUrl);
            pemesananFragment.setArguments(args);

            ((MainActivity) ctx).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, pemesananFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return this.daftar.size();
    }




}
