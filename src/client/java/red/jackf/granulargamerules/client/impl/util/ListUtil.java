package red.jackf.granulargamerules.client.impl.util;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface ListUtil {
    static <T> int getIndexOf(List<T> collection, Predicate<T> predicate) {
        for (int i = 0; i < collection.size(); i++) {
            T element = collection.get(i);
            if (predicate.test(element)) {
                return i;
            }
        }

        return -1;
    }

    @Nullable
    static <T> T get(List<T> collection, Predicate<T> predicate) {
        for (T element : collection) {
            if (predicate.test(element)) {
                return element;
            }
        }

        return null;
    }
}
