package com.example.demo.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Helper {

  private final static Long USER_ID = 2022L;
  private final static String USERNAME = "pablinchapin";
  private final static String PASSWORD = "p4BL*p4$$";

  public static CreateUserRequest createUserRequest(String username, String password){
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername(username);
    userRequest.setPassword(password);
    userRequest.setPasswordConfirmation(password);
    return userRequest;
  }

  public static User createUser(Long id, String username, String password){
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setPassword(password);
    return user;
  }

  public static Item createItem(Long id, String name, BigDecimal price){
    Item item = new Item();
    item.setId(id);
    item.setName(name);
    item.setDescription(name);
    item.setPrice(price);
    return item;
  }

  public static Cart createCart(){
    User user = createUser(USER_ID, USERNAME, PASSWORD);
    Cart cart = new Cart();
    cart.setId(1L);
    cart.setUser(user);

    for(int x=0; x<5; x++){
      cart.addItem(createItem(x+1L, "Item "+(x+1L), BigDecimal.valueOf(100*(x+1.0))));
    }

    user.setCart(cart);
    return cart;
  }

  public static void dataNotFoundAssertions(ResponseEntity<?> responseEntity){
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(responseEntity.getBody()).isNull();
  }

  public static void dataFoundAssertions(ResponseEntity<?> responseEntity){
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
