package bg.softuni.app.user.model;

import bg.softuni.app.book.model.Book;
import bg.softuni.app.order.model.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String firstName;

    @Column
    private String lastName;

    private String profilePicture;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column
    private Country country;

    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Order> orders;




}
