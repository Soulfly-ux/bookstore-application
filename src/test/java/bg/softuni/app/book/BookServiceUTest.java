package bg.softuni.app.book;


import bg.softuni.app.book.model.Book;
import bg.softuni.app.book.repository.BookRepository;
import bg.softuni.app.book.service.BookService;
import bg.softuni.app.category.service.CategoryService;
import bg.softuni.app.web.dto.AddBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.*;


import static org.assertj.core.api.AssertionsForClassTypes.in;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceUTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private BookService bookService;


    private Book testBook1;
    private Book testBook2;
    private UUID testBookId1;
    private UUID testBookId2;
    private UUID notFoundBookId;
    private AddBook dto;




   @BeforeEach
    void setUp() {
       testBookId1 = UUID.randomUUID();
       testBookId2 = UUID.randomUUID();
       notFoundBookId = UUID.randomUUID();



       testBook1 = Book.builder()
               .id(testBookId1)
               .title("Test Book 1")
               .author("Test Author 1")
               .price(19.99)
                .description("Test Description 1")
               .bookCoverUrl("url1.jpg")
               .votes(12)
               .category(null)
               .build();

       testBook1 = Book.builder()
               .id(testBookId1)
               .title("Test Book 2")
               .author("Test Author 2")
               .price(10.99)
               .description("Test Description 2")
               .bookCoverUrl("url2.jpg")
               .votes(120)
               .category(null)
               .build();
   }

   @Test
    void saveBook_shouldSaveInRepositoryWithCorrectBook() {

       bookService.saveBook(testBook1);

         verify(bookRepository, times(1)).save(testBook1);
   }

   @Test
    void getAllBooks_whenBooksExist_shouldReturnAllBooks() {
       List<Book> expectedBooks = Arrays.asList(testBook1, testBook2);

       when(bookRepository.findAll()).thenReturn(expectedBooks);

       List<Book> actualBooks = bookService.getAllBooks();

        assertThat(actualBooks).isNotNull();
        assertThat(actualBooks).hasSize(2);
        assertThat(actualBooks).containsExactlyInAnyOrder(testBook1, testBook2);

       verify(bookRepository, times(1)).findAll();

   }

   @Test
    void getAllBooks_whenBooksDoesNotExist_shouldReturnEmptyList() {


       when(bookRepository.findAll()).thenReturn(Collections.emptyList());

       List<Book> actualBooks = bookService.getAllBooks();

       assertThat(actualBooks).isNotNull();
       assertThat(actualBooks).isEmpty();

       verify(bookRepository, times(1)).findAll();


   }

     @Test
    void createNewBook_shouldBuildCorrectBook_saveAndReturnIt() {

         AddBook dto = new AddBook("Test Book", "Test Author", 19.99, "url.jpg");
         
         ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);


         when(bookRepository.save(bookCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

         Book createdBook = bookService.createNewBook(dto);

         assertThat(createdBook).isNotNull();
         assertThat(createdBook.getId()).isNotNull();
         assertThat(createdBook.getTitle()).isEqualTo(dto.getTitle());
         assertThat(createdBook.getAuthor()).isEqualTo(dto.getAuthor());
         assertThat(createdBook.getPrice()).isEqualTo(dto.getPrice());
         assertThat(createdBook.getBookCoverUrl()).isEqualTo(dto.getBookCoverUrl());



        verify(bookRepository, times(1)).save(any(Book.class));
     }

     @Test
       void getById_whenBookExists_shouldReturnBook() {

         when(bookRepository.findById(testBookId1)).thenReturn(Optional.of(testBook1));

         Book actualBook = bookService.getById(testBookId1);

         assertThat(actualBook).isNotNull();
         assertThat(actualBook.getId()).isEqualTo(testBookId1);
         assertThat(actualBook.getTitle()).isEqualTo(testBook1.getTitle());
         assertThat(actualBook.getAuthor()).isEqualTo(testBook1.getAuthor());

         verify(bookRepository, times(1)).findById(testBookId1);
     }

        @Test
    void getById_whenBookDoesNotExist_shouldThrowException() {

            when(bookRepository.findById(notFoundBookId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> bookService.getById(notFoundBookId));

        verify(bookRepository, times(1)).findById(notFoundBookId);
   }
}

