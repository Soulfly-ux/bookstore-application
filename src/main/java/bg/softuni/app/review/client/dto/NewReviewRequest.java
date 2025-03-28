package bg.softuni.app.review.client.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NewReviewRequest {

    private UUID bookId;

    private UUID userId;

    private String comment;

    private int rating;
}
