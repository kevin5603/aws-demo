package com.kevin.aws_demo.controller;

import com.kevin.aws_demo.modal.Employee;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("external")
public class ExternalResource {

  private final List<Employee> employees = Arrays.asList(
      new Employee("kevin", getRandomAge()),
      new Employee("julia", getRandomAge()),
      new Employee("david", getRandomAge()),
      new Employee("scott", getRandomAge()),
      new Employee("liliana", getRandomAge()),
      new Employee("anne", getRandomAge()));

  @GetMapping
  public ResponseEntity<Employee> getEmployeeByName(@RequestParam String name) {
    Optional<Employee> employee = employees.stream()
        .filter(e -> e.getName().equals(name))
        .findAny();
    return employee.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<String> getEmployeeByName2(@RequestParam String name) {
    String s = "{\"access_token\": \"123\", \"expire\": \"456\"}";
    return ResponseEntity.ok(s);
//    Optional<Employee> employee = employees.stream()
//        .filter(e -> e.getName().equals(name))
//        .findAny();
//    return employee.map(ResponseEntity::ok)
//        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // 25 ~ 49
  private int getRandomAge() {
    Random random = new Random();
    return 25 + random.nextInt(25);
  }
}
