package bg.softuni.app.category.service;

import bg.softuni.app.book.model.Book;
import bg.softuni.app.book.service.BookService;
import bg.softuni.app.category.model.Category;
import bg.softuni.app.category.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookService bookService;


    @Autowired
    public CategoryService(CategoryRepository categoryRepository, BookService bookService) {
        this.categoryRepository = categoryRepository;
        this.bookService = bookService;
    }


    @Transactional
    public void addBookToCategory(UUID categoryId, UUID bookId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Book book = bookService.getById(bookId);


        if (!category.getBook().contains(book)) {
            category.getBook().add(book);
            book.setCategory(category);


            categoryRepository.save(category);
            bookService.saveBook(book);
        }else {
            throw new IllegalStateException("Book is already assigned to this category");
        }

    }

    @Transactional
    public void removeBookFromCategory(UUID categoryId, UUID bookId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Book book = bookService.getById(bookId);

        if (category.getBook().contains(book)) {
            category.getBook().remove(book);
            book.setCategory(null);

            categoryRepository.save(category);
            bookService.saveBook(book);
        }else {

            throw new IllegalStateException("Book is not assigned to this category");
        }
    }

    public List<Book> getAllBooksByCategoryId(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return category.getBook();
    }

    public Category findDefaultCategory() {
        return categoryRepository.findFirstByOrderById()
                .orElse(new Category());
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public Category getById(UUID categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
