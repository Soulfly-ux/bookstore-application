package bg.softuni.app.web;

import bg.softuni.app.category.model.Category;
import bg.softuni.app.category.service.CategoryService;
import bg.softuni.app.order.service.OrderService;
import bg.softuni.app.security.AuthenticationDetails;
import bg.softuni.app.user.model.User;
import bg.softuni.app.user.service.UserService;
import bg.softuni.app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;
    private final CategoryService categoryService;

    public IndexController(UserService userService, OrderService orderService, CategoryService categoryService) {
        this.userService = userService;

        this.categoryService = categoryService;
    }


    @GetMapping("/")
    public ModelAndView getIndexPage() {

        return new ModelAndView("index");
    }


    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");

        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Invalid username or password");
        }



        return modelAndView;
    }


    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }


    @PostMapping("/register")
    public String register(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.registerNewUser(registerRequest);

        return "redirect:/login";
    }





    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {



        User user = userService.getById(authenticationDetails.getUserId());// ще ми трябва ако за да достъпи тази страница потребителя трябва да се е логнал
                                                                            // или да се показва информация за него например името
        List<Category> categoriesList = categoryService.getAllCategories();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("categoriesList", categoriesList);



        return modelAndView;
    }

//    @GetMapping("/cart")
//    public ModelAndView getCartPage() {
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("cart");
//
//        return modelAndView;
//    }




}
