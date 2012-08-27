package com.vsiwest.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public enum SurveyCountries {
    United_States,
    Argentina,
    Australia,
    Austria,
    Belgium,
    Brazil,
    Bulgaria,
    Canada,
    Chile,
    China,
    Colombia,
    Costa_Rica,
    Czech_Republic,
    Denmark,
    Egypt,
    Estonia,
    Finland,
    France,
    Germany,
    Greece,
    Hong_Kong,
    Hungary,
    Iceland,
    India,
    Indonesia,
    Iran__Islamic_Republic_of,
    Ireland,
    Israel,
    Italy,
    Japan,
    Korea__Republic_of,
    Kuwait,
    Latvia,
    Lithuania,
    Luxembourg,
    Malaysia,
    Mexico,
    Netherlands,
    new_Zealand,
    Norway,
    Peru,
    Philippines,
    Poland,
    Portugal,
    Romania,
    Russian_Federation,
    Saudi_Arabia,
    Serbia_And_Montenegro,
    Singapore,
    Slovakia,
    Slovenia,
    South_Africa,
    Spain,
    Sweden,
    Switzerland,
    Taiwan,
    Thailand,
    Turkey,
    Ukraine,
    United_Arab_Emirates,
    United_Kingdom,
    Uruguay,
    Venezuela,
    Viet_Nam,
    None_of_the_above;

    private static final ArrayList<String> displayList = new ArrayList<String>();


    String getDisplayString() {
        String comma_replaced;
        comma_replaced = this.toString().replace("__", ", ");
        return comma_replaced.replace("_", " ");
    }

    static Collection<String> getDisplayList() {

        if (displayList.isEmpty())
            for (SurveyCountries country : values()) {
                displayList.add(country.getDisplayString());
            }
        return displayList;


    }
}
