package org.aBly.models.ticket;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import com.google.gson.annotations.SerializedName;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class TicketRequest {

    @SerializedName("title")
    private final String title;

    @SerializedName("description")
    private final String description;

    @SerializedName("ticketPriority")
    private final String ticketPriority;

    @SerializedName("categoryId")
    private final Long categoryId;
}
