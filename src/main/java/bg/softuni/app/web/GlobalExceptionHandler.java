package bg.softuni.app.web;

import bg.softuni.app.exception.ReviewNotFoundException;
import bg.softuni.app.exception.UserAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistException.class)
    public String handleUserAlreadyExistException(UserAlreadyExistException e) {
        logger.error("User already exists: ", e);
        return "redirect:/register";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            AccessDeniedException.class,
            MissingRequestValueException.class
    })
    public ModelAndView handleNotFoundException(Exception e) {
        logger.error("Resource not found: ", e);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        modelAndView.addObject("message", "Resource not found");
        return modelAndView;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("Invalid request: ", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        logger.error("An error occurred: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add book to cart: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReviewNotFoundException.class)
    public ModelAndView handleNotFoundException(ReviewNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("review-not-found");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("errorCode", 404);
        return modelAndView;
    }



}
