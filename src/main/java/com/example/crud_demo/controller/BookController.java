package com.example.crud_demo.controller;

import com.example.crud_demo.model.Book;
import com.example.crud_demo.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository = new InMemoryBookRepository();
    private final AtomicLong counter = new AtomicLong();

    public BookController() {
        // Додаємо дві стартові книги
        bookRepository.save(new Book(null, "1984", "George Orwell", "9780451524935"));
        bookRepository.save(new Book(null, "Brave New World", "Aldous Huxley", "9780060850524"));
    }

    private class InMemoryBookRepository implements BookRepository {
        private final Map<Long, Book> books = new HashMap<>();

        @Override
        public Book save(Book book) {
            if (book.getId() == null) {
                book.setId(counter.incrementAndGet());
            }
            books.put(book.getId(), book);
            return book;
        }

        @Override
        public Optional<Book> findById(Long id) {
            return Optional.ofNullable(books.get(id));
        }

        @Override
        public List<Book> findAll() {
            return new ArrayList<>(books.values());
        }

        @Override
        public void deleteById(Long id) {
            books.remove(id);
        }
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return new ResponseEntity<>(bookRepository.findAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isPresent()) {
            updatedBook.setId(id);
            Book savedBook = bookRepository.save(updatedBook);
            return new ResponseEntity<>(savedBook, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
