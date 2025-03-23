package bg.softuni.app.book.model;


import bg.softuni.app.category.model.Category;
import bg.softuni.app.order.model.OrderItem;
import bg.softuni.app.user.model.User;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private double price;

    @Column
    private String description;

    @Column(nullable = false)
    private String bookCoverUrl;

    private int votes;

    private boolean isAvailable;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "book")
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    private Category category;




}






