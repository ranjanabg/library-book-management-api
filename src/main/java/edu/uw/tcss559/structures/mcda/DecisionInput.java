package edu.uw.tcss559.structures.mcda;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class DecisionInput {
    @NonNull public List<Attribute> attributes;
    @NonNull public List<Alternative> alternatives;
}
