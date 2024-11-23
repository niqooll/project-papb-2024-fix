package com.example.indoconcertfix;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PemesananFragment extends Fragment {
    public static final String DBURL = "https://indoconcert-b14f1-default-rtdb.asia-southeast1.firebasedatabase.app/";

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
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(gambarImageView);
            } else {
                gambarImageView.setImageResource(R.drawable.jc5); // Gambar default jika imageUrl kosong
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


        DatabaseReference dbRef = FirebaseDatabase.getInstance(DBURL).getReference("orders");
        btBayar.setOnClickListener(v -> {
            // Tampilkan ProgressDialog untuk memberi feedback ke pengguna
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Memproses pesanan...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Handler untuk kembali ke main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());

            // Jalankan operasi berat di thread latar belakang
            new Thread(() -> {
                try {
                    // Proses berat: encoding gambar ke Base64
                    gambarImageView.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(gambarImageView.getDrawingCache());
                    gambarImageView.setDrawingCacheEnabled(false);
                    String encodedImage = ImageUtil.encodeImageToBase64(bitmap);

                    // Siapkan data untuk Firebase
                    String id = dbRef.push().getKey();
                    String judul = judulTextView.getText().toString();
                    String lokasi = lokasiTextView.getText().toString();
                    String harga = hargaTextView.getText().toString();
                    int jumlah = ticketQuantity;
                    int totalHarga = Integer.parseInt(
                            totalHargaTextView.getText().toString()
                                    .replace("IDR ", "")
                                    .replace(".", "")
                    );

                    TicketOrder ticketOrder = new TicketOrder(judul, lokasi, harga, jumlah, encodedImage, totalHarga);
                    ticketOrder.setId(id);

                    // Simpan ke Firebase di latar belakang
                    dbRef.child(id).setValue(ticketOrder).addOnSuccessListener(aVoid -> {
                        // Kembali ke main thread untuk perpindahan fragment
                        mainHandler.post(() -> {
                            progressDialog.dismiss(); // Tutup ProgressDialog
                            Toast.makeText(requireContext(), "Pesanan berhasil disimpan!", Toast.LENGTH_SHORT).show();

                            // Pindahkan fragment
                            Fragment orderListFragment = new OrderListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("orderId", id); // Sertakan ID pesanan jika diperlukan
                            orderListFragment.setArguments(bundle);

                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, orderListFragment)
                                    .addToBackStack(null)
                                    .commit();
                        });
                    }).addOnFailureListener(e -> {
                        // Kembali ke main thread untuk menangani error
                        mainHandler.post(() -> {
                            progressDialog.dismiss(); // Tutup ProgressDialog
                            Toast.makeText(requireContext(), "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mainHandler.post(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });


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
