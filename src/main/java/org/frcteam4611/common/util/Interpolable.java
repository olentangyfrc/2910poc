package org.frcteam4611.common.util;

public interface Interpolable<T> {
    T interpolate(T other, double t);
}
