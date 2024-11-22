package com.example.indoconcertfix;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class PemesananFragment extends Fragment {
    private TextView judulTextView, judul2Textview, lokasiTextView, hargaTextView, totalHargaTextView;
    private ImageView gambarImageView, tombolBack;
    private TextView tvQuantity;
    private int ticketQuantity = 1; // Jumlah tiket dimulai dari 1
    private Button btBayar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pemesanan, container, false);

        judulTextView = view.findViewById(R.id.tvJudul);
        judul2Textview = view.findViewById(R.id.tvJudul2);
        lokasiTextView = view.findViewById(R.id.tvLokasi);
        hargaTextView = view.findViewById(R.id.tvHarga);
        gambarImageView = view.findViewById(R.id.ivGambar);
        tombolBack = view.findViewById(R.id.btBack);
        btBayar = view.findViewById(R.id.btBayar);

        tvQuantity = view.findViewById(R.id.tvQuantity);
        totalHargaTextView = view.findViewById(R.id.tvTotalHarga);
        ImageView btMinus = view.findViewById(R.id.btMinus);
        ImageView btPlus = view.findViewById(R.id.btPlus);

        // Mengambil data dari Bundle
        Bundle args = getArguments();
        if (args != null) {
            judulTextView.setText(args.getString("judul"));
            judul2Textview.setText(args.getString("judul"));
            lokasiTextView.setText(args.getString("lokasi"));
            String imageUrl = args.getString("imageUrl");
            if (imageUrl != null) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(gambarImageView);
            }

            // Mengambil dan memformat harga jika ada di dalam args
            String hargaStr = args.getString("harga");
            if (hargaStr != null && !hargaStr.isEmpty()) {
                hargaTextView.setText("IDR " + hargaStr);
                updateTotalPrice(); // Update total harga saat fragment pertama kali ditampilkan
            } else {
                hargaTextView.setText("IDR 0");
                totalHargaTextView.setText("IDR 0");
            }


        } else {
            Log.d("PemesananFragment", "Bundle is null");
        }

        // Tombol Kembali
        tombolBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Tombol Bayar
        btBayar.setOnClickListener(v -> {
            Fragment orderListFragment = new OrderListFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, orderListFragment)
                    .addToBackStack(null)
                    .commit();

            String judul = judulTextView.getText().toString();
            String lokasi = lokasiTextView.getText().toString();
            String harga = hargaTextView.getText().toString();
            int totalHarga = 0;
            try {
                totalHarga = Integer.parseInt(totalHargaTextView.getText().toString().replace("IDR ", "").replace(".", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            int quantity = ticketQuantity;

            // Ambil gambar dari ImageView sebagai Bitmap
            gambarImageView.setDrawingCacheEnabled(true);
            gambarImageView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(gambarImageView.getDrawingCache());
            gambarImageView.setDrawingCacheEnabled(false);
            // Encode gambar ke Base64 menggunakan ImageUtil
            String base64Image = ImageUtil.encodeImageToBase64(bitmap);

            TicketOrder order = new TicketOrder(judul, lokasi, harga, quantity, totalHarga);
            order.setImageBase64(base64Image);
            saveOrderToDatabase(order);
        });

        // Update total harga saat jumlah tiket diubah
        btPlus.setOnClickListener(v -> {
            ticketQuantity++;
            tvQuantity.setText(String.valueOf(ticketQuantity));
            updateTotalPrice();
        });

        btMinus.setOnClickListener(v -> {
            if (ticketQuantity > 1) {
                ticketQuantity--;
                tvQuantity.setText(String.valueOf(ticketQuantity));
                updateTotalPrice();
            }
        });

        return view;
    }

    private void saveOrderToDatabase(TicketOrder order) {
        TicketDatabase db = TicketDatabase.getDatabase(requireContext());

        new Thread(() -> {
            try {
                db.ticketOrderDao().insert(order);
                new Handler(Looper.getMainLooper()).post(this::showSuccessNotification);
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(this::showErrorNotification);
            }
        }).start();
    }


    private void showErrorNotification() {
        Toast.makeText(requireContext(), "Gagal membuat pesanan. Coba lagi.", Toast.LENGTH_SHORT).show();
    }

    private void showSuccessNotification() {
        Toast.makeText(requireContext(), "Pesanan berhasil dibuat!", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateTotalPrice() {
        try {
            String hargaStr = hargaTextView.getText().toString();
            hargaStr = hargaStr.replace("IDR", "").replace(" ", "").replace(".", "").replace("K", "000").trim();

            int pricePerTicket = Integer.parseInt(hargaStr);
            int totalPrice = pricePerTicket * ticketQuantity;

            totalHargaTextView.setText("IDR " + String.format("%,d", totalPrice).replace(",", "."));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            totalHargaTextView.setText("IDR 0");
        }
    }
}
