package com.sithumonline.simplelibrary.simple_library_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sithumonline.simplelibrary.simple_library_api.dto.BookDto;
import com.sithumonline.simplelibrary.simple_library_api.dto.BorrowerDto;
import com.sithumonline.simplelibrary.simple_library_api.entity.Book;
import com.sithumonline.simplelibrary.simple_library_api.entity.Borrower;
import com.sithumonline.simplelibrary.simple_library_api.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    LibraryService svc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void createBorrower_returns201() throws Exception {
        BorrowerDto dto = new BorrowerDto("Alice","alice@x.com");
        Borrower saved = Borrower.builder()
                .id(UUID.randomUUID())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
        when(svc.registerBorrower(dto.getName(), dto.getEmail())).thenReturn(saved);

        mvc.perform(post("/api/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@x.com"));
    }

    @Test
    void createBook_returns201() throws Exception {
        BookDto dto = new BookDto("ISBN","Title","Author");
        Book saved = Book.builder()
                .id(UUID.randomUUID())
                .isbn(dto.getIsbn())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .copyId(1)
                .build();
        when(svc.registerBook(anyString(), anyString(), anyString())).thenReturn(saved);

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.copyId").value(1))
                .andExpect(jsonPath("$.isbn").value("ISBN"));
    }

    @Test
    void listBooks_returnsList() throws Exception {
        Book b1 = Book.builder().id(UUID.randomUUID()).title("A").build();
        Book b2 = Book.builder().id(UUID.randomUUID()).title("B").build();
        when(svc.listAllBooks()).thenReturn(List.of(b1,b2));

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void borrowBook_returnsUpdated() throws Exception {
        UUID bookId = UUID.randomUUID(), borrowerId = UUID.randomUUID();
        Book updated = Book.builder().id(bookId).borrowedBy(new Borrower(borrowerId,"","")).build();
        when(svc.borrowBook(bookId, borrowerId)).thenReturn(updated);

        mvc.perform(put("/api/books/{id}/borrow", bookId)
                        .param("borrowerId", borrowerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowedBy.id").value(borrowerId.toString()));
    }

    @Test
    void returnBook_returnsUpdated() throws Exception {
        UUID bookId = UUID.randomUUID();
        Book returned = Book.builder().id(bookId).borrowedBy(null).build();
        when(svc.returnBook(bookId)).thenReturn(returned);

        mvc.perform(put("/api/books/{id}/return", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowedBy").doesNotExist());
    }
}
