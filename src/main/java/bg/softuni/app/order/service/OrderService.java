package bg.softuni.app.order.service;

import bg.softuni.app.book.model.Book;
import bg.softuni.app.book.service.BookService;
import bg.softuni.app.order.model.Order;
import bg.softuni.app.order.model.OrderItem;
import bg.softuni.app.order.model.OrderStatus;
import bg.softuni.app.order.repository.OrderItemRepository;
import bg.softuni.app.order.repository.OrderRepository;
import bg.softuni.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;
    private final BookService bookService;

    private List<OrderItem> orderItems;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookService = bookService;
    }



    public List<Order>  getOrCreatePendingOrder(User user) {
        if(user == null) {
            throw new RuntimeException("User not found");

        }

        List<Order> pendingOrders = orderRepository.findByUserAndStatus(user, OrderStatus.PENDING);


        if (pendingOrders.isEmpty()) {
            Order newoOrder = createOrderForUser(user);
            orderRepository.save(newoOrder);
            return List.of(newoOrder);
        }

        return pendingOrders;
    }




    private Order createOrderForUser(User user) {

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .orderDate(LocalDate.now())
                .status(OrderStatus.PENDING)
                .totalPrice(0.0)
                .orderItems(new ArrayList<>())
                .build();

        return order;
    }



    @Transactional
    public void addOrderItem(UUID orderId, UUID bookId, int quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Book book = bookService.getById(bookId);



        Optional<OrderItem> existingOrderItem = order.getOrderItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        if (existingOrderItem.isPresent()) {
            OrderItem orderItem = existingOrderItem.get();
            orderItem.setQuantity(orderItem.getQuantity() + quantity);
        } else {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(quantity);
            order.getOrderItems().add(item);
        }
        updateTotalPrice(order);

        orderRepository.save(order);
    }

    private void updateTotalPrice(Order order) {
        double totalPrice = order.getOrderItems().stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();



        order.setTotalPrice(totalPrice);
    }



//    public List<Order> getAllOrders() {
//        return orderRepository.findAll();
//    }
//
//    public void completeOrder(UUID id) {
//        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
//
//        order.setStatus(OrderStatus.COMPLETED);
//
//        orderRepository.save(order);
//    }



//    public void cancelOrder(UUID id) {
//        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
//
//        order.setStatus(OrderStatus.CANCELLED);
//
//        orderRepository.save(order);
//    }


    public List<OrderItem> getOrderItems(UUID orderId) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }

       List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        items.forEach(item -> System.out.println("OrderItem: " + item.getId() + ", Book: " + item.getBook().getTitle()));;

        return items;
    }








//    public void removeItemFromOrder(UUID id) {
//
//        orderItemRepository.deleteById(id);
//    }
//
//
//    public void buyItems(UUID id) {
//        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
//        order.setStatus(OrderStatus.COMPLETED);
//
//        orderRepository.save(order);
//    }

//    public boolean hasOrderItems(UUID id) {
//        return !orderItemRepository.existsById(id);
//    }



    public void updateQuantity(UUID id, int quantity) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        item.setQuantity(quantity);

        orderItemRepository.save(item);
    }



//
//    private void validateOrderItem(Book book, int quantity) {
//        if (quantity <= 0) {
//            throw new RuntimeException("Quantity must be greater than 0");
//        }
//
//        if (!book.isAvailable()) {
//            throw new RuntimeException("Book not found");
//        }
//
//    }


//    public Order getOrderById(UUID orderId) {
//      return   orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
//    }
}
