package edu.uw.tcss559.structures.notification;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class EmailRequest {
    @NonNull public List<EmailDestination> personalizations;
    @NonNull public Email from;
    @NonNull public List<EmailContent> content;
}