package com.util.resource.entity;

import com.util.book.entity.RoomBook;
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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roomId;

    @Column(name = "room_name")
    private String name;

    @Column(name = "room_max_capacity")
    private int maxCapacity;

    @ElementCollection
    @Column(name = "equipment_name")
    private List<String> equipment = new ArrayList<>();

    @Column(name = "room_location")
    private String location;

    @Column(name = "room_available")
    private boolean available = true;

    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RoomBook> roomBooks = new ArrayList<>();

    public void setRoomBooks(RoomBook roomBook) {
        roomBooks.add(roomBook);
        if (roomBook.getRoom() != this) {
            roomBook.setRoom(this);
        }
    }
}
