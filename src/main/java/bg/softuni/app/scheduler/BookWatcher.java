package bg.softuni.app.scheduler;

import bg.softuni.app.book.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookWatcher {

    public final BookRepository bookRepository;

    @Autowired
    public BookWatcher(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Scheduled(fixedDelay = 10000)
    public void printBookCount() {


        log.info("Book count: {}", bookRepository.count());
    }
}

