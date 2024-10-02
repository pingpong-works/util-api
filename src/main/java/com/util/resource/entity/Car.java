package com.util.resource.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "car_fuel_type")
    private fuelType fuel = fuelType.GASOLINE;

    @Column(name = "car_available")
    private boolean available = true;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarImage> images = new ArrayList<>();

    public void addImage(CarImage image) {
        images.add(image);
        image.setCar(this);
    }

    public void removeImage(CarImage image) {
        images.remove(image);
        image.setCar(null);
    }

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
