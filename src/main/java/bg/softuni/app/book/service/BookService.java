package bg.softuni.app.book.service;

import bg.softuni.app.book.model.Book;
import bg.softuni.app.book.repository.BookRepository;
import bg.softuni.app.web.dto.AddBook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;


    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;


    }

    public void saveBook(Book book) {

        bookRepository.save(book);
    }

    public List<Book> getAllBooks() {

        return bookRepository.findAll();

    }



   public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }


    @Transactional
    public Book createNewBook(AddBook addBook) {

        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title(addBook.getTitle())
                .author(addBook.getAuthor())
                .price(addBook.getPrice())
                .description("")
                .bookCoverUrl(addBook.getBookCoverUrl())
                .votes(0)
                .build();

        return bookRepository.save(book);
    }

    public List<Book> getBooksByCategoryId(UUID categoryId) {
        return bookRepository.findAllByCategoryId(categoryId);
    }



    public Book getById(UUID id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book with id [%s] not found.".formatted(id)));

    }






}
