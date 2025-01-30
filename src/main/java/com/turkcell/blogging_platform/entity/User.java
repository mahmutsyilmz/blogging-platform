package com.turkcell.blogging_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) //tarihi otomatik oluşturmak için
public class User {

    @Id
    @GeneratedValue(generator = "UUID") // Hibernate UUID generator
    @Column(
            columnDefinition = "uuid", // Veritabanında UUID tipi için
            updatable = false,
            nullable = false,
            unique = true
    )
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(length = 500)
    private String bio; // Kullanıcı biyografisi (opsiyonel)

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt; // Otomatik oluşturulma tarihi


    @Enumerated(EnumType.STRING)
    private Role role;
}