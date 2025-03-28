package bg.softuni.app.service.api;

import bg.softuni.app.review.client.ReviewClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class Test implements ApplicationRunner {  // Този клас е тестващ клас, който използва BookClient за да направи заявка към друг REST API;
                                                  // да го изтрия преди предаване на проекта

    private final ReviewClient reviewClient;

    public Test(ReviewClient reviewClient) {
        this.reviewClient = reviewClient;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        ResponseEntity<String> hello = reviewClient.getHello("Svetozar");

        System.out.println(hello.getBody());
    }
}
