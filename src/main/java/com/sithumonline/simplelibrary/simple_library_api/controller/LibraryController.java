package com.sithumonline.simplelibrary.simple_library_api.controller;

import com.sithumonline.simplelibrary.simple_library_api.dto.BookDto;
import com.sithumonline.simplelibrary.simple_library_api.dto.BorrowerDto;
import com.sithumonline.simplelibrary.simple_library_api.entity.Book;
import com.sithumonline.simplelibrary.simple_library_api.entity.Borrower;
import com.sithumonline.simplelibrary.simple_library_api.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LibraryController {

    private final LibraryService libraryService;

    @Autowired
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Register a new borrower.
     * POST /api/borrowers
     */
    @PostMapping("/borrowers")
    public ResponseEntity<Borrower> createBorrower(@Valid @RequestBody BorrowerDto dto) {
        Borrower created = libraryService.registerBorrower(dto.getName(), dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Register a new book copy.
     * POST /api/books
     */
    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookDto dto) {
        Book created = libraryService.registerBook(dto.getIsbn(), dto.getTitle(), dto.getAuthor());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * List all books (with borrower info if borrowed).
     * GET /api/books
     */
    @GetMapping("/books")
    public ResponseEntity<List<Book>> listBooks() {
        List<Book> books = libraryService.listAllBooks();
        return ResponseEntity.ok(books);
    }

    /**
     * Borrow a specific book copy.
     * PUT /api/books/{bookId}/borrow?borrowerId={borrowerId}
     */
    @PutMapping("/books/{bookId}/borrow")
    public ResponseEntity<Book> borrowBook(
            @PathVariable UUID bookId,
            @RequestParam UUID borrowerId) {
        Book updated = libraryService.borrowBook(bookId, borrowerId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Return a specific book copy.
     * PUT /api/books/{bookId}/return
     */
    @PutMapping("/books/{bookId}/return")
    public ResponseEntity<Book> returnBook(@PathVariable UUID bookId) {
        Book updated = libraryService.returnBook(bookId);
        return ResponseEntity.ok(updated);
    }
}
