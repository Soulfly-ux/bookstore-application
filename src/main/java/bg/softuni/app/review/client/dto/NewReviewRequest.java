package bg.softuni.app.review.client.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewReviewRequest {

    private UUID bookId;

    private UUID userId;

    private String comment;

    private int rating;


}
