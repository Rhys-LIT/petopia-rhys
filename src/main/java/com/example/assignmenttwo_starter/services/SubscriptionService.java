package com.example.assignmenttwo_starter.services;

import com.example.assignmenttwo_starter.model.Subscription;
import com.example.assignmenttwo_starter.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Subscription createSubscription(Subscription subscription) {
        subscription.setId(subscriptionRepository.getNextFreeId());
        return subscriptionRepository.save(subscription);
    }

    public void deleteSubscription(Integer subscriptionId) {
        Subscription subscription = subscriptionRepository
                .findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription with ID " + subscriptionId + " not found"));

        subscriptionRepository.delete(subscription);
    }

    public List<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }

    public Page<Subscription> findAll(PageRequest pageRequest) {
        return subscriptionRepository.findAll(pageRequest);
    }

    public Optional<Subscription> findById(Integer subscriptionId) {
        return subscriptionRepository.findById(subscriptionId);
    }

    public Subscription updateSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }
}


