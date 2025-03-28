package bg.softuni.app.review.service;

import bg.softuni.app.review.client.ReviewClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewService {

    private final ReviewClient reviewClient;


    @Autowired
    public ReviewService(ReviewClient reviewClient) {
        this.reviewClient = reviewClient;
    }
}
