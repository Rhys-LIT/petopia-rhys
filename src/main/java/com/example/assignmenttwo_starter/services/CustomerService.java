package com.example.assignmenttwo_starter.services;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        customer.setId(customerRepository.getNextFreeId());
        return customerRepository.save(customer);
    }
    public Customer deleteCustomer(Integer customerId) {
    Customer customer = customerRepository
            .findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + customerId + " not found"));

    customerRepository.delete(customer);
    return customer;
}
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    public Page<Customer> findAll(PageRequest pageRequest) {
        return customerRepository.findAll(pageRequest);
    }
    public Optional<Customer> findById(Integer customerId) {
        return customerRepository.findById(customerId);
    }
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}


