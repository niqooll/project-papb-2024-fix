package com.example.indoconcertfix;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TicketDao {
    @Insert
    void insert(TicketOrder order);

    @Query("SELECT * FROM ticket_orders")
    List<TicketOrder> getAllOrders();

    @Delete
    void deleteOrder(TicketOrder order);

}
