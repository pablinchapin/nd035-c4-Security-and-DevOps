package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final Logger logger;

	@Autowired
	public OrderController(OrderRepository orderRepository, UserRepository userRepository,
			Logger logger) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.logger = logger;
	}

	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			logger.info("User {} not found, cart can not be added", username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		logger.info("User {} order successfully saved", username);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			logger.info("User {} not found, cart can not be added", username);
			return ResponseEntity.notFound().build();
		}
		logger.info("Retrieving user {} order data", username);
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
