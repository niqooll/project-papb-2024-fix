package com.example.indoconcertfix;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<TicketOrder> orderList = new ArrayList<>();
    private DatabaseReference dbRef;
    private ImageView btBack;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.rvOrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbRef = FirebaseDatabase.getInstance().getReference("orders");

        adapter = new OrderAdapter(getContext(), orderList, dbRef);
        recyclerView.setAdapter(adapter);
        fetchOrders();

        btBack = view.findViewById(R.id.btBack1);
        btBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    // Fetch orders from Firebase and update the RecyclerView
    private void fetchOrders() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Thread(() -> {
                    orderList.clear(); // Clear existing list
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        TicketOrder order = orderSnapshot.getValue(TicketOrder.class);
                        if (order != null) {
                            orderList.add(order); // Add new order to the list
                        }
                    }

                    // Pastikan notifyDataSetChanged() dijalankan di main thread dan fragment masih attached
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            adapter.notifyDataSetChanged(); // Notify adapter about data changes
                            Log.d("OrderList", "Orders fetched: " + orderList.size());
                        });
                    }
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
                Log.e("OrderList", "Error fetching orders: " + error.getMessage());
            }
        });
    }
}
