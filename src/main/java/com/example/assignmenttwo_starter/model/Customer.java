package com.example.assignmenttwo_starter.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "customers")
public class Customer implements Serializable {

    @Id
    @NotNull
    @Column(name = "customer_id")
    private Integer customerId;

    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;

    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//consider using this annotation to enforce field validation
    @Size(max = 255)
    @Column(name = "email")

    private String email;

    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//consider using this annotation to enforce field validation
    @Size(max = 20)
    @Column(name = "phone")
    private String phone;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 50)
    @Column(name = "city")
    private String city;

    @Size(max = 50)
    @Column(name = "country")
    private String country;

    @Size(max = 10)
    @Column(name = "postcode")
    private String postcode;

    @OneToMany(mappedBy = "customerId")
    @JsonManagedReference
    @ToString.Exclude
    private List<Review> reviewCollection;

    @OneToMany(mappedBy = "customerId")
    @JsonManagedReference
    @ToString.Exclude
    private List<Orders> ordersCollection;
}
