package com.example.indoconcertfix;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {
    private final Context ctx;
    private List<TicketOrder> orderList;
    private final DatabaseReference dbRef;

    public OrderAdapter(Context ctx, List<TicketOrder> orderList, DatabaseReference dbRef) {
        this.ctx = ctx;
        this.orderList = orderList;
        this.dbRef = dbRef;
    }

    public class VH extends RecyclerView.ViewHolder {
        private final TextView tvJudulOrder;
        private final TextView tvLokasiOrder;
        private final TextView tvHargaOrder;
        private final TextView tvQuantityOrder;
        private final Button btnDeleteOrder;
        private final ImageView tvGambarOrder;


        public VH(@NonNull View itemView) {
            super(itemView);
            this.tvJudulOrder = itemView.findViewById(R.id.tvJudulOrder);
            this.tvLokasiOrder = itemView.findViewById(R.id.tvLokasiOrder);
            this.tvHargaOrder = itemView.findViewById(R.id.tvHargaOrder);
            this.tvQuantityOrder = itemView.findViewById(R.id.tvQuantityOrder);
            this.tvGambarOrder = itemView.findViewById(R.id.ivGambarT);

            this.btnDeleteOrder = itemView.findViewById(R.id.btRefund);
        }

        public void bind(TicketOrder order) {
            tvJudulOrder.setText(order.getJudul());
            tvLokasiOrder.setText(order.getLokasi());
            tvHargaOrder.setText("Total Harga : IDR " + order.getTotalHarga());
            tvQuantityOrder.setText(order.getJumlah() + "x Tiket");
            if (order.getEncodedImage() != null) {
                Bitmap decodedBitmap = ImageUtil.decodeBase64ToImage(order.getEncodedImage());
                tvGambarOrder.setImageBitmap(decodedBitmap);
            }

            btnDeleteOrder.setOnClickListener(v -> {
                dbRef.child(order.getId()).removeValue();
            });
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TicketOrder order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
