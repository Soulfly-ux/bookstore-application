package bg.softuni.app.web;

import bg.softuni.app.category.model.Category;
import bg.softuni.app.category.service.CategoryService;
import bg.softuni.app.security.AuthenticationDetails;
import bg.softuni.app.user.model.User;
import bg.softuni.app.user.service.UserService;
import bg.softuni.app.web.dto.UserEditRequest;
import bg.softuni.app.web.dto.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {


  private final UserService userService;
  private final CategoryService categoryService;


    @Autowired
    public UserController(UserService userService, CategoryService categoryService) {
        this.userService = userService;

        this.categoryService = categoryService;
    }

    @GetMapping("/my-profile")
    public ModelAndView getProfilePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        User user = userService.getById(authenticationDetails.getUserId());
        List<Category> categoriesList = categoryService.getAllCategories();


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("my-profile");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getEditProfilePage(@PathVariable UUID id) {
        User byId = userService.getById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        modelAndView.addObject("user", byId);
//        modelAndView.addObject("userEditRequest", DtoMapper.mapUserToUserEditRequest(byId));
        modelAndView.addObject("userEditRequest", new UserEditRequest());

        return modelAndView;
    }


   @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(@PathVariable UUID id, @Valid UserEditRequest userEditRequest, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
           User user = userService.getById(id);
           ModelAndView modelAndView = new ModelAndView();
           modelAndView.setViewName("profile");
           modelAndView.addObject("user", user);
           modelAndView.addObject("userEditRequest", userEditRequest);

           return modelAndView;
        }

        userService.editUserProfile(id, userEditRequest);

        return new ModelAndView("redirect:/home");
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        List<User> allUsers = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", allUsers);

        return modelAndView;
    }

    @PutMapping("/{id}/role")
    public String changeUserRole(@PathVariable UUID id) {

        userService.switchUserRole(id);

        return "redirect:/users";
    }

   @DeleteMapping("/{id}/delete")
    public String deleteUser(@PathVariable UUID id) {

        userService.deleteUserById(id);

        return "redirect:/users";
   }

}
