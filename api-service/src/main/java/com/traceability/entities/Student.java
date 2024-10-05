package com.traceability.entities;

import jakarta.persistence.Entity;
import lombok.*;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@Table(name = "students", uniqueConstraints = {@UniqueConstraint(columnNames = {"firstname", "lastname"})})
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idStudent;
    String firstname;
    String lastname;
    Double fees;
}
