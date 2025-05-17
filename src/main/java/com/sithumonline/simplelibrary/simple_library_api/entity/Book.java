package com.sithumonline.simplelibrary.simple_library_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "books",
        uniqueConstraints = @UniqueConstraint(columnNames = {"isbn", "copyId"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String isbn;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String author;

    /**
     * Distinguishes multiple copies of the same ISBN.
     */
    @Column(nullable = false)
    private int copyId;

    /**
     * Who currently has this copy; null if available.
     */
    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Borrower borrowedBy;
}
