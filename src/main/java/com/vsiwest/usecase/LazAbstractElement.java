package com.vsiwest.usecase;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public abstract class LazAbstractElement<T> implements LazElement {
    final T data;

    public LazAbstractElement(T data) {
        this.data = data;
    }

    public String getBeanName(Object data) {
        try {
            return String.valueOf(data.getClass().getField("name").get(data));
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //TODO: verify for a purpose
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //TODO: verify for a purpose
        }
        return "default";
    }
}
