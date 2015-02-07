package sc.ript.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TreeList<T> extends AbstractSortedList<T> implements Serializable {

    private static final long serialVersionUID = 6713717513007107315L;

    private final TreeSet<T> set;

    private final ReentrantReadWriteLock setLock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock viewLock = new ReentrantReadWriteLock();

    private transient Iterator<T> setItr;

    private transient ArrayList<T> list;

    public TreeList() {
        set = new TreeSet<>();
    }

    public TreeList(Collection<? extends T> c) {
        set = new TreeSet<>(c);
    }

    public TreeList(Comparator<? super T> c) {
        set = new TreeSet<>(c);
    }

    public TreeList(SortedSet<T> s) {
        set = new TreeSet<>(s);
    }

    @Override
    public int size() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.contains(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.containsAll(c);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return Collections.unmodifiableSortedSet(set).iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public ListIterator<T> listIterator() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            loadList();
            return Collections.unmodifiableList(list).listIterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            if ((index < 0) || (set.size() < index)) {
                throw new IndexOutOfBoundsException();
            }
            loadList();
            return Collections.unmodifiableList(list).listIterator(index);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public T get(int index) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            if ((index < 0) || (set.size() <= index)) {
                throw new IndexOutOfBoundsException();
            }
            loadList(index);
            return list.get(index);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.toArray();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <S> S[] toArray(S[] a) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.toArray(a);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int indexOf(Object o) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            loadList();
            return list.indexOf(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            loadList();
            return list.lastIndexOf(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public T remove(int index) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            if ((index < 0) || (set.size() <= index)) {
                throw new IndexOutOfBoundsException();
            }
        } finally {
            readLock.unlock();
        }

        Lock writeLock = setLock.writeLock();
        writeLock.lock();
        try {
            if ((index < 0) || (set.size() <= index)) {
                throw new IndexOutOfBoundsException();
            }
            loadList(index);
            T o = list.get(index);
            clearView();
            if (!set.remove(o)) {
                throw new IllegalStateException();
            }
            return o;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        Lock writeLock = setLock.writeLock();
        writeLock.lock();
        try {
            clearView();
            return set.remove(o);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Lock writeLock = setLock.writeLock();
        writeLock.lock();
        try {
            clearView();
            return set.removeAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Lock writeLock = setLock.writeLock();
        writeLock.lock();
        try {
            clearView();
            return set.retainAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        Lock writeLock = setLock.writeLock();
        writeLock.lock();
        try {
            clearView();
            set.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Comparator<? super T> comparator() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.comparator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public T first() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.first();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public T last() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return set.last();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean insert(T o) {
        Lock writeLock = setLock.writeLock();
        writeLock.lock();
        try {
            clearView();
            return set.add(o);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean insertAll(Collection<T> c) {
        Lock writeLock = setLock.writeLock();
        writeLock.lock();
        try {
            clearView();
            return set.addAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    private void loadList() {
        Lock readLock = viewLock.readLock();
        readLock.lock();
        try {
            if ((setItr != null) && !setItr.hasNext()) {
                return;
            }
        } finally {
            readLock.unlock();
        }

        Lock writeLock = viewLock.writeLock();
        writeLock.lock();
        try {
            if ((setItr != null) && !setItr.hasNext()) {
                return;
            }
            if (setItr == null) {
                setItr = set.iterator();
            }
            if (list == null) {
                list = new ArrayList<>(set.size());
            }
            while (setItr.hasNext()) {
                list.add(setItr.next());
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void loadList(int index) {
        Lock readLock = viewLock.readLock();
        readLock.lock();
        try {
            if ((setItr != null) && !setItr.hasNext()) {
                return;
            }
        } finally {
            readLock.unlock();
        }

        Lock writeLock = viewLock.writeLock();
        writeLock.lock();
        try {
            if ((setItr != null) && !setItr.hasNext()) {
                return;
            }
            if (setItr == null) {
                setItr = set.iterator();
            }
            if (list == null) {
                list = new ArrayList<>(set.size());
            }
            while (setItr.hasNext() && (list.size() <= index)) {
                list.add(setItr.next());
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void clearView() {
        Lock writeLock = viewLock.writeLock();
        writeLock.lock();
        try {
            setItr = null;
            list = null;
        } finally {
            writeLock.unlock();
        }
    }
}
