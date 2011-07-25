package com.vsiwest.usecase;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * (c) Copyright 2006 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
final class WindowTabsAction extends AbstractAction {
    WindowTabsAction() {
        super("WindowTabs");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Collection<? extends Actor> actors = UseCaseView.getUseCaseView().actors.values();
        for (Actor actor : actors) {
            Document document = DocumentHelper.createDocument();
            Element canvas = document.addElement("canvas");   //x=178,y=2,width=595,height=480
            Element actorTabBar = new LazTabsView(actor).getElement(canvas);

            Set<UseCase> usecases = actor.usecases;
            for (UseCase useCase : usecases) {
                dupes.clear();
                Element element2 = new LazTabPane(useCase).getElement(actorTabBar);
                finishUsecase(useCase, element2);
            }
            String s = document.asXML();
//            JTabbedPane navTabs = UseCaseView.getUseCaseView().getNavTabs();
//            JTextPane jTextPane = new JTextPane();
//            jTextPane.setText(s);
//            navTabs.add(jTextPane, actor.toString());

            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer;
            try {
                writer = new XMLWriter(new FileOutputStream(UseCaseView.APP_LZX_FILE_LOC + '/' + actor.name + "Tab.lzx"), format);
                writer.write(document);
                writer.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //TODO: verify for a purpose
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //TODO: verify for a purpose
            } catch (IOException e) {
                e.printStackTrace();  //TODO: verify for a purpose
            }
        }


    }

    private final Set<UseCase> dupes = new HashSet<UseCase>();

    private void finishUsecase(UseCase useCase, Element element2) {
        if (dupes.contains(useCase)) return;
        else dupes.add(useCase);
        Set<UseCase> includes = useCase.includes;
        if (!includes.isEmpty()) {
            Element tabBar = new LazTabsView<UseCase>(useCase).getElement(element2);
            for (UseCase aCase : includes) {
                Element element = new LazTabPane(aCase).getElement(tabBar);
                finishUsecase(aCase, element);
            }
        }
    }
}
