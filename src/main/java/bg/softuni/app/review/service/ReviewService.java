package bg.softuni.app.review.service;

import bg.softuni.app.exception.ReviewNotFoundException;
import bg.softuni.app.review.client.ReviewClient;
import bg.softuni.app.review.client.dto.BookReviewResponse;
import bg.softuni.app.review.client.dto.NewReviewRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReviewService {

    private final ReviewClient reviewClient;


    @Autowired
    public ReviewService(ReviewClient reviewClient) {
        this.reviewClient = reviewClient;
    }

    public List<BookReviewResponse> getReviewsByBookId(UUID bookId) {
        log.info("Fetching reviews for book with ID: {}", bookId);
        return reviewClient.getReviewsByBookId(bookId).getBody();
    }

    public BookReviewResponse createReview(NewReviewRequest newReviewRequest) {
        log.info("Creating review for book with ID: {}", newReviewRequest.getBookId());
        return reviewClient.createReview(newReviewRequest).getBody();
    }

    public List<BookReviewResponse> getReviewsByUserId(UUID userId) {
        log.info("Fetching reviews for user with ID: {}", userId);
        return reviewClient.getReviewsByUserId(userId).getBody();
    }

    public double getAverageRatingByBookId(UUID bookId) throws ReviewNotFoundException {
        log.info("Fetching average rating for book with ID: {}", bookId);

        ResponseEntity<Double> response = reviewClient.getAverageRatingByBookId(bookId);


        if (response == null || response.getBody() == null) {
            log.error("Null response received for book ID: {}", bookId);
            throw new ReviewNotFoundException("No rating data found for book: " + bookId);
        }

        return response.getBody();
    }
}
