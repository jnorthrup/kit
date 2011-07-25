package com.vsiwest.kit.view;

import com.vsiwest.kit.Kit;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
class InstanceView {
    InstanceView(Rectangle rectangle) {
        try {
            JInternalFrame iframe = new JInternalFrame();
            Kit.prepIFrame(iframe, "Instance", new JScrollPane(new JTree()), rectangle);

        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }
}
