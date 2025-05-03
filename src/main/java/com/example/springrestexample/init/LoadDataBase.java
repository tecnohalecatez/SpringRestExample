package com.example.springrestexample.init;

import com.example.springrestexample.model.Employee;
import com.example.springrestexample.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDataBase {

    private static final Logger log = LoggerFactory.getLogger(LoadDataBase.class);

    @Bean
    public CommandLineRunner initDatabase(EmployeeRepository employeeRepository) {
        return args -> {
            Employee employee1 = new Employee("John Doe", "Software Engineer");
            Employee employee2 = new Employee("Jane Smith", "Project Manager");
            Employee employee3 = new Employee("Rosa Vargas", "Software Engineer");
            Employee employee4 = new Employee("Diana Ramirez", "Project Manager");
            log.info("Preloading: {} ", employeeRepository.save(employee1));
            log.info("Preloading: {} ", employeeRepository.save(employee2));
            log.info("Preloading: {} ", employeeRepository.save(employee3));
            log.info("Preloading: {} ", employeeRepository.save(employee4));
        };
    }
}
