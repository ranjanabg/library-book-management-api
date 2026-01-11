package edu.uw.tcss559.structures.mcda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {
    @NonNull public String name;
    		 public Double weight;
}
