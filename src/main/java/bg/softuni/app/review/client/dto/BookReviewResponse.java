package bg.softuni.app.review.client.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookReviewResponse {

    private String comment;
    private int rating;
    private String username;
    private LocalDateTime date;
}
