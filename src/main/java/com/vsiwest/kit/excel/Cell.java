package com.vsiwest.kit.excel;

import java.io.Serializable;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public interface Cell {
    String toString();

    Serializable getValue();
}
