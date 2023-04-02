package com.example.assignmenttwo_starter.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "subscription")
public class Subscription extends RepresentationModel<Subscription> implements Serializable {

    @Id

    private int id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotNull
    @Lob
    private String description;

    @NotBlank
    @Pattern(regexp = "^https?://.+")
    private String url;
}