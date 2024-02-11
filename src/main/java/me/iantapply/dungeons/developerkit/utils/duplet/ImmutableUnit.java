package me.iantapply.dungeons.developerkit.utils.duplet;

import java.util.Objects;
import java.util.function.Function;

public class ImmutableUnit<A> {
    private final A first;

    private ImmutableUnit(A first) {
        this.first = first;
    }

    public static <A, B> Function<ImmutableUnit<A>, ImmutableDuplet<A, B>> mapToDuplet(Function<? super A, ? extends B> mapFirst) {
        return unit -> ImmutableDuplet.of(unit.first, mapFirst.apply(unit.first));
    }

    public <B> ImmutableDuplet<A, B> add(B second) {
        return ImmutableDuplet.of(first, second);
    }

    public <B> ImmutableUnit<B> map(Function<? super A, ? extends B> mapFirst) {
        return new ImmutableUnit<>(mapFirst.apply(first));
    }

    public static <A> ImmutableUnit<A> of(A first) {
        return new ImmutableUnit<>(first);
    }

    public A getFirst() {
        return first;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Unit{");
        sb.append("first=").append(first);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImmutableUnit<?> unit = (ImmutableUnit<?>) o;
        return Objects.equals(first, unit.first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first);
    }
}
