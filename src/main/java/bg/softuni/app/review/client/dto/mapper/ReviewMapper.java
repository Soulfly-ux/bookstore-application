package bg.softuni.app.review.client.dto.mapper;

import bg.softuni.app.review.client.dto.BookReviewResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ReviewMapper {


    public BookReviewResponse mapReviewResponse(BookReviewResponse review) {
        return BookReviewResponse.builder()

                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }



}