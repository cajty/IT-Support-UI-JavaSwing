package org.aBly.services.client;

import org.aBly.models.ticket.PageTicketResponse;
import org.aBly.models.ticket.TicketRequest;
import org.aBly.models.ticket.TicketResponse;
import org.aBly.services.TicketService;
import retrofit2.Response;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class TicketClient extends BaseClient {
    private final TicketService ticketService;

    @Inject
    public TicketClient(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public Optional<TicketResponse> createTicket(TicketRequest request) {
        return executeCall(() -> ticketService.createTicket(request).execute());
    }

    public Optional<TicketResponse> getTicketById(Long ticketId) {
        return executeCall(() -> ticketService.getTicketById(ticketId).execute());
    }

    public boolean updateTicketStatus(Long ticketId, String status) {
        return executeCall(() -> ticketService.updateTicketStatus(ticketId, status).execute())
                .map(Boolean::booleanValue)
                .orElse(false);
    }

  public Optional<PageTicketResponse> searchTickets(String category, String priority, String status, int page, int size, String sortBy, String sortDirection) {
    return executeCall(() -> ticketService.searchTickets(category, priority, status, page, size, sortBy, sortDirection).execute());
}
}
