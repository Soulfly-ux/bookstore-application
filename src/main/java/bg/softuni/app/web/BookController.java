package bg.softuni.app.web;

import bg.softuni.app.book.model.Book;
import bg.softuni.app.book.service.BookService;
import bg.softuni.app.security.AuthenticationDetails;
import bg.softuni.app.user.model.User;
import bg.softuni.app.user.service.UserService;
import bg.softuni.app.web.dto.AddBook;
import bg.softuni.app.web.dto.BookSearchRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;


    @Autowired
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;

    }


    @GetMapping("/available")
    public ModelAndView getBooksPage() {



        List<Book> books = bookService.getAllBooks();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("availableBooks");
        modelAndView.addObject("books", books);



        return modelAndView;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/register")
    public ModelAndView getBookRegisterPage( @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        User user = userService.getById(authenticationDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bookRegister");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addBook", new AddBook());

        return modelAndView;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String addBook(@Valid AddBook addBook,  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "bookRegister";
        }



     bookService.createNewBook(addBook);

        return "redirect:/home";
    }




//
//    @GetMapping("/my_books")
//    public ModelAndView getMyBooks(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("myBooks");
//
//        return modelAndView;
//    }

//    @PostMapping("/save")
//    public String addBook(@ModelAttribute Book book) {
//
//        bookService.saveBook(book);
//
//        return "redirect:/search";
//    }

    @GetMapping("/search")
    public ModelAndView getSearchPage(String query) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search");
       modelAndView.addObject("searchQuery", new BookSearchRequest());

        return modelAndView;
    }


    @PostMapping("/search")
    public String searchBooks(@Valid @ModelAttribute("searchForm")  ModelAndView modelAndView, BookSearchRequest bookSearchRequest) {


      String query = bookSearchRequest.getQuery();

      List<Book> searchResults = bookService.searchBooks(query);



      searchResults = searchResults.stream().distinct().collect(Collectors.toList());

        modelAndView.addObject("searchQuery", query);
         modelAndView.addObject("searchResults", searchResults);



        return "search";

    }
}
