package interfaces;

import java.util.List;

/*
Marker interface for Manager classes that support keyword-based searching. Implemented by ItemManager (searches titles/authors)
and UserManager (searches names). Separating this from Manageable
is intentional — not every entity needs to be searchable (e.g. LoanManager), so splitting the contracts keeps each interface focused on a single responsibility.
*/
public interface Searchable<T> { //Used generic type here, per https://docs.oracle.com/javase/tutorial/java/generics/types.html

    /*
     Return a list of entities whose searchable fields contain
     the given keyword (case-insensitive partial match).
     */
    List<T> search(String keyword);
}
