package com.traceability.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@Table(name = "students")
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
