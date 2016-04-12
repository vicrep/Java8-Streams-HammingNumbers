import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

interface Stream<T> {
    T first();
    Stream<T> rest();
    boolean isEmpty();

    default void forEach(Consumer<? super T> consumer) {
        Stream<T> cell = this;
        while(!cell.isEmpty()) {
            consumer.accept(cell.first());
            cell = cell.rest();
        }
    }

    default Stream<T> map(UnaryOperator<T> fun) {
        if(this.isEmpty()) {
            return this;
        }
        else return new Cons<>(fun.apply(this.first()),() -> this.rest().map(fun));
    }

    default Stream<T> take(int n) {
        if(n > 0 && !this.isEmpty()) {
            return new Cons<>(this.first(), () -> this.rest().take(n - 1));
        }
        else return new Empty<>();
    }
}

class Empty<T> implements Stream<T> {
    public T first() {
        throw new UnsupportedOperationException("Empty stream");
    }
    public Stream<T> rest() {
        throw new UnsupportedOperationException("Empty stream");
    }
    public boolean isEmpty() { return true; }
}

class Cons<T> implements Stream<T> {
    private final T first;
    private final Supplier<Stream<T>> rest;

    Cons(T first, Supplier<Stream<T>> rest) {
        this.first = first;
        this.rest = rest;
    }
    public T first() {
        return this.first;
    }

    public Stream<T> rest() {
        return this.rest.get();
    }
    public boolean isEmpty() {
        return false;
    }
}

class Hamming {
    private static Stream<Integer> merge(Stream<Integer> s1, Stream<Integer> s2) {
        if(s1.first() < s2.first())
            return new Cons<>(s1.first(), () -> merge(s1.rest(), s2));
        else if(s1.first() > s2.first())
            return new Cons<>(s2.first(), () -> merge(s1, s2.rest()));
        else
            return new Cons<>(s1.first(), () -> merge(s1.rest(), s2.rest()));
    }

    private static Stream<Integer> mult(Stream<Integer> s, int n) {
        return s.map(x -> x * n);
    }

    private static Stream<Integer> ham(Stream<Integer> s) {
        return new Cons<>(s.first(),
                () -> merge(mult(ham(s), 2), merge(mult(ham(s), 3), mult(ham(s), 5))));
    }

    static Stream<Integer> init() {
        return ham(new Cons<>(1, Empty::new));
    }
}

public class StreamExample {

    public static void main(String ...args) {
        Hamming.init().take(20).forEach(x -> System.out.print(x + ", "));
        System.out.println();

        // Outputs:
        // 1, 2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 16, 18, 20, 24, 25, 27, 30, 32, 36
    }

}
