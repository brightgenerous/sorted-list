package sc.ript.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TreeList<T> extends AbstractSortedList<T> implements Serializable, Cloneable {

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
    public List<T> subList(int fromIndex, int toIndex) {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            if ((fromIndex < 0) || (toIndex < fromIndex) || (set.size() < toIndex)) {
                throw new IndexOutOfBoundsException();
            }
            loadList(toIndex);
            T from = list.get(fromIndex);
            T to = list.get(toIndex);
            return new SubList<>(set.subSet(from, to));
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
    public TreeList<T> clone() {
        Lock readLock = setLock.readLock();
        readLock.lock();
        try {
            return new TreeList<>(set);
        } finally {
            readLock.unlock();
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

class SubList<T> implements List<T> {

    private final SortedSet<T> set;

    private final ReentrantReadWriteLock viewLock = new ReentrantReadWriteLock();

    private Iterator<T> setItr;

    private List<T> list;

    SubList(SortedSet<T> s) {
        set = s;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableSortedSet(set).iterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        loadList();
        return Collections.unmodifiableList(list).listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        if ((index < 0) || (set.size() < index)) {
            throw new IndexOutOfBoundsException();
        }
        loadList(index);
        return Collections.unmodifiableList(list).listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if ((fromIndex < 0) || (toIndex < fromIndex) || (set.size() < toIndex)) {
            throw new IndexOutOfBoundsException();
        }
        loadList(toIndex);
        T from = list.get(fromIndex);
        T to = list.get(toIndex);
        return new SubList<>(set.subSet(from, to));
    }

    @Override
    public T get(int index) {
        if (set.size() <= index) {
            throw new IndexOutOfBoundsException();
        }
        loadList(index);
        return list.get(index);
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <S> S[] toArray(S[] a) {
        return set.toArray(a);
    }

    @Override
    public int indexOf(Object o) {
        loadList();
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        loadList();
        return list.lastIndexOf(o);
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
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
}
