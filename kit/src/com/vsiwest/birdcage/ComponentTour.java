package com.vsiwest.birdcage;

import quicktime.QTException;
import quicktime.std.comp.ComponentDescription;
import quicktime.std.comp.ComponentIdentifier;

import java.text.MessageFormat;


public class ComponentTour {

    public static void main(String[] args) {
        try {
            QTSessionCheck.check();
            /* use this wildcard to show all components in QT
            */
            ComponentDescription wildcard =
                    new ComponentDescription();
            ComponentIdentifier ci = null;
            System.out.println(("{{{\n#!html\n<table><TR><th>Name</th><th>description</th><th>type<th>subtype</TR>"));
            while ((ci = ComponentIdentifier.find(ci, wildcard)) != null) {
                ComponentDescription cd = ci.getInfo();
                System.out.println(MessageFormat.format("<tr><td>{0}<td>{1}<td>{2}<td>{3}</tr>", cd.getName(), cd.getInformationString(), Integer.toString(cd.getType(), 16), Integer.toString(cd.getSubType(), 16)));
            }

        } catch (QTException qte) {
            qte.printStackTrace();
        }
        System.out.println("</table>");
    }
}
