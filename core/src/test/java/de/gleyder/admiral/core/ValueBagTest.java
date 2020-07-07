package de.gleyder.admiral.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ValueBagTest {

  private final ValueBag bag = new ValueBag();

  @BeforeEach
  void setup() {
    bag.add("integer", 10);
    bag.add("integer", -15);

    bag.add("string", "hallo");
  }

  @Test
  void shouldReturnKeys() {
    assertIterableEquals(List.of("string", "integer"), bag.keySet());
  }

  @Test
  void shouldReturnList() {
    assertIterableEquals(List.of("hallo", List.of(10, -15)), bag.getAll());
  }

  @Test
  void shouldThrowExceptionIfMoreThanOneValueIsPresent() {
    assertThrows(IllegalStateException.class, () -> bag.get("integer"));
  }

  @Test
  void shouldGetString() {
    assertEquals(Optional.of("hallo"), bag.get("string"));
  }

  @Test
  void shouldGetStringAsList() {
    assertEquals(Optional.of(List.of("hallo")), bag.getList("string"));
  }

  @Test
  void shouldGetIntegers() {
    assertIterableEquals(List.of(10, -15), bag.getList("integer").orElseThrow());
  }
}
