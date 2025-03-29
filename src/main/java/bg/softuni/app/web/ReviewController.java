package bg.softuni.app.web;


import bg.softuni.app.exception.ReviewNotFoundException;
import bg.softuni.app.review.client.ReviewClient;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("book/{bookId}")
    public ModelAndView getReviewsByBook(@PathVariable UUID bookId, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
         ModelAndView modelAndView = new ModelAndView();
        User currentUser = userService.getById(authenticationDetails.getUserId());

        modelAndView.setViewName("reviews-list");
        modelAndView.addObject("reviews", reviewService.getReviewsByBookId(bookId));
        modelAndView.addObject("bookId", bookId);
        modelAndView.addObject("currentUser", currentUser);
        modelAndView.addObject("newReviewRequest", new NewReviewRequest());

        return modelAndView;
    }

    @PostMapping("create")
    public String createReview(@Valid NewReviewRequest newReviewRequest, @AuthenticationPrincipal AuthenticationDetails authenticationDetails, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
        return "redirect:/reviews-list";
        }
        User currenrUser = userService.getById(authenticationDetails.getUserId());
        newReviewRequest.setUserId(currenrUser.getId());
        reviewService.createReview(newReviewRequest);
        redirectAttributes.addFlashAttribute("success", "Review created successfully");
      return "redirect:/reviews/book/" + newReviewRequest.getBookId();
    }

    @GetMapping("user")
    public ModelAndView getReviewsByUser(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        ModelAndView modelAndView = new ModelAndView();
        User currentUser = userService.getById(authenticationDetails.getUserId());

        modelAndView.setViewName("user-reviews");
        modelAndView.addObject("reviews", reviewService.getReviewsByUserId(currentUser.getId()));
        modelAndView.addObject("currentUser", currentUser);

        return modelAndView;
    }

    @GetMapping("book/{bookId}/average")
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
