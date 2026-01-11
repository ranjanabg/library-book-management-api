package edu.uw.tcss559.structures.mcda;

import java.util.List;

import edu.uw.tcss559.structures.Book;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Alternative {
    @NonNull public Book book;
    @NonNull public List<Integer> attributeValues;
}
