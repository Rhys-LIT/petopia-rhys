package com.example.assignmenttwo_starter.repositories;

import com.example.assignmenttwo_starter.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    /**
     * Simple query to get the next free id in the database
     * For example, if the database contains 1, 2, 5, 6, 7, 8, 9, 10
     * then the next free id is 11
     * Another example, if the database contains 2, 5, 7
     * then the next free id is 8
     * If the database has no records the maz is 0 + 1 = 1
     *
     * @return The next free id (Max + 1)
     */
    @Query("SELECT MAX(customer.id) + 1 FROM Customer customer")
    public int getNextFreeId();

    List<Customer> findAllByFirstNameEqualsIgnoreCase(String firstname);
}

