package bg.softuni.app.review.client.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BookReviewResponse {

    @NotBlank
    private String comment;

    @Min(1) @Max(5)
    private Integer rating;

    private LocalDateTime createdAt;
}
