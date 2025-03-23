package bg.softuni.app.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddBook {

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Author is required")
    private String author;

    @NotNull(message = "The price cannot be empty.")
    @Min(value = 0, message = "The price should be positive.")
    private double price;

    @NotEmpty(message = "Book cover URL is required")
    @URL
    private String bookCoverUrl;




}
