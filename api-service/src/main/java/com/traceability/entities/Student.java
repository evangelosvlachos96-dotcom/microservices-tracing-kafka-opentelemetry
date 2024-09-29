package com.traceability.entities;

import jakarta.persistence.Entity;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "students")
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idStudent;
    String firstname;
    String lastname;
}
