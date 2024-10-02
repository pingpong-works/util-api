package com.util.resource.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class CarImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long carImageId;

    @Column(name = "image_url")
    private String url;

    @Column(name = "image_description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    public CarImage(String url, String description, Car car) {
        this.url = url;
        this.description = description;
        this.car = car;
    }
}
