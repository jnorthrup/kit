package com.vsiwest.server.spreadsheet;

import java.util.ArrayList;
import java.util.Collection;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public enum StateProvince {

    AL,
    AK,
    AZ,
    AR,
    CA,
    CO,
    CT,
    DE,
    DC,
    FL,
    GA,
    HI,
    ID,
    IL,
    IN,
    IA,
    KS,
    KY,
    LA,
    ME,
    MD,
    MA,
    MI,
    MN,
    MS,
    MO,
    MT,
    NE,
    NV,
    NH,
    NJ,
    NM,
    NY,
    NC,
    ND,
    OH,
    OK,
    OR,
    PA,
    RI,
    SC,
    SD,
    TN,
    TX,
    UT,
    VT,
    VA,
    WA,
    WV,
    WI,
    WY,
    I_live_outside_the_United_States;

    private static final ArrayList<String> displayList = new ArrayList<String>();


    String getDisplayString() {
        String comma_replaced = this.toString().replace("__", ", ");

        return comma_replaced.replace("_", " ");
    }

    public static Collection<String> getDisplayList() {


        if (displayList.isEmpty())
            for (StateProvince state : values()) displayList.add(state.getDisplayString());
        return displayList;
    }

}
