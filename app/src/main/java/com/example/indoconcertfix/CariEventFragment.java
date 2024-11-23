package com.example.indoconcertfix;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CariEventFragment extends Fragment {
    private EventAdapter eventAdapter;
    private List<Event> daftar;
    private EditText etSearch;
    private TextView tvNoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cari_event, container, false);

        // Inisialisasi komponen UI
        ImageView btCari = view.findViewById(R.id.btCari);
        RecyclerView rvEvents = view.findViewById(R.id.rvEvent);
        etSearch = view.findViewById(R.id.et_search);
        tvNoData = view.findViewById(R.id.tvNoData);

        // Set listener untuk button dan search bar
        btCari.setOnClickListener(v -> searchEvents(etSearch.getText().toString()));
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchEvents(etSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Inisialisasi RecyclerView dan adapter
        daftar = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), daftar);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEvents.setAdapter(eventAdapter);

        return view;

    }
    private void searchEvents(String keyword) {
        String url = "http://10.12.34.64/IndoConcertAPI/search_event.php?keyword=" + keyword;
        @SuppressLint("NotifyDataSetChanged") StringRequest req = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Gson gson = new Gson();
                    Event[] daftarArray = gson.fromJson(response, Event[].class);
                    daftar.clear();
                    Collections.addAll(daftar, daftarArray);

                    // Urutkan tiket berdasarkan ketersediaan (tiket tersedia di atas)
                    daftar.sort((e1, e2) -> {
                        return Boolean.compare(e1.isSoldOut, e2.isSoldOut);  // tiket tidak habis (false) di atas
                    });

                    eventAdapter.notifyDataSetChanged();

                    if (daftar.isEmpty()) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }
                },
                error -> Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show()
        );
        Volley.newRequestQueue(requireContext()).add(req);
    }
}