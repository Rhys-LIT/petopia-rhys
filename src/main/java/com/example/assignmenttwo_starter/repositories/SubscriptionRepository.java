package com.example.assignmenttwo_starter.repositories;

import com.example.assignmenttwo_starter.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    /**
     * Simple query to get the next free id in the database
     * For example, if the database contains 1, 2, 5, 6, 7, 8, 9, 10
     * then the next free id is 11
     *
     * @return The next free id (Max + 1)
     */
    @Query("SELECT MAX(subscription.id) + 1 FROM Subscription subscription")
    public int getNextFreeId();
}

