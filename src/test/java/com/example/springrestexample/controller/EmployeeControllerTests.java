package com.example.springrestexample.controller;

import com.example.springrestexample.model.Employee;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAllEmployeesWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.getForEntity("/employees", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int employeeCount = documentContext.read("$.length()");
        assertThat(employeeCount).isEqualTo(4);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(1, 2, 3, 4);

        JSONArray names = documentContext.read("$..name");
        assertThat(names).containsExactlyInAnyOrder("John Doe", "Jane Smith", "Rosa Vargas", "Diana Ramirez");
    }

    @Test
    void shouldReturnAnEmployeeById() {
        ResponseEntity<String> response = restTemplate.getForEntity("/employees/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);

        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("John Doe");

        String position = documentContext.read("$.position");
        assertThat(position).isEqualTo("Software Engineer");
    }

    @Test
    void shouldReturnAnEmployeeWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/employees/999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldCreateANewEmployee() {
        Employee employee = new Employee("Juan Diaz", "Software Engineer");
        ResponseEntity<String> response = restTemplate.postForEntity("/employees", employee, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Integer id = documentContext.read("$.id");
        assertThat(id).isNotNull();

        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Juan Diaz");

        String position = documentContext.read("$.position");
        assertThat(position).isEqualTo("Software Engineer");
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnEmployee() {
        Employee employee = new Employee("Elias Blanco", "Software Engineer");
        HttpEntity<Employee> request = new HttpEntity<>(employee);
        ResponseEntity<Void> response = restTemplate.exchange("/employees/1", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/employees/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Integer id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);

        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Elias Blanco");

        String position = documentContext.read("$.position");
        assertThat(position).isEqualTo("Software Engineer");
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnEmployee() {
        ResponseEntity<Void> response = restTemplate.exchange("/employees/1", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/employees/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isBlank();
    }

}