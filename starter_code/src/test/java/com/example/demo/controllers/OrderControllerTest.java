package com.example.demo.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.util.Arrays;
import java.util.List;
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
public class OrderControllerTest {

  @InjectMocks
  private OrderController orderController;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Logger logger;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void submit(){
    Cart expected = Helper.createCart();
    User user = expected.getUser();

    when(userRepository.findByUsername(anyString()))
        .thenReturn(user);

    ResponseEntity<?> responseEntity = orderController.submit(user.getUsername());

    Helper.dataFoundAssertions(responseEntity);

    UserOrder userOrder = (UserOrder) responseEntity.getBody();
    assertThat(userOrder).isNotNull();
    assertThat(user.getUsername()).isEqualTo(userOrder.getUser().getUsername());
    assertThat(expected.getItems().size()).isEqualTo(userOrder.getItems().size());
  }

  @Test
  public void submitUserNotFound(){
    when(userRepository.findByUsername(anyString()))
        .thenReturn(null);

    ResponseEntity<?> responseEntity = orderController.submit(anyString());

    Helper.dataNotFoundAssertions(responseEntity);
  }

  @Test
  public void getOrdersForUser(){
    Cart expected = Helper.createCart();
    User user = expected.getUser();

    when(userRepository.findByUsername(anyString()))
        .thenReturn(user);
    ResponseEntity<?> responseEntity = orderController.submit(user.getUsername());
    UserOrder userOrder = (UserOrder) responseEntity.getBody();
    List<UserOrder> userOrderList = Arrays.asList(userOrder);

    when(orderRepository.findByUser(any()))
        .thenReturn(userOrderList);

    ResponseEntity<?> response = orderController.getOrdersForUser(user.getUsername());

    Helper.dataFoundAssertions(response);
    List<UserOrder> userOrderListFromResponse = (List<UserOrder>) response.getBody();
    assertThat(userOrderListFromResponse).isNotNull();
    assertThat(userOrderList.get(0).getItems().size()).isEqualTo(userOrderListFromResponse.get(0).getItems().size());
    assertThat(userOrderList.get(0).getTotal()).isEqualTo(userOrderListFromResponse.get(0).getTotal());
  }

  @Test
  public void getOrdersForUserUserNotFound(){
    when(userRepository.findByUsername(anyString()))
        .thenReturn(null);

    ResponseEntity<?> responseEntity = orderController.getOrdersForUser(anyString());

    Helper.dataNotFoundAssertions(responseEntity);
  }

}
