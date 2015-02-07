package sc.ript.util;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class Sample {

    @Test
    public void test() {
        sc.ript.util.SortedList<String> list = new sc.ript.util.TreeList<>(
                new java.util.Comparator<String>() {

                    @Override
                    public int compare(String o1, String o2) {
                        int cmp = o1.length() - o2.length();
                        if (cmp != 0) {
                            return cmp;
                        }
                        return o1.compareTo(o2);
                    }
                });

        list.insert("hoge");
        list.insert("foo");
        list.insertAll(java.util.Arrays.asList("hoge", "foo", "bar"));

        assertThat(list, isA(java.util.List.class));
        assertThat(list, not(empty()));
        assertThat(list, hasSize(3));
        assertThat(list.get(0), is("bar"));
        assertThat(list.get(1), is("foo"));
        assertThat(list.get(2), is("hoge"));
        assertThat(list.remove(1), is("foo"));
        assertThat(list.get(1), is("hoge"));
        assertThat(list, hasSize(2));
        list.clear();
        assertThat(list, empty());
    }
}
