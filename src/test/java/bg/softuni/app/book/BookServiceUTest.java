package bg.softuni.app.book;


import bg.softuni.app.book.repository.BookRepository;
import bg.softuni.app.book.service.BookService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceUTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;


}
