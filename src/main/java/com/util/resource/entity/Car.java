package com.util.resource.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long carId;

    @Column(name = "car_type")
    private String type;

    @Column(name = "car_number")
    private String number;

    @ElementCollection
    @CollectionTable(name = "car_images", joinColumns = @JoinColumn(name = "car_id"))
    @MapKeyColumn(name = "car_image_url")
    @Column(name = "car_image_description")
    private Map<String, String> images = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "car_fuel_type")
    private fuelType fuel = fuelType.GASOLINE;

    @Column(name = "car_available")
    private boolean available = true;

    public enum fuelType {
        GASOLINE("휘발유"),
        DIESEL("경유"),
        LPG("LPG"),
        ELECTRIC("전기");

        @Getter
        private final String fuel;
        fuelType(String fuel) {
            this.fuel = fuel;
        }
    }
}
