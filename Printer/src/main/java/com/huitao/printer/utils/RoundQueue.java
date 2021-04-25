package com.huitao.printer.utils;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * author  : huitao
 * e-mail  : pig.huitao@gmail.com
 * date    : 2021/4/13 14:27
 * desc    :
 * version :
 */
public class RoundQueue<T> implements Serializable {
    private static final long serialVersionUID = -873109114121357176L;
    private T[] queue;
    private int head = 0;
    private int tail = 0;
    private int realSize = 0;

    public RoundQueue(int size) {
        this.queue = (T[]) new Object[size <= 0 ? 500 : size];
    }

    public void addLast(T element) {
        if (this.isFull()) {
            this.removeFirst();
        }

        this.tail = (this.head + this.realSize) % this.queue.length;
        this.queue[this.tail] = element;
        ++this.realSize;
    }

    public T removeFirst() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            T tempLog = this.queue[this.head];
            this.queue[this.head] = null;
            this.head = (this.head + 1) % this.queue.length;
            --this.realSize;
            return tempLog;
        }
    }

    public int realSize() {
        return this.realSize;
    }

    public boolean isEmpty() {
        return this.realSize() == 0;
    }

    public boolean isFull() {
        return this.realSize() == this.queue.length;
    }

    public void clear() {
        while (!this.isEmpty()) {
            this.removeFirst();
        }

    }

    public T get(int index) {
        if (index >= 0 && index < this.realSize) {
            return this.queue[index];
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.realSize);
        }
    }

    public int indexOf(T key) {
        if (key == null) {
            return -1;
        } else {
            for (int index = 0; index <= this.realSize() - 1; ++index) {
                if (key.equals(this.queue[index])) {
                    return index;
                }
            }

            return -1;
        }
    }

    public int getHead() {
        return this.head;
    }

    public int getTail() {
        return this.tail;
    }

    public T getLast() {
        return this.queue[this.tail];
    }

    public T getFirst() {
        return this.queue[this.head];
    }
}
