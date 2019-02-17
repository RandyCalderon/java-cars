package com.cars.restcars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import java.util.stream.*;
import java.util.*;

@Slf4j
@RestController
public class CarController {
    private final CarRepository carrepos;
    private final RabbitTemplate rt;

    public CarController(CarRepository carrepos, RabbitTemplate rt) {
        this.carrepos = carrepos;
        this.rt = rt;
    }

    @GetMapping("/cars")
    public List<Car> all() {
        return carrepos.findAll();
    }

    @GetMapping("/cars/{id}")
    public Car findOne(@PathVariable Long id) {
        return carrepos.findById(id).orElseThrow(() -> new CarNotFoundException(id));
    }

    @GetMapping("/cars/year/{year}")
    public List<Car> findYear(@PathVariable int year) {
       return carrepos.findAll().stream().filter(c -> c.getYear() == year).collect(Collectors.toList());
    }

    @GetMapping("/cars/brand/{brand}")
    public List<Car> findBrand(@PathVariable String brand) {
        CarLog message = new CarLog(" search for " + brand);
        log.info(" search for {}" + brand);
        rt.convertAndSend(RestcarsApplication.MESSAGE_QUEUE, message.toString());
        return carrepos.findAll().stream().filter(c -> c.getBrand().equalsIgnoreCase(brand)).collect(Collectors.toList());
    }

    @PostMapping("/cars/upload")
    public List<Car> newCar(@RequestBody List<Car> newCars) {
        log.info("Data loaded");
        return carrepos.saveAll(newCars);
    }

    @DeleteMapping("/cars/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        CarLog message = new CarLog("{id} data deleted");
        log.info(id + " data deleted");
        rt.convertAndSend(RestcarsApplication.MESSAGE_QUEUE, message.toString());
        carrepos.deleteById(id);
    }

}
