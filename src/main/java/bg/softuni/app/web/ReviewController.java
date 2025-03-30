package bg.softuni.app.web;


import bg.softuni.app.exception.ReviewNotFoundException;
import bg.softuni.app.review.client.ReviewClient;
import bg.softuni.app.review.client.dto.BookReviewResponse;
import bg.softuni.app.review.client.dto.NewReviewRequest;
import bg.softuni.app.review.service.ReviewService;
import bg.softuni.app.security.AuthenticationDetails;
import bg.softuni.app.user.model.User;
import bg.softuni.app.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

   private final ReviewService reviewService;
   private final UserService userService;

   @Autowired
    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
       this.userService = userService;
   }

    @GetMapping("/{bookId}")
    public ModelAndView getReviewsByBook(@PathVariable UUID bookId, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
         ModelAndView modelAndView = new ModelAndView();
       User userId = userService.getById(authenticationDetails.getUserId());


        List<BookReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);

        if (reviews.isEmpty()) {
           reviews = Collections.emptyList();
        }

        modelAndView.setViewName("reviews-list");
        modelAndView.addObject("bookId", bookId);
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("reviews", reviews);
        modelAndView.addObject("newReviewRequest", new NewReviewRequest());
        System.out.println("Loading reviews for book: " + bookId);
        return modelAndView;
    }

    @PostMapping("/create/{bookId}")
    public String createReview(@PathVariable UUID bookId,@Valid @ModelAttribute("newReviewRequest") NewReviewRequest newReviewRequest, @AuthenticationPrincipal AuthenticationDetails authenticationDetails, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
          redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors());
         redirectAttributes.addFlashAttribute("newReviewRequest", newReviewRequest);
        return "redirect:/reviews/" + newReviewRequest.getBookId();
        }
        User currentUser = userService.getById(authenticationDetails.getUserId());
        newReviewRequest.setBookId(bookId);
        newReviewRequest.setUserId(currentUser.getId());
        reviewService.createReview(newReviewRequest);
        redirectAttributes.addFlashAttribute("success", "Review created successfully");
     return "redirect:/reviews/" + newReviewRequest.getBookId();

   }





    @GetMapping("/user")
    public ModelAndView getReviewsByUser(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        ModelAndView modelAndView = new ModelAndView();
        User currentUser = userService.getById(authenticationDetails.getUserId());

        modelAndView.setViewName("user-reviews");
        modelAndView.addObject("reviews", reviewService.getReviewsByUserId(currentUser.getId()));
        modelAndView.addObject("currentUser", currentUser);

        return modelAndView;
    }

    @GetMapping("/book/{bookId}/average")
    public ModelAndView getAverageRatingByBookId(@PathVariable UUID bookId, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) throws ReviewNotFoundException {

        ModelAndView modelAndView = new ModelAndView();
        User currentUser = userService.getById(authenticationDetails.getUserId());

      double averageRating = reviewService.getAverageRatingByBookId(bookId);

        modelAndView.setViewName("average-rating");
        modelAndView.addObject("averageRating", averageRating);
        modelAndView.addObject("bookId", bookId);
        modelAndView.addObject("currentUser", currentUser);

        return modelAndView;
    }



}
