package com.vsiwest.usecase;

import org.dom4j.Element;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public class LazTabPane extends LazAbstractElement {
    public LazTabPane(UseCase data) {
        super(data);
    }

    public Element getElement(Element actorTabSliderElement) {
        return actorTabSliderElement.addElement("tabpane").addAttribute("text", getBeanName(data));
    }


}
