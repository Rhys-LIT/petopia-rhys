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
@Table(name = "order_status")
public class OrderStatus implements Serializable {

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_status_id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "status")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderStatus")
    @JsonManagedReference
    @ToString.Exclude
    private List<Order> orders;

    // Status Methods
    public boolean isCancelled() {
        return this.id == 4;
    }

    public boolean isDelivered() {
        return this.id == 2;
    }

    public boolean isPending() {
        return this.id == 5;
    }
    public boolean isProcessing() {
        return this.id == 3;
    }

    public boolean isShipped() {
        return this.id == 1;
    }
    // More Status Methods
    public boolean isPendingOrProcessing() {
        return isPending() || isProcessing();
    }
}