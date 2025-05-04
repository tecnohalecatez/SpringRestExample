package com.example.springrestexample.controller;

import com.example.springrestexample.model.Employee;
import com.example.springrestexample.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        if (Optional.ofNullable(id).map(value -> value < 0).orElse(true)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        URI uriEmployee = URI.create("/employees/" + savedEmployee.getId());
        return ResponseEntity.created(uriEmployee).build();
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee employee, @PathVariable Long id) {
        if (Optional.ofNullable(id).map(value -> value < 0).orElse(true)) {
            return ResponseEntity.badRequest().build();
        }
        return employeeRepository.findById(id)
                .map(existingEmployee -> {
                    existingEmployee.setName(employee.getName());
                    existingEmployee.setPosition(employee.getPosition());
                    employeeRepository.save(existingEmployee);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    employeeRepository.save(employee);
                    return ResponseEntity.ok(employee);
                });
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        if (Optional.ofNullable(id).map(value -> value < 0).orElse(true)) {
            return ResponseEntity.badRequest().build();
        }
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
