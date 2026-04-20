package interfaces;

import java.util.List;
import exception.ItemNotFoundException;
import exception.UserNotFoundException;

/* Generic interface defining the standard set of CRUD operations that
every Manager class in the data layer must support. Using generics
lets the same contract apply to Items, Users, Loans, or any future
entity type. This decouples the service layer from concrete manager
implementations and makes the system easier to extend. */
public interface Manageable<T> {

    // Insert a new record into the database.
    void add(T entity);

    // Retrieve a record by its primary key. 
    T getById(int id) throws ItemNotFoundException, UserNotFoundException;

    // Update an existing record. 
    void update(T entity);

    // Delete a record by its primary key. 
    void delete(int id);

    // Return all records of this entity type. 
    List<T> getAll();
}
