package me.iantapply.dungeons.dungeons.generation;

public enum Type {
    _1x1(1),
    _L1(3),
    _L2(3),
    _2x2(4),
    _2x1(2, 1),
    _3x1(3, 2),
    _4x1(4, 3);

    int distance;
    int amountOfRooms;

    Type(int amountOfRooms) {
        this.amountOfRooms = amountOfRooms;
    }

    Type(int amountOfRooms, int distance) {
        this.amountOfRooms = amountOfRooms;
        this.distance = distance;
    }
}
