package me.iantapply.dungeons.developerkit.utils.duplet;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ImmutableQuintet<A, B, C, D, E> {
    private final A first;

    private final B second;

    private final C third;

    private final D fourth;

    private final E fifth;

    private ImmutableQuintet(A first, B second, C third, D fourth, E fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
    }

    public static <A, B, C, D, E> ImmutableQuintet<A, B, C, D, E> of(A first, B second, C third, D fourth, E fifth) {
        return new ImmutableQuintet<>(first, second, third, fourth, fifth);
    }

    public static <A, B, C, D, E, F> Function<ImmutableQuintet<A, B, C, D, E>, Stream<F>> flat(
            Function<? super A, ? extends F> mapFirst,
            Function<? super B, ? extends F> mapSecond,
            Function<? super C, ? extends F> mapThird,
            Function<? super D, ? extends F> mapFourth,
            Function<? super E, ? extends F> mapFifth) {

        return quintet -> quintet.stream(mapFirst, mapSecond, mapThird, mapFourth, mapFifth);
    }

    public <F> Stream<F> stream(Function<? super A, ? extends F> mapFirst,
                                Function<? super B, ? extends F> mapSecond,
                                Function<? super C, ? extends F> mapThird,
                                Function<? super D, ? extends F> mapFourth,
                                Function<? super E, ? extends F> mapFifth) {

        return Stream.of(mapFirst.apply(first),
                         mapSecond.apply(second),
                         mapThird.apply(third),
                         mapFourth.apply(fourth),
                         mapFifth.apply(fifth));
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

    public D getFourth() {
        return fourth;
    }

    public E getFifth() {
        return fifth;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Quintet{");
        sb.append("first=").append(first);
        sb.append(", second=").append(second);
        sb.append(", third=").append(third);
        sb.append(", fourth=").append(fourth);
        sb.append(", fifth=").append(fifth);
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
        ImmutableQuintet<?, ?, ?, ?, ?> quintet = (ImmutableQuintet<?, ?, ?, ?, ?>) o;
        return Objects.equals(first, quintet.first) &&
               Objects.equals(second, quintet.second) &&
               Objects.equals(third, quintet.third) &&
               Objects.equals(fourth, quintet.fourth) &&
               Objects.equals(fifth, quintet.fifth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth, fifth);
    }
}
