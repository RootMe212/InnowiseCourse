package javacore.task1;

import java.util.NoSuchElementException;

/**
 * Create your own realization of LinkedList and implement the following operations: size() -


/**
 * в первой таске ничего не делаешь с null в addFirst(T el) - done remove main in linkedlist - done
 */

public class MyLinkedList<T> implements MyList<T> {

  private static class Node<T> {

    private T el;
    private Node<T> next;
    private Node<T> prev;

    public Node(T el) {
      this.el = el;
    }
  }

  private Node<T> head;
  private Node<T> tail;
  private int size;


  public int size() {
    return size;
  }

  public void addFirst(T el) {
    if (el == null) {
      throw new NullPointerException("Element is null, can't add first");
    }

    Node<T> newNode = new Node<>(el);
    Node<T> oldHead = head;
    newNode.next = oldHead;

    if (oldHead == null) {
      tail = newNode;
    } else {
      oldHead.prev = newNode;
    }

    head = newNode;
    size++;
  }

  public void addLast(T el) {
    if (el == null) {
      throw new NullPointerException("Element is null, can't add last");
    }

    Node<T> newNode = new Node<>(el);
    Node<T> oldTail = tail;
    newNode.prev = oldTail;

    if (tail == null) {
      head = newNode;
    } else {
      oldTail.next = newNode;
    }

    tail = newNode;
    size++;
  }

  public void add(int index, T el) {
    if (el == null) {
      throw new NullPointerException("Cannot add null element");
    }

    if (!isPositionIndex(index)) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    if (index == size) {
      addLast(el);
      return;
    }

    if (index == 0) {
      addFirst(el);
      return;
    }

    Node<T> current = getNode(index);
    Node<T> newNode = new Node<>(el);

    newNode.prev = current.prev;
    newNode.next = current;
    current.prev.next = newNode;
    current.prev = newNode;

    size++;

  }

  private Node<T> getNode(int index) {
    if (!isPositionIndex(index)) {
      throw new IndexOutOfBoundsException("Index: " + index);
    }

    Node<T> current;

    if (index < size / 2) {
      current = head;

      for (int i = 0; i < index; i++) {
        current = current.next;

      }
    } else {
      current = tail;

      for (int i = size - 1; i > index; i--) {
        current = current.prev;
      }
    }
    return current;
  }

  private boolean isPositionIndex(int index) {
    return index >= 0 && index <= size;
  }

  public T getFirst() {
    if (head == null) {
      throw new NoSuchElementException("No such element");
    }

    return head.el;
  }

  public T getLast() {
    if (tail == null) {
      throw new NoSuchElementException("No such element");
    }

    return tail.el;
  }

  public T get(int index) {
    return getNode(index).el;
  }

  public T removeFirst() {
    if (head == null) {
      throw new NoSuchElementException("No such element");
    }
    T element = head.el;
    head = head.next;
    if (head == null) {
      tail = null;
    } else {
      head.prev = null;
    }

    size--;
    return element;
  }

  public T removeLast() {
    if (tail == null) {
      throw new NoSuchElementException("No such element");
    }

    T element = tail.el;
    tail = tail.prev;

    if (tail == null) {
      head = null;
    } else {
      tail.next = null;
    }

    size--;
    return element;
  }

  public T remove(int index) {
    Node<T> current = getNode(index);
    T el = current.el;

    Node<T> prevNode = current.prev;
    Node<T> nextNode = current.next;

    if (prevNode == null) {
      head = nextNode;
    } else {
      prevNode.next = nextNode;
    }

    if (nextNode == null) {
      tail = prevNode;
    } else {
      nextNode.prev = prevNode;
    }

    size--;
    return el;
  }
}

