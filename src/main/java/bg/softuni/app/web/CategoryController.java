package bg.softuni.app.web;

import bg.softuni.app.book.model.Book;
import bg.softuni.app.book.service.BookService;
import bg.softuni.app.category.model.Category;
import bg.softuni.app.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final BookService bookService;



    @Autowired
    public CategoryController(CategoryService categoryService, BookService bookService) {
        this.categoryService = categoryService;
        this.bookService = bookService;
    }


    @GetMapping
    public ModelAndView categoryDetails(){
      List<Category> categoriesList = categoryService.getAllCategories();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("categories");
        modelAndView.addObject("categoriesList", categoriesList);

        return modelAndView;
    }


    @GetMapping("/{categoryId}")
    public ModelAndView getCategoryPage(@PathVariable UUID categoryId) {


        Category category = categoryService.getById(categoryId);
        List<Book> books = categoryService.getAllBooksByCategoryId(categoryId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("category-details");
        modelAndView.addObject("category", category);
        modelAndView.addObject("books", books);

        return modelAndView;



    }

    @GetMapping("/{categoryId}/books")
    public ModelAndView getBookByCategory(@PathVariable UUID categoryId) {


        Category category = categoryService.getById(categoryId);
        List<Book> books = categoryService.getAllBooksByCategoryId(categoryId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("books-by-category");
        modelAndView.addObject("category", category);
        modelAndView.addObject("books", books);

        return modelAndView;



    }


    @PostMapping("{categoryId}/add-book")
    public String addBookToCategory(@PathVariable UUID categoryId, @RequestParam UUID bookId, RedirectAttributes redirectAttributes) {


        categoryService.addBookToCategory(categoryId, bookId);




      redirectAttributes.addAttribute("message", "Book added to category successfully");


        return "redirect:/categories/" ;
    }


    @PostMapping("{categoryId}/remove-book/{bookId}")
    public String removeBookFromCategory(@PathVariable UUID categoryId,@PathVariable UUID bookId) {

        categoryService.removeBookFromCategory(categoryId, bookId);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("message", "Book removed from category successfully");


        return "redirect:/categories" + categoryId;
    }
}
