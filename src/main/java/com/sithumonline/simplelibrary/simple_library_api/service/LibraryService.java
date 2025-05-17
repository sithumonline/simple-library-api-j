package com.sithumonline.simplelibrary.simple_library_api.service;

import com.sithumonline.simplelibrary.simple_library_api.entity.Book;
import com.sithumonline.simplelibrary.simple_library_api.entity.Borrower;
import com.sithumonline.simplelibrary.simple_library_api.repository.BookRepository;
import com.sithumonline.simplelibrary.simple_library_api.repository.BorrowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    @Autowired
    public LibraryService(BookRepository bookRepository,
                          BorrowerRepository borrowerRepository) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    /**
     * Register a new borrower.
     */
    public Borrower registerBorrower(String name, String email) {
        Borrower borrower = Borrower.builder()
                .name(name)
                .email(email)
                .build();
        return borrowerRepository.save(borrower);
    }

    /**
     * Register a new book copy for the given ISBN.
     */
    public Book registerBook(String isbn, String title, String author) {
        long nextCopyId = bookRepository.countByIsbn(isbn) + 1;
        Book book = Book.builder()
                .isbn(isbn)
                .title(title)
                .author(author)
                .copyId((int) nextCopyId)
                .build();
        return bookRepository.save(book);
    }

    /**
     * List all books (including who has borrowed which copy).
     */
    public List<Book> listAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Borrow a book copy by its ID for the given borrower ID.
     * Throws if the book is already borrowed or IDs are invalid.
     */
    @Transactional
    public Book borrowBook(UUID bookId, UUID borrowerId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));
        if (book.getBorrowedBy() != null) {
            throw new IllegalStateException("Book copy already borrowed: " + bookId);
        }

        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found: " + borrowerId));

        book.setBorrowedBy(borrower);
        return bookRepository.save(book);
    }

    /**
     * Return a book copy by its ID.
     * Throws if the book isn't currently borrowed.
     */
    @Transactional
    public Book returnBook(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));
        if (book.getBorrowedBy() == null) {
            throw new IllegalStateException("Book copy is not currently borrowed: " + bookId);
        }

        book.setBorrowedBy(null);
        return bookRepository.save(book);
    }
}
