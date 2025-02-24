package org.aBly.services.client;

import lombok.extern.slf4j.Slf4j;
import org.aBly.models.ticket.TicketRequest;
import org.aBly.models.ticket.TicketResponse;
import org.aBly.services.TicketService;
import org.aBly.utils.SessionManager;
import retrofit2.Response;
import javax.inject.Inject;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.List;
@Slf4j
public class TicketClient extends BaseClient {
    private final TicketService ticketService;

    @Inject
    public TicketClient(TicketService ticketService) {

        this.ticketService = ticketService;
    }

    /**
     * Create a new ticket.
     */
    public TicketResponse createTicket(TicketRequest request) {
        if (!isAuthenticated()) return null;

        try {
            Response<TicketResponse> response = ticketService.createTicket(request).execute();
            return response.isSuccessful() ? response.body() : null;
        } catch (IOException e) {
            handleError(e);
            return null;
        }
    }


    public TicketResponse getTicketById(Long ticketId) {
        if (!isAuthenticated()) return null;

        try {
            Response<TicketResponse> response = ticketService.getTicketById(ticketId).execute();
            return response.isSuccessful() ? response.body() : null;
        } catch (IOException e) {
            handleError(e);
            return null;
        }
    }

    /**
     * Update the status of a ticket.
     */
    public boolean updateTicketStatus(Long ticketId, String status) {
        if (!isAuthenticated()) return false;

        try {
            Response<Boolean> response = ticketService.updateTicketStatus(ticketId, status).execute();
            return response.isSuccessful() && Boolean.TRUE.equals(response.body());
        } catch (IOException e) {
            handleError(e);
            return false;
        }
    }

    /**
     * Search for tickets based on filters.
     */
    public List<TicketResponse> searchTickets(String category, String priority, String status, int page, int size, String sortBy, String sortDirection) {
        if (!isAuthenticated()) return null;

        try {
            Response<List<TicketResponse>> response = ticketService.searchTickets(category, priority, status, page, size, sortBy, sortDirection).execute();
            return response.isSuccessful() ? response.body() : null;
        } catch (IOException e) {
            handleError(e);
            return null;
        }
    }

    // Helper method to check authentication
    private boolean isAuthenticated() {
        String authToken = SessionManager.getToken();
        if (authToken == null) {
            JOptionPane.showMessageDialog(null, "User is not authenticated", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // Helper method to handle API errors
    private void handleError(IOException e) {
        System.err.println("API Request failed: " + e.getMessage());
    }
}