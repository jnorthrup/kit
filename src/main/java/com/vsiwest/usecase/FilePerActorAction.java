package com.vsiwest.usecase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * (c) Copyright 2011 vsiwest, Inc.  All rights reserved.
 * Confidential Information.  Do not copy or distribute without express written permission.
 */
final class FilePerActorAction extends AbstractAction {


    public FilePerActorAction() {
        super("com.vsiwest.usecase.FilePerActorAction");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Collection<? extends Actor> actors = UseCaseView.useCaseView.actors
                .values();

        for (Actor actor : actors) {
            handleActor(actor);
        }
    }

    private void handleActor(Actor actor) {
        String s = "<canvas><resizelayout axis='y'/>\n";
        s += MessageFormat.format("<window y=''10'' x=''{0}''  height=''500''  width=''{1}'' title=''{2}'' resizable=''true'' closeable=''true''>    " +
                "    <resizelayout axis=''y''/>\n" +
                "<tabslider width=''{1}'' x=''1'' y=''1'' options=''releasetolayout'' spacing=''2'' '>",
                1, UseCaseView.WIN_WIDTH, actor.name);
        for (UseCase useCase : actor.usecases)
            s += handleUsecase(useCase);

        s += "</tabslider></window></canvas>";

        dupes.clear();
        UseCaseView.updateView(s, actor.name + ".lzx");

    }

    private final Set<UseCase> dupes = new HashSet<UseCase>();


    private String handleUsecase(UseCase useCase) {
        String x = "";
        Set<UseCase> includes = useCase.includes;
        if (dupes.contains(useCase)) return x;
        dupes.add(useCase);
        for (UseCase aCase : includes)
            x += "width='" + UseCaseView.WIN_WIDTH + "' options='releasetolayout' > " + handleUsecase(aCase) + '\n';
        String z = "<tabelement width='" + UseCaseView.WIN_WIDTH + "' options='releasetolayout'  text='" + useCase.getName() + "'>\n";
        if (x.length() > 0)
            z += "<resizelayout axis='y'/>\n" + x;
        z += "</tabelement>\n";
        return z;
    }
}
