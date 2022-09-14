package com.example.demo.controllers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class ItemControllerTest {

  @InjectMocks
  private ItemController itemController;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private Logger logger;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  private List<Item> generateItemList(){
    return Arrays.asList(
      Helper.createItem(1L, "Item 1", BigDecimal.valueOf(1.0)),
      Helper.createItem(2L, "Item 2", BigDecimal.valueOf(2.0)),
      Helper.createItem(3L, "Item 3", BigDecimal.valueOf(3.0))
    );
  }

  private void dataFoundAssertions(ResponseEntity<?> responseEntity){
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private void dataNotFoundAssertions(ResponseEntity<?> responseEntity){
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(responseEntity.getBody()).isNull();
  }

  @Test
  public void getItems(){
    when(itemRepository.findAll()).thenReturn(generateItemList());
    final ResponseEntity<List<Item>> responseEntity = itemController.getItems();

    dataFoundAssertions(responseEntity);

    assertThat(responseEntity.getBody()).isNotNull().size().isEqualTo(generateItemList().size());
  }

  @Test
  public void getItemsEmpty(){
    when(itemRepository.findAll()).thenReturn(Collections.emptyList());
    final ResponseEntity<List<Item>> responseEntity = itemController.getItems();

    dataFoundAssertions(responseEntity);

    assertThat(responseEntity.getBody()).isEmpty();
  }

  @Test
  public void getItemById(){
    Item expected = generateItemList().get(0);
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(expected));
    final ResponseEntity<?> responseEntity = itemController.getItemById(expected.getId());

    dataFoundAssertions(responseEntity);

    Item item = (Item) responseEntity.getBody();
    assertThat(item).isNotNull();
    assertThat(item.getId()).isEqualTo(expected.getId());
  }

  @Test
  public void getItemByIdNotFound(){
    Item expected = generateItemList().get(0);
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
    final ResponseEntity<?> responseEntity;
    responseEntity = itemController.getItemById(expected.getId());

    dataNotFoundAssertions(responseEntity);
  }

  @Test
  public void getItemsByName(){
    Item expected = generateItemList().get(0);
    when(itemRepository.findByName(expected.getName())).thenReturn(Arrays.asList(expected));
    final ResponseEntity<?> responseEntity = itemController.getItemsByName(expected.getName());

    dataFoundAssertions(responseEntity);

    assertThat(responseEntity.getBody()).isNotNull().asList().size().isEqualTo(1);
  }

  @Test
  public void getItemsByNameNotFound(){
    Item expected = generateItemList().get(0);
    when(itemRepository.findByName(anyString())).thenReturn(Collections.emptyList());
    final ResponseEntity<?> responseEntity;
    responseEntity = itemController.getItemsByName(expected.getName());

    dataNotFoundAssertions(responseEntity);
  }

}
