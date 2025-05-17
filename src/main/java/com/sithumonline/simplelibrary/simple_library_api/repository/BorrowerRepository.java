package com.sithumonline.simplelibrary.simple_library_api.repository;

import com.sithumonline.simplelibrary.simple_library_api.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, UUID> {
    // no extra methods needed for now
}
