package com.cars.restcars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public ArrayList<Car> findYear(@PathVariable int year) {
        List<Car> carsByYear = carrepos.findAll();
        for (Car c : carsByYear) {
            if(c.getYear() == year) {
                carrepos.save(c);
            }
        }
        return carrepos;
    }

    @PostMapping("/cars")
    public List<Car> newCar(@RequestBody List<Car> newCars) {
        log.info("Data loaded");
        return carrepos.saveAll(newCars);
    }
}
