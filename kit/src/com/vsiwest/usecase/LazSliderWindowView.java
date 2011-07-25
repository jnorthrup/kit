package com.vsiwest.usecase;

import org.dom4j.Element;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
class LazSliderWindowView<T> extends LazAbstractElement {

    public LazSliderWindowView(T data) {
        super(data);

    }

    public Element getElement(Element root) {
        Element element = root.addElement("window");
        element.addAttribute("title", getBeanName(data))
                .addAttribute("x", "11")
                .addAttribute("y", "33")
                .addAttribute("width", "775")
                .addAttribute("height", "484")/*
        .addElement("resizelayout")
                .addAttribute("axis", "y")*/;
        Element tabSlider = element.addElement("tabslider");//     x=2,y=2,width=158,height=480


        tabSlider
                .addAttribute("x", "2")
                .addAttribute("y", "2")
                .addAttribute("width", "158")
                .addAttribute("height", "460");
        return tabSlider;
    }


}
