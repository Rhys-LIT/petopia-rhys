package com.example.assignmenttwo_starter.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "categories")
public class Category implements Serializable {


        @Id
        @NotNull
        private Integer id;

        @Size(max = 50)
        private String name;

        @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
        @JsonManagedReference
        @ToString.Exclude
        private List<Product> products;
}
