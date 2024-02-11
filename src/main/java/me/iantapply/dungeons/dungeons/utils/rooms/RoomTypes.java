package me.iantapply.dungeons.dungeons.utils.rooms;

import me.iantapply.dungeons.dungeons.generation.Type;
import lombok.Getter;

public enum RoomTypes {
    _1x1(4),
    _2x1(2),
    _2x2(4),
    _3x1(2),
    _4x1(2),
    L(1);

    @Getter int possibleRotations;

    RoomTypes(int possibleRotations) {
        this.possibleRotations = possibleRotations;
    }

    public static RoomTypes getRoomType(Type type) {
        if(type == null) return _1x1;
        switch (type) {
            case _1x1:
            default:
                return _1x1;
            case _2x1:
                return _2x1;
            case _2x2:
                return _2x2;
            case _3x1:
                return _3x1;
            case _4x1:
                return _4x1;
            case _L1:
                case _L2:
                    return L;
        }
    }
}
