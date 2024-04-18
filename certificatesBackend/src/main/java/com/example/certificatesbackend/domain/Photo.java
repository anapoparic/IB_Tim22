package com.example.certificatesbackend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String url;

    @Column(nullable = true, unique = false)
    private String caption;

    @Column(nullable = true)
    private int width;

    @Column(nullable = true)
    private int height;

    @Column(nullable = false)
    private boolean active = true;
}
