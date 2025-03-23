package bg.softuni.app.book.repository;

import bg.softuni.app.book.model.Book;
import bg.softuni.app.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    Optional<Book> findById(UUID id);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    Optional<Book> findByTitle(String title);

    List<Book> findAllByCategoryId(UUID id);

    List<Book> findAllByCategory(Category category);
}
