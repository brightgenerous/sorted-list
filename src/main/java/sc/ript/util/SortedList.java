package sc.ript.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface SortedList<T> extends List<T> {

    public Comparator<? super T> comparator();

    public T first();

    public T last();

    public boolean insert(T o);

    public boolean insertAll(Collection<T> c);
}
