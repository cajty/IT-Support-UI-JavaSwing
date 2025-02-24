package org.aBly.models.ticket;


import lombok.Getter;
import com.google.gson.annotations.SerializedName;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("ticketPriority")
    private String ticketPriority;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("status")
    private String status;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("creatorName")
    private String creatorName;
}

