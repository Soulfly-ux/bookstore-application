package bg.softuni.app.order;

import bg.softuni.app.book.model.Book;
import bg.softuni.app.book.service.BookService;
import bg.softuni.app.order.model.Order;
import bg.softuni.app.order.model.OrderItem;
import bg.softuni.app.order.model.OrderStatus;
import bg.softuni.app.order.repository.OrderItemRepository;
import bg.softuni.app.order.repository.OrderRepository;
import bg.softuni.app.order.service.OrderService;
import bg.softuni.app.user.model.Country;
import bg.softuni.app.user.model.User;
import bg.softuni.app.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceUTests {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private BookService bookService;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;
    @Captor
    private ArgumentCaptor<OrderItem> orderItemCaptor;


    private User testUser;
    private Book testBook;
    private Order testOrder;
    private OrderItem testOrderItem;
    private UUID testUserId;
    private UUID testBookId;
    private UUID testOrderId;
    private UUID testOrderItemId;


    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testBookId = UUID.randomUUID();
        testOrderId = UUID.randomUUID();
        testOrderItemId = UUID.randomUUID();

        testUser = User.builder()
                .id(testUserId)
                .username("testUser")
                .password("testPassword")
                .email("testEmail")
                .country(Country.BULGARIA)
                .isActive(true)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .firstName("Test")
                .lastName("Testov")
                .build();


        testBook = Book.builder()
                .id(testBookId)
                .title("Test Book")
                .author("Test Author")
                .price(19.99)
                .description("Test Description")
                .bookCoverUrl("url.jpg")
                .votes(12)
                .category(null)
                .build();


        testOrder = Order.builder()
                .id(testOrderId)
                .user(testUser)
               .orderDate(LocalDate.now().minusDays(1))
                .status(OrderStatus.PENDING)
                .totalPrice(0.0)
                .orderItems(new ArrayList<>())
                .build();


        testOrderItem = OrderItem.builder()
                .id(testOrderItemId)
                .order(testOrder)
                .book(testBook)
                .quantity(1)
                .build();
    }

    @Test
    void getOrCreatePendingOrder_whenUserIsNull_shouldThrowException() {


        assertThrows(RuntimeException.class, () -> orderService.getOrCreatePendingOrder(null));

      verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrCreatePendingOrder_whenNoPendingOrderExists_shouldCreateAndReturnNewOrder() {

        when(orderRepository.findByUserAndStatus(testUser, OrderStatus.PENDING)).thenReturn(new ArrayList<>());

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Order> result = orderService.getOrCreatePendingOrder(testUser);

        verify(orderRepository,times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getUser()).isEqualTo(testUser);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(savedOrder.getTotalPrice()).isEqualTo(0.0);
        assertThat(savedOrder.getOrderDate()).isEqualTo(LocalDate.now());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(savedOrder);

//        verify(orderRepository, times(1)).findByUserAndStatus(testUser, OrderStatus.PENDING);
    }

    @Test
    void getOrCreatePendingOrder_whenPendingOrderExists_shouldReturnExistingOrder() {

        List<Order> existingOrders = List.of(testOrder);
        testOrder.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByUserAndStatus(testUser, OrderStatus.PENDING)).thenReturn(existingOrders);


        List<Order> result = orderService.getOrCreatePendingOrder(testUser);

        verify(orderRepository, never()).save(any(Order.class));

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        assertThat(result.get(0).getId()).isEqualTo(testOrderId);



    }

    @Test
    void addOrderItem_whenOrderNotFound_shouldThrowException() {

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.empty());

        assertThat(assertThrows(RuntimeException.class, () -> orderService.addOrderItem(testOrderId, testBookId, 1))).hasMessage("Order not found");

        verify(bookService, never()).getById(any(UUID.class));
        verify(orderRepository, never()).save(any(Order.class));

    }

    @Test
    void addOrderItem_whenBookNotFound_shouldThrowException() {
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrder));
        when(bookService.getById(testBookId)).thenThrow(new RuntimeException("Book not found"));

        assertThat(assertThrows(RuntimeException.class, () -> orderService
                .addOrderItem(testOrderId, testBookId, 1)))
                .hasMessage("Book not found");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void addOrderItem_whenItemIsNew_shouldAddNewItemAndUpdateOrder() {

        int quantity = 2;
        double expectedTotalPrice = testBook.getPrice() * quantity;

        testOrder.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrder));
        when(bookService.getById(testBookId)).thenReturn(testBook);


        orderService.addOrderItem(testOrderId, testBookId, quantity);

        verify(orderRepository,times(1)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getOrderItems()).hasSize(1);
        OrderItem addedItem = savedOrder.getOrderItems().get(0);

        assertThat(addedItem.getBook()).isEqualTo(testBook);
        assertThat(addedItem.getQuantity()).isEqualTo(quantity);
        assertThat(savedOrder.getTotalPrice()).isEqualTo(expectedTotalPrice);
        assertThat(addedItem.getOrder()).isEqualTo(savedOrder);


    }

    @Test
    void addOrderItem_whenItemExists_shouldUpdateQuantityAndUpdateOrder() {

        int initialQuantity = 1;
        int additionalQuantity = 2;
        double expectedTotalPrice = testBook.getPrice() * (initialQuantity + additionalQuantity);

        testOrder.setOrderItems(new ArrayList<>());
        testOrder.getOrderItems().add(testOrderItem);
        testOrderItem.setQuantity(initialQuantity);

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrder));
        when(bookService.getById(testBookId)).thenReturn(testBook);

        orderService.addOrderItem(testOrderId, testBookId, additionalQuantity);

        verify(orderRepository,times(1)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getTotalPrice()).isEqualTo(expectedTotalPrice);
        assertThat(savedOrder.getOrderItems()).hasSize(1);
        OrderItem updatedItem = savedOrder.getOrderItems().get(0);

        assertThat(updatedItem.getQuantity()).isEqualTo(initialQuantity + additionalQuantity);
    }

    @Test
    void getOrderItems_shouldReturnItemsFromRepository() {

        List<OrderItem> mockOrderItems = List.of(testOrderItem);
        when(orderItemRepository.findByOrderId(testOrderId)).thenReturn(mockOrderItems);

        List<OrderItem> result = orderService.getOrderItems(testOrderId);

         assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockOrderItems);

        verify(orderItemRepository, times(1)).findByOrderId(testOrderId);

    }

    @Test
    void updateQuantity_whenItemNotFound_shouldThrowException() {

        when(orderItemRepository.findById(testOrderItemId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.updateQuantity(testOrderItemId, 2));

        verify(orderItemRepository, never()).save(any(OrderItem.class));


    }

    @Test
    void updateQuantity_whenItemFound_shouldUpdateQuantityAndSave() {

        int newQuantity = 3;
        testOrderItem.setQuantity(1);

        when(orderItemRepository.findById(testOrderItemId)).thenReturn(Optional.of(testOrderItem));

        orderService.updateQuantity(testOrderItemId, newQuantity);

        verify(orderItemRepository, times(1)).save(orderItemCaptor.capture());

        OrderItem updatedItem = orderItemCaptor.getValue();

        assertThat(updatedItem.getQuantity()).isEqualTo(newQuantity);
        assertThat(updatedItem.getId()).isEqualTo(testOrderItemId);
        assertThat(updatedItem.getOrder()).isEqualTo(testOrder);
        assertThat(updatedItem.getBook()).isEqualTo(testBook);
    }







}
