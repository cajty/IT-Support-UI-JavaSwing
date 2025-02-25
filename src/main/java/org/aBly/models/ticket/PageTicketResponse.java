package org.aBly.models.ticket;

import lombok.Data;
import java.util.List;

@Data
public class PageTicketResponse {
    private List<TicketResponse> content;
    private int totalElements;
    private int totalPages;
    private int size;
    private int number;
    private boolean first;
    private boolean last;
    private int numberOfElements;
    private boolean empty;
}
