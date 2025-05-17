package com.sithumonline.simplelibrary.simple_library_api.repository;

import com.sithumonline.simplelibrary.simple_library_api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    /**
     * Count how many copies exist for a given ISBN, used to assign the next copyId.
     */
    long countByIsbn(String isbn);
}
