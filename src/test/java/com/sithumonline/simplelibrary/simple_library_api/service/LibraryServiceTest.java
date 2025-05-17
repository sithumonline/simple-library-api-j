package com.sithumonline.simplelibrary.simple_library_api.service;

import com.sithumonline.simplelibrary.simple_library_api.entity.Book;
import com.sithumonline.simplelibrary.simple_library_api.entity.Borrower;
import com.sithumonline.simplelibrary.simple_library_api.repository.BookRepository;
import com.sithumonline.simplelibrary.simple_library_api.repository.BorrowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    BookRepository bookRepo;

    @Mock
    BorrowerRepository borrowerRepo;

    @InjectMocks
    LibraryService svc;

    @Test
    void registerBorrower_savesAndReturns() {
        Borrower out = Borrower.builder()
                .id(UUID.randomUUID())
                .name("Bob")
                .email("bob@x.com")
                .build();
        when(borrowerRepo.save(any(Borrower.class))).thenReturn(out);

        Borrower result = svc.registerBorrower("Bob", "bob@x.com");

        assertThat(result.getId()).isEqualTo(out.getId());
        assertThat(result.getName()).isEqualTo("Bob");
        assertThat(result.getEmail()).isEqualTo("bob@x.com");
    }

    @Test
    void registerBook_incrementsCopyId() {
        when(bookRepo.countByIsbn("ISBN1")).thenReturn(2L);
        Book saved = Book.builder()
                .id(UUID.randomUUID())
                .isbn("ISBN1")
                .title("T")
                .author("A")
                .copyId(3)
                .build();
        when(bookRepo.save(any(Book.class))).thenReturn(saved);

        Book result = svc.registerBook("ISBN1", "T", "A");

        assertThat(result.getCopyId()).isEqualTo(3);
        verify(bookRepo).save(argThat(b -> b.getCopyId()==3 && b.getIsbn().equals("ISBN1")));
    }

    @Test
    void listAllBooks_delegatesToRepo() {
        List<Book> list = List.of(new Book(), new Book());
        when(bookRepo.findAll()).thenReturn(list);

        List<Book> result = svc.listAllBooks();

        assertThat(result).hasSize(2);
        verify(bookRepo).findAll();
    }

    @Test
    void borrowBook_successful() {
        UUID bookId = UUID.randomUUID();
        UUID borrowerId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).build();
        Borrower borrower = Borrower.builder().id(borrowerId).build();

        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowerRepo.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(bookRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Book result = svc.borrowBook(bookId, borrowerId);

        assertThat(result.getBorrowedBy()).isEqualTo(borrower);
    }

    @Test
    void borrowBook_alreadyBorrowed_throws() {
        UUID id = UUID.randomUUID();
        Book book = Book.builder().id(id).borrowedBy(new Borrower()).build();
        when(bookRepo.findById(id)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> svc.borrowBook(id, UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already borrowed");
    }

    @Test
    void borrowBook_missingBook_throws() {
        UUID id = UUID.randomUUID();
        when(bookRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> svc.borrowBook(id, UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void returnBook_successful() {
        UUID id = UUID.randomUUID();
        Borrower b = Borrower.builder().id(UUID.randomUUID()).build();
        Book book = Book.builder().id(id).borrowedBy(b).build();
        when(bookRepo.findById(id)).thenReturn(Optional.of(book));
        when(bookRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Book result = svc.returnBook(id);

        assertThat(result.getBorrowedBy()).isNull();
    }

    @Test
    void returnBook_notBorrowed_throws() {
        UUID id = UUID.randomUUID();
        Book book = Book.builder().id(id).borrowedBy(null).build();
        when(bookRepo.findById(id)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> svc.returnBook(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not currently borrowed");
    }
}
