package com.example.indoconcertfix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TicketDatabase db;
    private OrderAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.rvOrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = TicketDatabase.getDatabase(requireContext());

        loadOrders();

        ImageView btBack = view.findViewById(R.id.btBack1);
        btBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void addOrder(TicketOrder newOrder) {
        new Thread(() -> {
            db.ticketOrderDao().insert(newOrder); // Menambahkan order baru ke database
            requireActivity().runOnUiThread(() -> {
                loadOrders(); // Muat ulang order setelah penambahan
            });
        }).start();
    }

    private void loadOrders() {
        new Thread(() -> {
            List<TicketOrder> orders = db.ticketOrderDao().getAllOrders();
            requireActivity().runOnUiThread(() -> {
                if (adapter == null) {
                    adapter = new OrderAdapter(orders, db); // Buat adapter pertama kali
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updateOrders(orders); // Memperbarui data yang ada di adapter
                }
            });
        }).start();
    }

}
