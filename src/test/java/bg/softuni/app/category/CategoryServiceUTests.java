package bg.softuni.app.category;


import bg.softuni.app.book.model.Book;
import bg.softuni.app.book.service.BookService;
import bg.softuni.app.category.model.Category;
import bg.softuni.app.category.repository.CategoryRepository;
import bg.softuni.app.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceUTests {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookService bookService;

    @InjectMocks
    private CategoryService categoryService;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;
    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    private Category testCategory;
    private Book testBook;
    private UUID testCategoryId;
    private UUID testBookId;
    private List<Book> booksInCategory;


    @BeforeEach
    void setUp() {

        testCategoryId = UUID.randomUUID();
        testBookId = UUID.randomUUID();

        booksInCategory = new ArrayList<>();

        testCategory = Category.builder()
                .id(testCategoryId)
                .name("Test Category")
                .book(booksInCategory)
                .build();

        testBook = Book.builder()
                .id(testBookId)
                .title("Test Book")
                .build();

        booksInCategory = List.of(testBook);
    }



    @Test
    void addBookToCategory_whenCategoryNotFound_shouldThrowException() {

        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () -> categoryService.addBookToCategory(testCategoryId, testBookId));

         verify(bookService,never()).getById(any());
         verify(categoryRepository,never()).save(any());
         verify(bookService,never()).saveBook(any());
    }

    @Test
    void addBookToCategory_whenBookNotFound_shouldThrowException() {
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));
        when(bookService.getById(testBookId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> categoryService.addBookToCategory(testCategoryId, testBookId));

        verify(categoryRepository).findById(testCategoryId);
        verify(bookService).getById(testBookId);
        verify(categoryRepository, never()).save(any());
        verify(bookService, never()).saveBook(any());
    }


    @Test
    void removeBookFromCategory_whenCategoryNotFound_shouldThrowException() {

        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> categoryService.removeBookFromCategory(testCategoryId, testBookId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");

        verify(bookService, never()).getById(any());
        verify(categoryRepository, never()).save(any());
        verify(bookService, never()).saveBook(any());
    }

    @Test
    void removeBookFromCategory_whenBookNotFound_shouldThrowException() {

        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));
        when(bookService.getById(testBookId)).thenThrow(new RuntimeException("Book not found")); // Примерно съобщение


        assertThatThrownBy(() -> categoryService.removeBookFromCategory(testCategoryId, testBookId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book not found");

        verify(categoryRepository, never()).save(any());
        verify(bookService, never()).saveBook(any());
    }





    @Test
    void getAllBooksByCategoryId_whenCategoryFound_shouldReturnBooks() {

        Book book1 = Book.builder()
                .id(UUID.randomUUID())
                .title("Book 1")
                .build();

        Book book2 = Book.builder()
                .id(UUID.randomUUID())
                .title("Book 2")
                .build();

      List<Book> booksInCategory = new ArrayList<>(List.of(book1, book2)) ;

      Category testCategory = Category.builder()
              .id(testCategoryId)
              .name("Test Category")
                .book(booksInCategory)
              .build();

        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));

        List<Book> result = categoryService.getAllBooksByCategoryId(testCategoryId);


        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(book1, book2);


        verify(categoryRepository).findById(testCategoryId);
    }

    @Test
    void getAllBooksByCategoryId_whenCategoryNotFound_shouldThrowException() {

        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> categoryService.getAllBooksByCategoryId(testCategoryId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");
    }




    @Test
    void findDefaultCategory_whenDefaultExists_shouldReturnIt() {

        Category defaultCategory = Category.builder().id(UUID.randomUUID()).name("Default").build();
        when(categoryRepository.findFirstByOrderById()).thenReturn(Optional.of(defaultCategory));


        Category result = categoryService.findDefaultCategory();


        assertThat(result).isEqualTo(defaultCategory);
        verify(categoryRepository).findFirstByOrderById();
    }

    @Test
    void findDefaultCategory_whenNoDefaultExists_shouldReturnNewCategory() {

        when(categoryRepository.findFirstByOrderById()).thenReturn(Optional.empty());


        Category result = categoryService.findDefaultCategory();


        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getBook()).isNotNull().isEmpty();

        verify(categoryRepository).findFirstByOrderById();
    }


    // --- Тестове за getAllCategories ---

    @Test
    void getAllCategories_shouldReturnAllFromRepository() {

        List<Category> mockCategories = List.of(
                Category.builder().id(UUID.randomUUID()).name("Cat 1").build(),
                Category.builder().id(UUID.randomUUID()).name("Cat 2").build()
        );
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        List<Category> result = categoryService.getAllCategories();


        assertThat(result).isEqualTo(mockCategories);
        verify(categoryRepository).findAll();
    }




    @Test
    void getById_whenCategoryFound_shouldReturnCategory() {

        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));


        Category result = categoryService.getById(testCategoryId);


        assertThat(result).isEqualTo(testCategory);
        verify(categoryRepository).findById(testCategoryId);
    }

    @Test
    void getById_whenCategoryNotFound_shouldThrowException() {

        UUID nonExistentId = UUID.randomUUID();
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> categoryService.getById(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(nonExistentId);
    }
}

