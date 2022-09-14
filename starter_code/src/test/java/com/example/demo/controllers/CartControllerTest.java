package com.example.demo.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class CartControllerTest {

  @InjectMocks
  private CartController cartController;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Logger logger;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  private ModifyCartRequest modifyCartRequest(){
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setUsername("pablinchapin");
    modifyCartRequest.setItemId(1L);
    modifyCartRequest.setQuantity(1);
    return modifyCartRequest;
  }

  private void noErrorsAssertions(ResponseEntity<?> responseEntity, Cart expected){
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    Cart cart = (Cart) responseEntity.getBody();

    assertThat(cart).isNotNull();
    assertThat(expected.getItems().size()).isEqualTo(cart.getItems().size());
  }

  private void dataNotFoundAssertions(ResponseEntity<?> responseEntity){
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void addToCart(){
    Cart expected = Helper.createCart();
    User user = expected.getUser();

    when(userRepository.findByUsername(anyString()))
        .thenReturn(user);
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(expected.getItems().get(0)));

    ResponseEntity<?> responseEntity = cartController.addToCart(modifyCartRequest());

    noErrorsAssertions(responseEntity, expected);
  }

  @Test
  public void addToCartUserNotFound(){
    when(userRepository.findByUsername(anyString()))
        .thenReturn(null);

    ResponseEntity<?> responseEntity = cartController.addToCart(modifyCartRequest());

    dataNotFoundAssertions(responseEntity);
  }

  @Test
  public void addToCartItemNotFound(){
    Cart expected = Helper.createCart();
    User user = expected.getUser();

    when(userRepository.findByUsername(anyString()))
        .thenReturn(user);
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    ResponseEntity<?> responseEntity = cartController.addToCart(modifyCartRequest());

    dataNotFoundAssertions(responseEntity);
  }

  @Test
  public void removeFromCart(){
    Cart expected = Helper.createCart();
    User user = expected.getUser();

    when(userRepository.findByUsername(anyString()))
        .thenReturn(user);
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(expected.getItems().get(0)));

    ResponseEntity<?> responseEntity = cartController.removeFromCart(modifyCartRequest());

    noErrorsAssertions(responseEntity, expected);

  }

  @Test
  public void removeFromCartUserNotFound(){
    when(userRepository.findByUsername(anyString()))
        .thenReturn(null);

    ResponseEntity<?> responseEntity = cartController.removeFromCart(modifyCartRequest());

    dataNotFoundAssertions(responseEntity);
  }

  @Test
  public void removeFromCartItemNotFound(){
    Cart expected = Helper.createCart();
    User user = expected.getUser();

    when(userRepository.findByUsername(anyString()))
        .thenReturn(user);
    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    ResponseEntity<?> responseEntity = cartController.removeFromCart(modifyCartRequest());

    dataNotFoundAssertions(responseEntity);
  }

}
