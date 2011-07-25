package com.vsiwest.usecase;

import org.dom4j.Element;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public interface LazElement {
    Element getElement(Element actorTabSliderElement);

    String getBeanName(Object data);
}
