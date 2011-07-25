package com.vsiwest.usecase;

import org.dom4j.Element;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public class LazTabsView<T> extends LazAbstractElement<T> {
    public LazTabsView(T data) {
        super(data);
    }

    public Element getElement(Element actorTabSliderElement) {
        return actorTabSliderElement.addElement("tabs");
    }
}
