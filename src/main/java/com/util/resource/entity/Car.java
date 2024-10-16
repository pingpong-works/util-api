package com.util.resource.entity;

import com.util.book.entity.CarBook;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @Column(name = "car_name")
    private String name;

    @Column(name = "car_number")
    private String number;

    @ElementCollection
    @CollectionTable(name = "car_images", joinColumns = @JoinColumn(name = "car_id"))
    @MapKeyColumn(name = "car_image_url")
    @Column(name = "car_image_description")
    private Map<String, String> images = new LinkedHashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "car_fuel_type")
    private FuelType fuel = FuelType.GASOLINE;

    @Column(name = "car_available")
    private boolean available = true;

    @OneToMany(mappedBy = "car", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CarBook> carBooks = new ArrayList<>();

    public void setCarBooks(CarBook carBook) {
        carBooks.add(carBook);
        if (carBook.getCar() != this) {
            carBook.setCar(this);
        }
    }

    public enum FuelType {
        GASOLINE("휘발유"),
        DIESEL("경유"),
        LPG("LPG"),
        ELECTRIC("전기");

        @Getter
        private final String fuel;
        FuelType(String fuel) {
            this.fuel = fuel;
        }
    }
}
