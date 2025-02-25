package org.aBly.models.category;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Category {
   @SerializedName("id")
    private Long id;
     @SerializedName("name")
    private String name;
}
