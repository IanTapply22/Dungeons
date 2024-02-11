package me.iantapply.dungeons.dungeons.generation;

public enum Directions {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, 1),
    DOWN(0, -1);

    int x, y;

    Directions(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
