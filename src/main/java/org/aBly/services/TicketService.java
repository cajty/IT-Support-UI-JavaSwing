package org.aBly.services;



import org.aBly.models.ticket.TicketRequest;
import org.aBly.models.ticket.TicketResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface TicketService {

    @POST("tickets")
    Call<TicketResponse> createTicket(@Body TicketRequest request);

    @GET("tickets/{id}")
    Call<TicketResponse> getTicketById(@Path("id") Long ticketId);

    @PATCH("tickets/{id}/status")
    Call<Boolean> updateTicketStatus(@Path("id") Long ticketId, @Query("status") String status);

    @GET("tickets/search")
    Call<List<TicketResponse>> searchTickets(
        @Query("category") String category,
        @Query("ticketPriority") String priority,
        @Query("status") String status,
        @Query("page") int page,
        @Query("size") int size,
        @Query("sortBy") String sortBy,
        @Query("sortDirection") String sortDirection
    );
}

