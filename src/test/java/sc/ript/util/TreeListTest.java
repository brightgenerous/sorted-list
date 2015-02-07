package sc.ript.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class TreeListTest {

    @RunWith(Enclosed.class)
    public static class Constructor {

        public static class NoArguments {

            @Test
            public void comparable() throws Exception {
                SortedList<String> obj = new TreeList<>();

                assertThat(obj, empty());
            }

            @Test
            public void notComparable() throws Exception {
                SortedList<Object> obj = new TreeList<>();

                assertThat(obj, empty());

                try {
                    obj.insert(new Object());
                    fail();
                } catch (ClassCastException e) {
                    // nop
                }
            }
        }

        public static class ArgumentCollection {

            @Test
            public void comparable() throws Exception {
                String obj1 = "hoge";
                Collection<String> arg;
                {
                    arg = new HashSet<>();
                    arg.add(obj1);
                }
                SortedList<String> obj = new TreeList<>(arg);

                assertThat(obj, hasSize(1));
                assertThat(obj, hasItem(obj1));
            }

            @Test
            public void notComparable() throws Exception {
                Collection<Object> arg;
                {
                    arg = new HashSet<>();
                    arg.add(new Object());
                }

                try {
                    new TreeList<>(arg);
                    fail();
                } catch (ClassCastException e) {
                    // nop
                }
            }

            @Test
            public void isEmpty() throws Exception {
                SortedList<?> obj = new TreeList<>(new HashSet<>());

                assertThat(obj, empty());
            }

            @Test
            public void isNull() throws Exception {
                try {
                    new TreeList<>((Collection<?>) null);
                    fail();
                } catch (NullPointerException e) {
                    // nop
                }
            }
        }

        public static class ArgumentComparator {

            @Test
            public void notNull() throws Exception {
                Comparator<Object> arg = new Comparator<Object>() {

                    @Override
                    public int compare(Object o1, Object o2) {
                        return o1.hashCode() - o2.hashCode();
                    }
                };
                SortedList<Object> obj = new TreeList<>(arg);

                assertThat(obj, empty());
            }

            @Test
            public void isNull() throws Exception {
                SortedList<Object> obj = new TreeList<>(
                        (Comparator<Object>) null);

                assertThat(obj, empty());
            }
        }

        public static class ArgumentSortedSet {

            @Test
            public void notNull() throws Exception {
                String obj1 = "hoge";
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    arg.add(obj1);
                }
                SortedList<String> obj = new TreeList<>(arg);

                assertThat(obj, hasSize(1));
                assertThat(obj, hasItem(obj1));
            }

            @Test
            public void isNull() throws Exception {
                try {
                    new TreeList<>((SortedSet<?>) null);
                    fail();
                } catch (NullPointerException e) {
                    // nop
                }
            }
        }
    }

    public static class MethodSize {

        @Test
        public void isEmpty() throws Exception {
            SortedList<String> obj = new TreeList<>();

            assertThat(obj.size(), is(0));
        }

        @Test
        public void haveSize() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                int size = Math.max(new Random().nextInt(10), 1);
                for (int i = 0; i < size; i++) {
                    arg.add("hoge" + i);
                }
            }
            SortedList<String> obj = new TreeList<>(arg);

            assertThat(obj.size(), is(arg.size()));
        }
    }

    public static class MethodIsEmpty {

        @Test
        public void isTrue() throws Exception {
            assertThat(new TreeList<>().isEmpty(), is(true));
            assertThat(new TreeList<>(new HashSet<>()).isEmpty(), is(true));
            assertThat(new TreeList<>((Comparator<Object>) null).isEmpty(),
                    is(true));
            assertThat(new TreeList<>(new TreeSet<>()).isEmpty(), is(true));
        }
    }

    public static class MethodContains {

        @Test
        public void test() throws Exception {
            SortedSet<String> arg;
            SortedSet<String> others;
            {
                arg = new TreeSet<>();
                others = new TreeSet<>();
                Random random = new Random();
                int size = Math.max(random.nextInt(10), 2);
                for (int i = 0; i < size; i++) {
                    String o = "hoge" + i;
                    if (arg.size() < 1) {
                        arg.add(o);
                    } else if (others.size() < 1) {
                        others.add(o);
                    } else if (random.nextBoolean()) {
                        arg.add(o);
                    } else {
                        others.add(o);
                    }
                }
            }
            SortedList<String> obj = new TreeList<>(arg);

            for (String a : arg) {
                assertThat(obj.contains(a), is(true));
            }
            for (String a : others) {
                assertThat(obj.contains(a), is(false));
            }
        }
    }

    public static class MethodContainsAll {

        @Test
        public void isEmpty() throws Exception {
            SortedList<String> obj = new TreeList<>();
            assertThat(obj.containsAll(new HashSet<>()), is(true));
        }

        @Test
        public void test() throws Exception {
            SortedSet<String> arg;
            SortedSet<String> others;
            SortedSet<String> all;
            SortedSet<String> sub1;
            SortedSet<String> sub2;
            {
                arg = new TreeSet<>();
                others = new TreeSet<>();
                all = new TreeSet<>();
                Random random = new Random();
                int size = Math.max(random.nextInt(10), 4);
                for (int i = 0; i < size; i++) {
                    String o = "hoge" + i;
                    if (arg.size() < 2) {
                        arg.add(o);
                    } else if (others.size() < 2) {
                        others.add(o);
                    } else if (random.nextBoolean()) {
                        arg.add(o);
                    } else {
                        others.add(o);
                    }
                    all.add(o);
                }
                sub1 = new TreeSet<>(arg);
                sub1.remove(sub1.first());
                sub2 = new TreeSet<>(arg);
                sub2.add(others.first());
            }
            SortedList<String> obj = new TreeList<>(arg);

            assertThat(obj.containsAll(arg), is(true));
            assertThat(obj.containsAll(sub1), is(true));
            assertThat(obj.containsAll(sub2), is(false));
            assertThat(obj.containsAll(others), is(false));
            assertThat(obj.containsAll(all), is(false));
        }
    }

    public static class MethodGet {

        @Test
        public void isEmpty() throws Exception {
            SortedList<String> obj = new TreeList<>();

            try {
                obj.get(-1);
            } catch (IndexOutOfBoundsException e) {
                // nop
            }
            try {
                obj.get(0);
            } catch (IndexOutOfBoundsException e) {
                // nop
            }
            try {
                obj.get(1);
            } catch (IndexOutOfBoundsException e) {
                // nop
            }
        }

        @Test
        public void haveSize() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                int size = Math.max(new Random().nextInt(10), 1);
                for (int i = 0; i < size; i++) {
                    arg.add("hoge" + i);
                }
            }
            SortedList<String> obj = new TreeList<>(arg);

            int index = 0;
            for (Object o : arg) {
                assertThat(obj.get(index++), sameInstance(o));
            }
        }

        @Test
        public void boundary() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                int size = Math.max(new Random().nextInt(10), 1);
                for (int i = 0; i < size; i++) {
                    arg.add("hoge" + i);
                }
            }
            SortedList<String> obj = new TreeList<>(arg);

            assertThat(obj.get(0), sameInstance(arg.first()));
            assertThat(obj.get(obj.size() - 1), sameInstance(arg.last()));
            try {
                obj.get(-1);
            } catch (IndexOutOfBoundsException e) {
                // nop
            }
            try {
                obj.get(obj.size());
            } catch (IndexOutOfBoundsException e) {
                // nop
            }
        }
    }

    public static class MethodIterator {

        @Test
        public void isEmpty() throws Exception {
            SortedList<String> obj = new TreeList<>();
            Iterator<String> itr = obj.iterator();

            if (itr.hasNext()) {
                fail();
            }
        }

        @Test
        public void haveSize() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                int size = Math.max(new Random().nextInt(10), 1);
                for (int i = 0; i < size; i++) {
                    arg.add("hoge" + i);
                }
            }
            SortedList<String> obj = new TreeList<>(arg);
            Iterator<String> itr = obj.iterator();

            int index = 0;
            while (itr.hasNext()) {
                Object o = itr.next();
                assertThat(obj.get(index++), sameInstance(o));
                try {
                    itr.remove();
                    fail();
                } catch (UnsupportedOperationException e) {
                    // nop
                }
            }

            assertThat(index, is(obj.size()));
        }
    }

    @RunWith(Enclosed.class)
    public static class MethodListIterator {

        public static class NoArguments {

            @Test
            public void isEmpty() throws Exception {
                SortedList<String> obj = new TreeList<>();
                ListIterator<String> itr = obj.listIterator();

                if (itr.hasNext()) {
                    fail();
                }
            }

            @Test
            public void haveSize() throws Exception {
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = Math.max(new Random().nextInt(10), 1);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                }
                SortedList<String> obj = new TreeList<>(arg);
                ListIterator<String> itr = obj.listIterator();

                int index = 0;
                while (itr.hasNext()) {
                    Object o = itr.next();
                    assertThat(obj.get(index++), sameInstance(o));
                    try {
                        itr.remove();
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                    try {
                        itr.add("foo");
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                    try {
                        itr.set("bar");
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                }

                assertThat(index, is(obj.size()));
            }
        }

        public static class ArgumentInt {

            @Test
            public void zero() throws Exception {
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = new Random().nextInt(10);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                }
                SortedList<String> obj = new TreeList<>(arg);
                ListIterator<String> itr = obj.listIterator(0);

                int index = 0;
                while (itr.hasNext()) {
                    Object o = itr.next();
                    assertThat(obj.get(index++), sameInstance(o));
                    try {
                        itr.remove();
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                    try {
                        itr.add("foo");
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                    try {
                        itr.set("bar");
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                }

                assertThat(index, is(obj.size()));
            }

            @Test
            public void some() throws Exception {
                SortedSet<String> arg;
                int some;
                {
                    Random random = new Random();
                    arg = new TreeSet<>();
                    int size = Math.max(random.nextInt(10), 1);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                    some = Math.max(random.nextInt(size), 1);
                }
                SortedList<String> obj = new TreeList<>(arg);
                ListIterator<String> itr = obj.listIterator(some);

                int index = 0;
                while (itr.hasNext()) {
                    Object o = itr.next();
                    assertThat(obj.get(some + index++), sameInstance(o));
                    try {
                        itr.remove();
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                    try {
                        itr.add("foo");
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                    try {
                        itr.set("bar");
                        fail();
                    } catch (UnsupportedOperationException e) {
                        // nop
                    }
                }

                assertThat(some + index, is(obj.size()));
            }

            @Test
            public void boundary() throws Exception {
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = new Random().nextInt(10);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                }
                SortedList<String> obj = new TreeList<>(arg);

                assertThat(obj.listIterator(0).nextIndex(), is(0));
                assertThat(obj.listIterator(obj.size()).hasNext(), is(false));
                try {
                    obj.listIterator(obj.size() + 1);
                } catch (IndexOutOfBoundsException e) {
                    // nop
                }
                try {
                    obj.listIterator(-1);
                } catch (IndexOutOfBoundsException e) {
                    // nop
                }
            }
        }
    }

    @RunWith(Enclosed.class)
    public static class MethodToArray {

        public static class NoArguments {

            @Test
            public void isEmpty() throws Exception {
                SortedList<String> obj = new TreeList<>();
                Object[] ary = obj.toArray();

                assertThat(ary, emptyArray());
            }

            @Test
            public void haveSize() throws Exception {
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = Math.max(new Random().nextInt(10), 1);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                }
                SortedList<String> obj = new TreeList<>(arg);
                Object[] ary = obj.toArray();

                assertArrayEquals(ary, arg.toArray());
            }
        }

        public static class ArgumentArray {

            @Test
            public void isEmpty() throws Exception {
                Object[] ary1 = new Object[new Random().nextInt(3)];
                SortedList<String> obj = new TreeList<>();
                Object[] ary = obj.toArray(ary1);

                assertThat(ary, sameInstance(ary1));
                for (Object o : ary) {
                    assertThat(o, nullValue());
                }
            }

            @Test
            public void hasSizeWithSmallArray() throws Exception {
                Object[] ary1;
                Object[] ary2;
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = Math.max(new Random().nextInt(10), 1);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                    ary1 = new Object[size - 1];
                    ary2 = arg.toArray(new Object[ary1.length]);
                }
                SortedList<String> obj = new TreeList<>(arg);
                Object[] ary = obj.toArray(ary1);

                assertThat(ary, not(sameInstance(ary1)));
                assertArrayEquals(ary, ary2);
            }

            @Test
            public void hasSizeWithLargeArray() throws Exception {
                Object[] ary1;
                Object[] ary2;
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = Math.max(new Random().nextInt(10), 1);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                    ary1 = new Object[size + new Random().nextInt(3)];
                    ary2 = arg.toArray(new Object[ary1.length]);
                }
                SortedList<String> obj = new TreeList<>(arg);
                Object[] ary = obj.toArray(ary1);

                assertThat(ary, sameInstance(ary1));
                assertArrayEquals(ary, ary2);
            }
        }
    }

    public static class MethodIndexOf {

        @Test
        public void test() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                int size = Math.max(new Random().nextInt(10), 1);
                for (int i = 0; i < size; i++) {
                    arg.add("hoge" + i);
                }
            }
            SortedList<String> obj = new TreeList<>(arg);

            int index = 0;
            for (Object o : arg) {
                assertThat(obj.indexOf(o), is(index++));
            }

            assertThat(obj.indexOf("foo"), is(-1));
            assertThat(obj.indexOf(null), is(-1));
        }
    }

    public static class MethodLastIndexOf {

        @Test
        public void test() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                int size = Math.max(new Random().nextInt(10), 1);
                for (int i = 0; i < size; i++) {
                    arg.add("hoge" + i);
                }
            }
            SortedList<String> obj = new TreeList<>(arg);

            int index = 0;
            for (Object o : arg) {
                assertThat(obj.lastIndexOf(o), is(index++));
            }

            assertThat(obj.lastIndexOf("foo"), is(-1));
            assertThat(obj.lastIndexOf(null), is(-1));
        }
    }

    @RunWith(Enclosed.class)
    public static class MethodRemove {

        public static class ArgumentInt {

            @Test
            public void test() throws Exception {
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = Math.max(new Random().nextInt(10), 1);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                }
                SortedList<String> obj = new TreeList<>(arg);

                List<Integer> list;
                {
                    list = new ArrayList<>();
                    for (int i = 0; i < arg.size(); i++) {
                        list.add(i);
                    }
                }
                Collections.shuffle(list);

                for (int i : list) {
                    i = Math.min(i, obj.size() - 1);
                    Object o = obj.get(i);
                    assertThat(obj.remove(i), is(o));
                }
                assertThat(obj, empty());
            }

            @Test
            public void boundary() throws Exception {
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = new Random().nextInt(10);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                }
                SortedList<String> obj = new TreeList<>(arg);

                try {
                    obj.remove(-1);
                } catch (IndexOutOfBoundsException e) {
                    // nop
                }
                try {
                    obj.remove(obj.size());
                } catch (IndexOutOfBoundsException e) {
                    // nop
                }
            }
        }

        public static class ArgumentT {

            @Test
            public void noComparator() throws Exception {
                SortedSet<String> arg;
                {
                    arg = new TreeSet<>();
                    int size = Math.max(new Random().nextInt(10), 1);
                    for (int i = 0; i < size; i++) {
                        arg.add("hoge" + i);
                    }
                }
                SortedList<String> obj = new TreeList<>(arg);
                List<String> list = new ArrayList<>(arg);
                Collections.shuffle(list);

                for (String o : list) {
                    assertThat(obj.remove(o), is(true));
                    assertThat(obj.remove(o), is(false));
                    assertThat(obj.remove("foo"), is(false));
                    try {
                        obj.remove(null);
                    } catch (NullPointerException e) {
                        // nop
                    }
                }
                assertThat(obj, empty());
            }

            @Test
            public void comparator() throws Exception {
                SortedSet<Object> arg;
                {
                    arg = new TreeSet<>(new Comparator<Object>() {

                        @Override
                        public int compare(Object o1, Object o2) {
                            return 0;
                        }
                    });
                    arg.add(null);
                }
                SortedList<Object> obj = new TreeList<>(arg);

                assertThat(obj.remove(null), is(true));
                assertThat(obj.remove(null), is(false));
            }
        }
    }

    public static class MethodRemoveAll {

        @Test
        public void isEmpty() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                arg.add("hoge");
            }
            SortedList<String> obj = new TreeList<>();
            assertThat(obj.removeAll(new HashSet<>()), is(false));
            assertThat(obj.removeAll(arg), is(false));
        }

        @Test
        public void test() throws Exception {
            SortedSet<String> arg;
            SortedSet<String> others;
            SortedSet<String> all;
            SortedSet<String> sub;
            {
                arg = new TreeSet<>();
                others = new TreeSet<>();
                all = new TreeSet<>();
                Random random = new Random();
                int size = Math.max(random.nextInt(10), 4);
                for (int i = 0; i < size; i++) {
                    String o = "hoge" + i;
                    if (arg.size() < 2) {
                        arg.add(o);
                    } else if (others.size() < 2) {
                        others.add(o);
                    } else if (random.nextBoolean()) {
                        arg.add(o);
                    } else {
                        others.add(o);
                    }
                    all.add(o);
                }
                sub = new TreeSet<>(arg);
                sub.remove(sub.first());
            }
            SortedList<String> obj = new TreeList<>(arg);

            assertThat(obj.removeAll(sub), is(true));
            assertThat(obj,
                    hasSize(greaterThanOrEqualTo(arg.size() - sub.size())));
            int size = obj.size();
            assertThat(obj.removeAll(sub), is(false));
            assertThat(obj, hasSize(size));

            assertThat(obj.removeAll(others), is(false));
            assertThat(obj, hasSize(size));

            assertThat(obj.removeAll(all), is(true));
            assertThat(obj, empty());
            assertThat(obj.removeAll(all), is(false));
        }
    }

    public static class MethodRetainAll {

        @Test
        public void isEmpty() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                arg.add("hoge");
            }
            SortedList<String> obj = new TreeList<>();
            assertThat(obj.retainAll(new HashSet<>()), is(false));
            assertThat(obj.retainAll(arg), is(false));
        }

        @Test
        public void test() throws Exception {
            SortedSet<String> arg;
            SortedSet<String> others;
            SortedSet<String> all;
            SortedSet<String> sub;
            {
                arg = new TreeSet<>();
                others = new TreeSet<>();
                all = new TreeSet<>();
                Random random = new Random();
                int size = Math.max(random.nextInt(10), 4);
                for (int i = 0; i < size; i++) {
                    String o = "hoge" + i;
                    if (arg.size() < 2) {
                        arg.add(o);
                    } else if (others.size() < 2) {
                        others.add(o);
                    } else if (random.nextBoolean()) {
                        arg.add(o);
                    } else {
                        others.add(o);
                    }
                    all.add(o);
                }
                sub = new TreeSet<>(arg);
                sub.remove(sub.first());
            }
            SortedList<String> obj = new TreeList<>(arg);

            assertThat(obj.retainAll(sub), is(true));
            assertThat(obj, hasSize(lessThanOrEqualTo(sub.size())));
            int size = obj.size();
            assertThat(obj.retainAll(sub), is(false));
            assertThat(obj, hasSize(size));

            assertThat(obj.retainAll(others), is(true));
            assertThat(obj, hasSize(lessThanOrEqualTo(others.size())));
            size = obj.size();
            assertThat(obj.retainAll(others), is(false));
            assertThat(obj, hasSize(size));

            assertThat(obj.removeAll(all), is(false));
            assertThat(obj, empty());
        }
    }

    public static class MethodClear {

        @Test
        public void test() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                arg.add("hoge");
            }
            SortedList<String> obj = new TreeList<>();

            obj.clear();
            assertThat(obj, empty());
        }
    }

    public static class MethodComparator {

        @Test
        public void test() throws Exception {
            Comparator<Object> c = new Comparator<Object>() {

                @Override
                public int compare(Object o1, Object o2) {
                    return 0;
                }
            };
            SortedSet<String> arg = new TreeSet<>();
            SortedList<Object> obj = new TreeList<>();
            SortedList<Object> obj1 = new TreeList<>(new HashSet<>());
            SortedList<Object> obj2 = new TreeList<>(c);
            SortedList<String> obj3 = new TreeList<>(arg);

            assertThat(obj.comparator(), nullValue());
            assertThat(obj1.comparator(), nullValue());
            assertThat(obj2.comparator(), sameInstance((Object) c));
            assertThat(obj3.comparator(),
                    sameInstance((Object) arg.comparator()));
        }
    }

    public static class MethodFirst {

        @Test
        public void isEmpty() throws Exception {
            SortedList<Object> obj = new TreeList<>();

            try {
                obj.first();
                fail();
            } catch (NoSuchElementException e) {
                // nop
            }
        }

        @Test
        public void haveSize() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                int size = Math.max(new Random().nextInt(10), 1);
                for (int i = 0; i < size; i++) {
                    arg.add("hoge" + i);
                }
            }
            SortedList<String> obj = new TreeList<>(arg);

            assertThat(obj.first(), sameInstance(arg.first()));
        }
    }

    public static class MethodLast {

        @Test
        public void isEmpty() throws Exception {
            SortedList<Object> obj = new TreeList<>();

            try {
                obj.last();
                fail();
            } catch (NoSuchElementException e) {
                // nop
            }
        }

        @Test
        public void haveSize() throws Exception {
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                int size = Math.max(new Random().nextInt(10), 1);
                for (int i = 0; i < size; i++) {
                    arg.add("hoge" + i);
                }
            }
            SortedList<String> obj = new TreeList<>(arg);

            assertThat(obj.last(), sameInstance(arg.last()));
        }
    }

    public static class MethodInsert {

        @Test
        public void test() throws Exception {
            String obj1 = "hoge1";
            String obj2 = "foo1";
            String obj3 = "bar1";
            SortedSet<String> arg;
            {
                arg = new TreeSet<>();
                arg.add(obj1);
                arg.add(obj2);
                arg.add(obj3);
            }
            SortedList<String> obj = new TreeList<>();

            assertThat(obj.insert(obj1), is(true));
            assertThat(obj.size(), is(1));

            assertThat(obj.insert(obj1), is(false));
            assertThat(obj.size(), is(1));

            assertThat(obj.insert(obj2), is(true));
            assertThat(obj.size(), is(2));

            assertThat(obj.insert(obj3), is(true));
            assertThat(obj.size(), is(3));

            assertThat(obj.insert(obj3), is(false));
            assertThat(obj.size(), is(3));

            assertThat(obj, contains(arg.toArray()));
        }
    }

    public static class MethodInsertAll {
        @Test
        public void test() throws Exception {
            SortedSet<String> arg;
            SortedSet<String> others;
            SortedSet<String> all;
            SortedSet<String> sub;
            {
                arg = new TreeSet<>();
                others = new TreeSet<>();
                all = new TreeSet<>();
                Random random = new Random();
                int size = Math.max(random.nextInt(10), 4);
                for (int i = 0; i < size; i++) {
                    String o = "hoge" + i;
                    if (arg.size() < 2) {
                        arg.add(o);
                    } else if (others.size() < 2) {
                        others.add(o);
                    } else if (random.nextBoolean()) {
                        arg.add(o);
                    } else {
                        others.add(o);
                    }
                    all.add(o);
                }
                sub = new TreeSet<>(arg);
                sub.remove(sub.first());
            }
            SortedList<String> obj = new TreeList<>();

            assertThat(obj.insertAll(sub), is(true));
            assertThat(obj, hasSize(sub.size()));
            assertThat(obj, contains(sub.toArray()));

            assertThat(obj.insertAll(sub), is(false));
            assertThat(obj, hasSize(sub.size()));

            assertThat(obj.insertAll(arg), is(true));
            assertThat(obj, hasSize(arg.size()));
            assertThat(obj, contains(arg.toArray()));

            assertThat(obj.insertAll(others), is(true));
            assertThat(obj, hasSize(all.size()));
            assertThat(obj, contains(all.toArray()));

            assertThat(obj.insertAll(all), is(false));
            assertThat(obj, hasSize(all.size()));
            assertThat(obj, contains(all.toArray()));
        }
    }
}
