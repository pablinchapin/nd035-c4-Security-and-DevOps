package com.example.demo.controllers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final Logger logger;

	@Autowired
	public UserController(UserRepository userRepository, CartRepository cartRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder, Logger logger) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.logger = logger;
	}


	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);
		if(!user.isPresent()){
			logger.info("User id {} not found", id);
		}

		return (user.isPresent()) ?
				ResponseEntity.of(user) :
				ResponseEntity.notFound().build();
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) { logger.info("User {} not found", username); }
		return user == null ?
				ResponseEntity.notFound().build() :
				ResponseEntity.ok(user);
	}

	private static boolean validPassword(String password){
		if(password != null){
			String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
			Pattern pattern = Pattern.compile(regex);
			return pattern.matcher(password).matches();
		}
		return false;
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		Cart cart = new Cart();
		Cart savedCart = cartRepository.save(cart);
		user.setCart(savedCart);

		if(createUserRequest.getPassword().equals(createUserRequest.getPasswordConfirmation()) && validPassword(
				createUserRequest.getPasswordConfirmation())){
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			userRepository.save(user);
			logger.info("User {} successfully created", createUserRequest.getUsername());
			return ResponseEntity.ok(user);
		}

		return ResponseEntity.badRequest().build();
	}
	
}
