package me.iantapply.dungeons.developerkit.utils.duplet;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ImmutableQuartet<A, B, C, D> {
    private final A first;

    private final B second;

    private final C third;

    private final D fourth;

    private ImmutableQuartet(A first, B second, C third, D fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static <A, B, C, D, E> Function<ImmutableQuartet<A, B, C, D>, Stream<E>> flat(
            Function<? super A, ? extends E> mapFirst,
            Function<? super B, ? extends E> mapSecond,
            Function<? super C, ? extends E> mapThird,
            Function<? super D, ? extends E> mapFourth) {

        return quartet -> quartet.stream(mapFirst, mapSecond, mapThird, mapFourth);
    }

    public <E> Stream<E> stream(Function<? super A, ? extends E> mapFirst,
                                Function<? super B, ? extends E> mapSecond,
                                Function<? super C, ? extends E> mapThird,
                                Function<? super D, ? extends E> mapFourth) {

        return Stream.of(mapFirst.apply(first),
                         mapSecond.apply(second),
                         mapThird.apply(third),
                         mapFourth.apply(fourth));
    }

    private <E> ImmutableQuintet<A, B, C, D, E> add(E fifth) {
        return ImmutableQuintet.of(first, second, third, fourth, fifth);
    }

    public static <A, B, C, D> ImmutableQuartet<A, B, C, D> of(A first, B second, C third, D fourth) {
        return new ImmutableQuartet<>(first, second, third, fourth);
    }

    public static <A, B, C, D, E> Function<ImmutableQuartet<A, B, C, D>, ImmutableQuintet<A, B, C, D, E>> mapToQuintet(
            QuatroFunction<? super A, ? super B, ? super C, ? super D, ? extends E> function
    ) {
        return quartet -> quartet.add(function.apply(quartet.first, quartet.second, quartet.third, quartet.fourth));
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Quartet{");
        sb.append("first=").append(first);
        sb.append(", second=").append(second);
        sb.append(", third=").append(third);
        sb.append(", fourth=").append(fourth);
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
        ImmutableQuartet<?, ?, ?, ?> quartet = (ImmutableQuartet<?, ?, ?, ?>) o;
        return Objects.equals(first, quartet.first) &&
               Objects.equals(second, quartet.second) &&
               Objects.equals(third, quartet.third) &&
               Objects.equals(fourth, quartet.fourth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }
}