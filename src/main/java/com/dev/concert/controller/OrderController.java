package com.dev.concert.controller;

import com.dev.concert.model.dto.response.OrderResponseDto;
import com.dev.concert.service.OrderService;
import com.dev.concert.service.ShoppingCartService;
import com.dev.concert.service.UserService;
import com.dev.concert.service.mapper.OrderMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderMapper orderMapper,
                           UserService userService,
                           ShoppingCartService shoppingCartService) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public List<OrderResponseDto> getOrderHistory(Authentication authentication) {
        UserDetails details = (UserDetails) authentication.getPrincipal();
        String email = details.getUsername();
        return orderService
                .getOrdersHistory(userService.findByEmail(email).get())
                .stream()
                .map(orderMapper::toDtoFromObject)
                .collect(Collectors.toList());
    }

    @PostMapping("/complete")
    public OrderResponseDto completeOrder(Authentication authentication) {
        UserDetails details = (UserDetails) authentication.getPrincipal();
        String email = details.getUsername();
        return orderMapper.toDtoFromObject(orderService
                .completeOrder(shoppingCartService
                        .getByUser(userService
                        .findByEmail(email).get())));
    }
}
