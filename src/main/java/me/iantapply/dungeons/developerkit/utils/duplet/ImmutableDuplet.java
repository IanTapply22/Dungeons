package me.iantapply.dungeons.developerkit.utils.duplet;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Duplet is a pair of two objects of different types.
 *
 * @param <A> first element of Duplet
 * @param <B> second element of Duplet
 */
public class ImmutableDuplet<A, B> {

    private final A first;

    private final B second;

    private ImmutableDuplet(A first, B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Maps Duplet to Triplet, using provided BiFunction for computing third element of Triplet.
     * Useful in Stream API:
     * <pre>
     * {@code
     *      Stream<Triplet<Integer, Integer, Integer>> s = Stream.of(Tuple.of(1,2))
     *                                .map(Duplet.mapToTriplet((a, b) -> a + b);
     * }
     * </pre>
     *
     * @param fun function taking both Duplet's elements as parameters, and returning third element
     * @param <A> first element
     * @param <B> second element
     * @param <C> third element
     * @return Mapping function Duplet -> Triplet
     */
    public static <A, B, C> Function<ImmutableDuplet<A, B>, ImmutableTriplet<A, B, C>> mapToTriplet(
            BiFunction<? super A, ? super B, ? extends C> fun) {

        return duplet -> duplet.compute(fun);
    }

    /**
     * Transforms Duplet to Triplet, using provided BiFunction
     *
     * @param fun function taking both Duplet's elements as parameters, and returning third element
     * @param <C> third element
     * @return Triplet where first two elements same as in Duplet, and third value is computed by provided function
     */
    public <C> ImmutableTriplet<A, B, C> compute(BiFunction<? super A, ? super B, ? extends C> fun) {

        return add(fun.apply(first, second));
    }

    /**
     * Transforms Duplet to Triplet, using provided value
     * @param third third element for triplet
     * @param <C> third element
     * @return Triplet where first two elements same as in Duplet
     */
    public <C> ImmutableTriplet<A, B, C> add(C third) {
        return ImmutableTriplet.of(first, second, third);
    }

    static <A, B> ImmutableDuplet<A, B> of(A first, B second) {
        return new ImmutableDuplet<>(first, second);
    }

    static <A, B> ImmutableDuplet<A, B> of(Map.Entry<A, B> entry) {
        return new ImmutableDuplet<>(entry.getKey(), entry.getValue());
    }

    public static <A, B, C> Function<ImmutableDuplet<A, B>, Stream<C>> flat(Function<? super A, ? extends C> mapFirst,
                                                                   Function<? super B, ? extends C> mapSecond) {
        return duplet -> duplet.stream(mapFirst, mapSecond);
    }

    public <C> Stream<C> stream(Function<? super A, ? extends C> firstMap,
                                Function<? super B, ? extends C> secondMap) {
        return Stream.of(firstMap.apply(first), secondMap.apply(second));
    }

    public <C> ImmutableDuplet<C, B> mapFirst(Function<? super A, ? extends C> firstMap) {

        return map(firstMap, Function.identity());
    }

    public <C, D> ImmutableDuplet<C, D> map(Function<? super A, ? extends C> firstMap,
                                   Function<? super B, ? extends D> secondMap) {

        return new ImmutableDuplet<>(firstMap.apply(first), secondMap.apply(second));
    }

    public <C> ImmutableDuplet<A, C> mapSecond(Function<? super B, ? extends C> secondMap) {

        return map(Function.identity(), secondMap);
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Duplet{");
        sb.append("first=").append(first);
        sb.append(", second=").append(second);
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
        ImmutableDuplet<?, ?> duplet = (ImmutableDuplet<?, ?>) o;
        return Objects.equals(first, duplet.first) &&
               Objects.equals(second, duplet.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
