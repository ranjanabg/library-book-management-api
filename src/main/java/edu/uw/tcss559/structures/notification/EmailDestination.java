package edu.uw.tcss559.structures.notification;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class EmailDestination {
    @NonNull public List<Email> to;
    @NonNull public String subject;
}
