package com.vsiwest.util;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: Jun 19, 2007
 * Time: 10:02:12 AM
 * To change this template use File | Settings | File Templates.
 */
class Triple<T1, T2, T3> {
    private T1 first;
    private T2 second;
    private T3 third;

    public Triple(T1 t1, T2 second, T3 third) {

        this.first = t1;
        this.second = second;
        this.third = third;
    }

    public T1 getFirst() {
        return first;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    public T2 getSecond() {
        return second;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }

    public T3 getThird() {
        return third;
    }

    public void setThird(T3 third) {
        this.third = third;
    }

}
