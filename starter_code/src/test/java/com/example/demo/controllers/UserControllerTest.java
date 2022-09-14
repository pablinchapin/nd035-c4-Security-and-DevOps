package com.example.demo.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class UserControllerTest {

  @InjectMocks
  private UserController userController;
  @Mock
  private UserRepository userRepository;
  @Mock
  private CartRepository cartRepository;
  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock
  private Logger logger;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  private User expectedData(){
    return Helper.createUser(2L, "pablinchapin", "p4BL*p4$$");
  }

  private void dataFoundAssertions(ResponseEntity<?> responseEntity, User expected){
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    User user  = (User) responseEntity.getBody();
    assertThat(user).isNotNull();
    assertThat(user.getId()).isEqualTo(expected.getId());
    assertThat(user.getUsername()).isEqualTo(expected.getUsername());
  }

  private void dataNotFoundAssertions(ResponseEntity<?> responseEntity){
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void userIsCreated(){
    Cart cart = Helper.createCart();
    CreateUserRequest userRequest = Helper.createUserRequest(cart.getUser().getUsername(), cart.getUser().getPassword());

    when(cartRepository.save(any())).thenReturn(cart);
    when(userRepository.save(any())).thenReturn(cart.getUser());

    final ResponseEntity<?> responseEntity = userController.createUser(userRequest);

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    User user = (User) responseEntity.getBody();
    assertThat(user).isNotNull();
    assertThat(user.getUsername()).isEqualTo(cart.getUser().getUsername());
  }

  @Test
  public void userIsNotCreated(){
    Cart cart = Helper.createCart();
    CreateUserRequest userRequest = Helper.createUserRequest(cart.getUser().getUsername(), "willfail");

    when(cartRepository.save(any())).thenReturn(cart);
    when(userRepository.save(any())).thenReturn(cart.getUser());

    final ResponseEntity<?> responseEntity = userController.createUser(userRequest);

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void findById(){
    User expected = expectedData();
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(expected));
    final ResponseEntity<?> responseEntity = userController.findById(expected.getId());

    dataFoundAssertions(responseEntity, expected);
  }

  @Test
  public void findByIdNotFound(){
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
    final ResponseEntity<?> responseEntity = userController.findById(anyLong());

    dataNotFoundAssertions(responseEntity);
  }

  @Test
  public void findByUsername(){
    User expected = expectedData();
    when(userRepository.findByUsername(anyString())).thenReturn(expected);
    final ResponseEntity<?> responseEntity = userController.findByUserName(expected.getUsername());

    dataFoundAssertions(responseEntity, expected);
  }

  @Test
  public void findByUsernameNotFound(){
    when(userRepository.findByUsername(anyString())).thenReturn(null);

    final ResponseEntity<?> responseEntity = userController.findByUserName(anyString());

    dataNotFoundAssertions(responseEntity);
  }

}
