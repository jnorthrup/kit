package com.vsiwest.usecase;

import org.dom4j.Element;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public class LazTabSliderView<T> extends LazAbstractElement {

    public LazTabSliderView(Object data) {
        super(data);
    }

    public Element getElement(Element actorTabSliderElement) {
        return actorTabSliderElement.addElement("tabelement").addAttribute("text", getBeanName(data));
    }


}
