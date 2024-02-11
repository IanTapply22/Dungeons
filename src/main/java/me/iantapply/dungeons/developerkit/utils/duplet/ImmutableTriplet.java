package me.iantapply.dungeons.developerkit.utils.duplet;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ImmutableTriplet<A, B, C> {

    private final A first;

    private final B second;

    private final C third;

    private ImmutableTriplet(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <A, B, C, D> Function<ImmutableTriplet<A, B, C>, Stream<D>> flat(
            Function<? super A, ? extends D> mapFirst,
            Function<? super B, ? extends D> mapSecond,
            Function<? super C, ? extends D> mapThird) {

        return triplet -> triplet.stream(mapFirst, mapSecond, mapThird);
    }

    public <D> Stream<D> stream(Function<? super A, ? extends D> mapFirst,
                                Function<? super B, ? extends D> mapSecond,
                                Function<? super C, ? extends D> mapThird) {

        return Stream.of(mapFirst.apply(first), mapSecond.apply(second), mapThird.apply(third));
    }

    public static <A, B, C, D> Function<ImmutableTriplet<A, B, C>, ImmutableQuartet<A, B, C, D>> mapToQuartet(
            TriFunction<? super A, ? super B, ? super C, ? extends D> fun) {

        return triplet -> triplet.add(fun.apply(triplet.first, triplet.second, triplet.third));
    }

    public <D> ImmutableQuartet<A, B, C, D> add(D fourth) {

        return ImmutableQuartet.of(first, second, third, fourth);
    }

    public static <A, B, C> ImmutableTriplet<A, B, C> of(A first, B second, C third) {

        return new ImmutableTriplet<>(first, second, third);
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    public C getThird() {
        return third;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Triplet{");
        sb.append("first=").append(first);
        sb.append(", second=").append(second);
        sb.append(", third=").append(third);
        sb.append('}');
        return sb.toString();
    }
    
    public boolean equals(ImmutableTriplet<A, B, C> other) {
    	return first.equals(other.getFirst())
    			&& second.equals(other.getSecond())
    			&& third.equals(other.getThird());
    }
    
    public boolean equals(Triplet<A, B, C> other) {
    	return first.equals(other.getFirst())
    			&& second.equals(other.getSecond())
    			&& third.equals(other.getThird());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImmutableTriplet<?, ?, ?> triplet = (ImmutableTriplet<?, ?, ?>) o;
        return Objects.equals(first, triplet.first) &&
               Objects.equals(second, triplet.second) &&
               Objects.equals(third, triplet.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}
