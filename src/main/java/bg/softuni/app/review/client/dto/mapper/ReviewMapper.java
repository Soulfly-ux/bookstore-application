package bg.softuni.app.review.client.dto.mapper;

import bg.softuni.app.review.client.dto.BookReviewResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ReviewMapper {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public BookReviewResponse mapReviewResponse(BookReviewResponse review) {
        return BookReviewResponse.builder()
                .username(review.getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
//                .date(review.getDate().format(DATE_FORMATTER))
                .build();
    }



}