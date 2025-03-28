package bg.softuni.app.review.client;

import bg.softuni.app.review.client.dto.BookReviewResponse;
import bg.softuni.app.review.client.dto.NewReviewRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "book-service", url = "${book.service.url}")
public interface ReviewClient {




         @GetMapping("/test")
    ResponseEntity<String> getHello(@RequestParam(name = "name") String name);


    @PostMapping
    ResponseEntity<BookReviewResponse> createReview(@RequestBody NewReviewRequest newReviewRequest);




     @GetMapping("{bookId}")
     ResponseEntity<List<BookReviewResponse>> getReviewsByBookId(@PathVariable UUID bookId);

}
