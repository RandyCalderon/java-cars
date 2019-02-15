package com.cars.restcars;

public class CarNotFoundException extends RuntimeException{

    public CarNotFoundException (Long id) {
        super("Could not find language");
    }
}
