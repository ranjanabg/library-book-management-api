package edu.uw.tcss559.structures.notification;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class EmailContent {
    @NonNull public String type;
    @NonNull public String value;
}
