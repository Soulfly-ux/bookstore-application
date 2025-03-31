package bg.softuni.app.web;

import bg.softuni.app.book.service.BookService;
import bg.softuni.app.order.model.Order;
import bg.softuni.app.order.model.OrderItem;
import bg.softuni.app.order.service.OrderService;
import bg.softuni.app.security.AuthenticationDetails;
import bg.softuni.app.user.model.User;
import bg.softuni.app.user.service.UserService;
import java.util.List;

import bg.softuni.app.web.dto.AddToCartRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;
    private final BookService bookService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, BookService bookService) {
        this.userService = userService;
        this.orderService = orderService;
        this.bookService = bookService;
    }




          @GetMapping
    public ModelAndView getMyOrders(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
              ModelAndView modelAndView = new ModelAndView();
            User user = userService.getById(authenticationDetails.getUserId());

              Order pendingOrder = orderService.getOrCreatePendingOrder(user).stream()
                      .findFirst()
                      .orElse(null);

              if (pendingOrder != null) {

                  List<OrderItem> orderItems = orderService.getOrderItems(pendingOrder.getId());

                  double totalPrice = orderItems.stream()
                          .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                          .sum();


                  modelAndView.setViewName("cart");
                  modelAndView.addObject("orderItems", orderItems);
                  modelAndView.addObject("totalPrice", String.format("%.2f", totalPrice));

              }else {
                  modelAndView.setViewName("cart");
                  modelAndView.addObject("orderItems", null);
                  modelAndView.addObject("totalPrice", "0.00");
              }
              return modelAndView;
          }



    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@Valid @RequestBody AddToCartRequest addToCartRequest, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        User user = userService.getById(authenticationDetails.getUserId());
        UUID orderId = orderService.getOrCreatePendingOrder(user).get(0).getId();

        orderService.addOrderItem(orderId, addToCartRequest.getBookId(), addToCartRequest.getQuantity());


        return ResponseEntity.ok("Book added to cart");

    }

    // delete

}
