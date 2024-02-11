package me.iantapply.dungeons.developerkit.utils.duplet;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
