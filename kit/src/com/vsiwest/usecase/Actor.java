package com.vsiwest.usecase;

import java.util.HashSet;
import java.util.Set;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
public final class Actor {
    public Actor(String id, String name) {
        this.name = name;
    }

    public final String name;

    final Set parents = new HashSet();
    final Set<UseCase> usecases = new HashSet<UseCase>();

    public String toString() {
        return "Actor::" + name;
    }
}
