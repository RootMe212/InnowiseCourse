package task1_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task1.MyLinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyLinkedListTest {
    private MyLinkedList<Integer> list;
    @BeforeEach
    public void setUp() {
        list = new MyLinkedList<>();
    }
    @Test
    void test_size() {
        list.add(0,9);
        list.add(1,5);

        assertEquals(2,list.size());
    }
    @Test
    void test_addFirst() {
        list.addFirst(10);

        assertEquals(1, list.size());
        assertEquals(10, list.getFirst());
    }

    @Test
    void test_addLast() {
        list.addLast(7);
        assertEquals(1, list.size());
        assertEquals(7, list.getLast());
    }

    @Test
    void test_add() {
        list.addFirst(1);
        list.addLast(3);
        list.add(1, 2);

        assertEquals(3, list.size());
        assertEquals(2, list.get(1));
    }

    @Test
    void test_getFirst_getLast() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        assertEquals(1, list.getFirst());
        assertEquals(3, list.getLast());
    }

    @Test
    void test_get() {
        list.addLast(6);
        list.addLast(8);
        list.addLast(9);
        list.addLast(10);

        assertEquals(8, list.get(1));
        assertEquals(9, list.get(2));
    }

    @Test
    void test_removeFirst() {
        list.addLast(1);
        list.addLast(2);

        assertEquals(1, list.removeFirst());
        assertEquals(1, list.size());
        assertEquals(2, list.getFirst());
    }

    @Test
    void test_removeLast() {
        list.addLast(1);
        list.addLast(2);

        assertEquals(2, list.removeLast());
        assertEquals(1, list.size());
        assertEquals(1, list.getLast());
    }

    @Test
    void test_remove() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        assertEquals(2, list.remove(1));
        assertEquals(2, list.size());
        assertEquals(3, list.get(1));
    }


}

