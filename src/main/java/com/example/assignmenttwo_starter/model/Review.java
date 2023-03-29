package com.example.assignmenttwo_starter.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "reviews")
public class Review implements Serializable {

    @Id
    @NotNull
    @Column(name = "review_id")
    private Integer id;

    @Column(name = "rating")
    private Integer rating;

    @Lob
    @Size(max = 65535)
    @Column(name = "comment")
    private String comment;

    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @ManyToOne
    @JsonBackReference
    @ToString.Exclude
    private Product product;

    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    @ManyToOne
    @JsonBackReference
    @ToString.Exclude
    private Customer customer;
}
