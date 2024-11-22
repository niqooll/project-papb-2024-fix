package com.example.indoconcertfix;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<TicketOrder> orderList;
    private TicketDatabase db;

    public OrderAdapter(List<TicketOrder> orderList, TicketDatabase db) {
        this.orderList = orderList;
        this.db = db;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        TicketOrder order = orderList.get(position);
        holder.judulTextView.setText(order.getJudul());
        holder.lokasiTextView.setText(order.getLokasi());
        holder.hargaTextView.setText("IDR " + order.getTotalHarga());
        holder.quantityTextView.setText(order.getQuantity() + "x Tiket");
        holder.btRefund.setOnClickListener(v -> removeOrder(order, position));

        String base64Image = order.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            Bitmap bitmap = ImageUtil.decodeBase64ToImage(base64Image); // Decode Base64 ke Bitmap
            if (bitmap != null) {
                holder.ivGambarT.setImageBitmap(bitmap);
            } else {
                Log.e("OrderAdapter", "Gambar tidak valid dari Base64");
                holder.ivGambarT.setImageResource(R.drawable.jc5); // Gunakan placeholder jika gambar invalid
            }
        } else {
            holder.ivGambarT.setImageResource(R.drawable.jc5); // Gunakan placeholder jika tidak ada gambar
        }

    }

    public void updateOrders(List<TicketOrder> newOrders) {
        this.orderList.clear(); // Hapus semua order yang ada
        this.orderList.addAll(newOrders); // Tambahkan order yang baru
        notifyDataSetChanged(); // Memberi tahu adapter untuk merefresh UI
    }

    // Untuk menambah order baru di adapter
    public void addOrder(TicketOrder newOrder) {
        orderList.add(newOrder);
        notifyItemInserted(orderList.size() - 1); // Memberi tahu bahwa ada item baru yang ditambahkan
    }

    // Untuk menghapus order dan memberitahukan perubahan di UI
    private void removeOrder(TicketOrder order, int position) {
        new Thread(() -> {
            db.ticketOrderDao().deleteOrder(order); // Menghapus order dari database
            if (position >= 0 && position < orderList.size()) {
                orderList.remove(position); // Menghapus order dari list
                new Handler(Looper.getMainLooper()).post(() -> {
                    notifyItemRemoved(position); // Memberi tahu adapter bahwa item telah dihapus
                });
            }
        }).start();
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivGambarT;
        TextView judulTextView, lokasiTextView, hargaTextView, quantityTextView;
        View btRefund;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            judulTextView = itemView.findViewById(R.id.tvJudulOrder);
            lokasiTextView = itemView.findViewById(R.id.tvLokasiOrder);
            hargaTextView = itemView.findViewById(R.id.tvHargaOrder);
            quantityTextView = itemView.findViewById(R.id.tvQuantityOrder);
            btRefund = itemView.findViewById(R.id.btRefund);
            ivGambarT = itemView.findViewById(R.id.ivGambarT);
        }
    }
}

