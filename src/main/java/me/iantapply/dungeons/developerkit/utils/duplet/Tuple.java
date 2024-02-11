package me.iantapply.dungeons.developerkit.utils.duplet;

import java.util.ArrayList;

public abstract class Tuple {

    public static <A> Unit<A> of(A first) {
        return Unit.of(first);
    }

    public static <A, B> Duplet<A, B> of(A first, B second) {
        return Duplet.of(first, second);
    }

    public static <A, B, C> Triplet<A, B, C> of(A first, B second, C third) {
        return Triplet.of(first, second, third);
    }

    public static <A, B, C, D> Quartet<A, B, C, D> of(A first, B second, C third, D fourth) {
        return Quartet.of(first, second, third, fourth);
    }
    
    public static <A, B, C, D,E> Quintet<A, B, C, D,E> of(A first, B second, C third, D fourth, E fifth) {
        return Quintet.of(first, second, third, fourth, fifth);
    }
    
    public static <A> ImmutableUnit<A> immutableOf(A first) {
        return ImmutableUnit.of(first);
    }

    public static <A, B> ImmutableDuplet<A, B> immutableOf(A first, B second) {
        return ImmutableDuplet.of(first, second);
    }

    public static <A, B, C> ImmutableTriplet<A, B, C> immutableOf(A first, B second, C third) {
        return ImmutableTriplet.of(first, second, third);
    }

    public static <A, B, C, D> ImmutableQuartet<A, B, C, D> immutableOf(A first, B second, C third, D fourth) {
        return ImmutableQuartet.of(first, second, third, fourth);
    }
    
    public static <A, B, C, D,E> ImmutableQuintet<A, B, C, D,E> immutableOf(A first, B second, C third, D fourth, E fifth) {
        return ImmutableQuintet.of(first, second, third, fourth, fifth);
    }

    public abstract <T> boolean contains(T thing);

    public static <A, B> Tuple correspondingSize(ArrayList<Duplet<A, B>> p) {
        switch (p.size()) {
            case 1: return Tuple.of(p.get(0));
            case 2: return Tuple.of(p.get(0), p.get(1));
            case 3: return Tuple.of(p.get(0), p.get(1), p.get(2));
            case 4: return Tuple.of(p.get(0), p.get(1), p.get(2), p.get(3));
            case 5: return Tuple.of(p.get(0), p.get(1), p.get(2), p.get(3), p.get(4));
            default: return null;
        }
    }
}
