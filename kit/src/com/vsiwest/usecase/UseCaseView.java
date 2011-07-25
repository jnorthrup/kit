package com.vsiwest.usecase;

import com.vsiwest.kit.Kit;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;


/**
 * com.vsiwest.usecase.UseCase import scanner
 * <p/>
 * place ../test.xmi in /tmp, and run
 *
 * @author James Northrup - Glamdring Incorporated Enterprises
 * @version 0.00-pre-unit-test
 * @license binary storage of sourcecode required to compile
 * <p/>
 * $Log: UseCaseView.java,v $
 * Revision 1.1  2007/01/04 02:41:40  jim
 * the vsiwest IT GUI with incorporated laszlo generators and some possible future birdcage mods.
 */
public final class UseCaseView extends JScrollPane {
    private static final String ASSOCIATIONSXPATH = "/XMI/XMI.content/UML:Model/UML:Namespace.ownedElement/UML:Association";
    private static final String ACTORS_XPATH = "/XMI/XMI.content/UML:Model/UML:Namespace.ownedElement/UML:Actor";
    private static final String EXTENSIONSXPATH = "/XMI/XMI.content/UML:Model/UML:Namespace.ownedElement/UML:Extend";
    private static final String INCLUDESXPATH = "/XMI/XMI.content/UML:Model/UML:Namespace.ownedElement/UML:Include";
    private static final String USECASESXPATH = "/XMI/XMI.content/UML:Model/UML:Namespace.ownedElement/UML:UseCase";
    private static final String UCDIAGRAMS = "//UML:SimpleSemanticModelElement[@typeInfo='UseCaseDiagram']/../../.";
    private static final int winWidth = 450;
    private static final int winStart = 10;
    private static final String ADX_LZX = "adx.lzx";
    public static final String APP_LZX_FILE_LOC = "/Applications/olzx/Server/lps-3.3/demos/dashboard/";
    public static final String UCIMPORT_ACTORS_XML = "_ucimport.actors.xml";
    private DefaultMutableTreeNode root;
//    private static JFrame frame;

    public static void main(String[] as) {
//        final JDesktopPane desktop = new JDesktopPane();

//        frame = new JFrame("MainGen");

//        JPanel panel = new JPanel(new BorderLayout());

//        panel.add(desktop, BorderLayout.CENTER);
//        frame.setContentPane(new UseCaseView());
//        panel.doLayout();
//        desktop.doLayout();

        Dimension dimension;
        dimension = new Dimension(800, 600);
//        frame.setMinimumSize(dimension);
//        frame.setSize(dimension);
        JMenuBar jMenuBar = new JMenuBar();
//        frame.setJMenuBar(jMenuBar);
        JMenu fileMenu = new JMenu("File");
        jMenuBar.add(fileMenu);


        fileMenu.add(new LoadXMIAction());

        //  fileMenu.add(new XStreamAction());

        fileMenu.add(new SlidingTabsAction());

        fileMenu.add(new FilePerActorAction());
        fileMenu.add(new WindowAction());
        fileMenu.add(new WindowTabsAction());
        fileMenu.add(new PoserAction());

//        frame.setVisible(true);
        try {
            XStream xs = new XStream(new DomDriver());

            Object[] arr;
            //cheap hack -- load a bogus xml com.vsiwest.dws.doc and parse no relevant tags.

            arr = (Object[]) xs.fromXML(new FileReader(UCIMPORT_ACTORS_XML));
            useCaseView = new UseCaseView(new File(UCIMPORT_ACTORS_XML));
            useCaseView.actors = (Map<Object, Actor>) arr[0];
            useCaseView.usecases = (Map<Object, UseCase>) arr[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //TODO: verify for a purpose
        }
    }


    public void setActors(Map<Object, Actor> actors) {
        this.actors = actors;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Map<?, ? extends UseCase> getUsecases() {
        return usecases;
    }

    public void setUsecases(Map<Object, UseCase> usecases) {
        this.usecases = usecases;
    }

    public static UseCaseView getUseCaseView() {
        return useCaseView;
    }

    public static void setUseCaseView(UseCaseView useCaseView) {
        UseCaseView.useCaseView = useCaseView;
    }

    private final JTree usecaseTree = new JTree();
    Map<Object, Actor> actors = new TreeMap<Object, Actor>();
    public Map<Object, UseCase> usecases = new TreeMap<Object, UseCase>();
    private File file;
    //    JTabbedPane navTabs;
    static UseCaseView useCaseView;

    public UseCaseView() {
        super();
        file = new File("_ucimport.actors.xml");
        init();

    }

    private UseCaseView(File file) {

        this.file = file;
        init();
    }

    private void init() {
        useCaseView = this;
        this.setViewportView(usecaseTree);
        root = initTreeView();

        Menu fileMenu = new Menu("UcImport");

        LoadXMIAction loadXMIAction = new LoadXMIAction();

        SlidingTabsAction slidingTabsAction = new SlidingTabsAction();
        FilePerActorAction filePerActorAction = new FilePerActorAction();
        WindowAction windowAction = new WindowAction();
        WindowTabsAction windowTabsAction = new WindowTabsAction();
        PoserAction poserAction = new PoserAction();

        if (file.exists())
            inject(file);
    }

    public void inject(File event) {
        Collection<UseCase> dupes;

        SAXReader reader = new SAXReader();

        Document document = null;
        try {
            document = reader.read(event);
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        assert document != null;
        List<? extends Object> actorsElements = document.selectNodes(ACTORS_XPATH);


        for (Object actorsElement : actorsElements) {
            Element element = (Element) actorsElement;
            String id = element.attributeValue("xmi.id");
            String name = element.attributeValue("name");
            Actor actor = new Actor(id, name);
            actors.put(id, actor);
        }

        List<? extends Object> usecaseElements = document.selectNodes(USECASESXPATH);

        for (Object usecaseElement : usecaseElements) {
            Element element = (Element) usecaseElement;
            String id = element.attributeValue("xmi.id");
            String name = element.attributeValue("name");

            UseCase usecase;
            usecase = new UseCase(id, name);
            usecases.put(id, usecase);
        }


        List<? extends Object> list = document.selectNodes(EXTENSIONSXPATH);
        for (Object aList1 : list) {
            Element element = (Element) aList1;
            String uniquePath = element.getUniquePath();
            Node parent = document.selectSingleNode(uniquePath + "/UML:Extend.base/UML:UseCase/@xmi.idref");
            Node child = document.selectSingleNode(uniquePath + "/UML:Extend.extension/UML:UseCase/@xmi.idref");
            String stringValue = parent.getStringValue();
            UseCase p = usecases.get(stringValue);
            UseCase c = usecases.get(child.getStringValue());

            c.parents.add(p);

        }

        list = document.selectNodes(INCLUDESXPATH);
        for (Object aList : list) {
            Element element = (Element) aList;
            Node parent = document.selectSingleNode(element.getUniquePath() + "/UML:Include.base/UML:UseCase/@xmi.idref");
            Node child = document.selectSingleNode(element.getUniquePath() + "/UML:Include.addition/UML:UseCase/@xmi.idref");
            UseCase useCase = usecases.get(parent.getStringValue());
            UseCase i = usecases.get(child.getStringValue());
            useCase.includes.add(i);
        }
        list = document.selectNodes(ASSOCIATIONSXPATH);
        /*
        /UML:Association.connection/UML:AssociationEnd/UML:AssociationEnd.participant/UML:Actor
        */
        for (Object aList2 : list) {
            Element element = (Element) aList2;
            Node parent = document.selectSingleNode(element.getUniquePath() + "/UML:Association.connection/UML:AssociationEnd/UML:AssociationEnd.participant/UML:Actor/@xmi.idref");
            Node child = document.selectSingleNode(element.getUniquePath() + "/UML:Association.connection/UML:AssociationEnd/UML:AssociationEnd.participant/UML:UseCase/@xmi.idref");
            Actor actor = actors.get(parent.getStringValue());
            UseCase i = usecases.get(child.getStringValue());
            actor.usecases.add(i);
        }


        document.selectNodes(ASSOCIATIONSXPATH);

        usecaseTree.setShowsRootHandles(true);
//        usecaseTree.setRootVisible(false);

        dupes = new HashSet<UseCase>();

        for (Actor actor : actors.values()) {
            DefaultMutableTreeNode actorNode = new DefaultMutableTreeNode(actor);
            root.add(actorNode);
            for (UseCase useCase : actor.usecases) {
                decorateUseCaseNode(useCase, actorNode, dupes);

            }
        }

    }

    private DefaultMutableTreeNode initTreeView() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Usecase Diagrams");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root);
        usecaseTree.setModel(defaultTreeModel);
        return root;
    }

    private void decorateUseCaseNode(UseCase useCase, DefaultMutableTreeNode parentNode, Collection<UseCase> dupes) {
        if (dupes.contains(useCase))
            return;
        DefaultMutableTreeNode ucNode = new DefaultMutableTreeNode(useCase);
        parentNode.add(ucNode);
        dupes.add(useCase);
        boolean empty = useCase.includes.isEmpty();
        if (!empty) {
            DefaultMutableTreeNode includeNode = new DefaultMutableTreeNode("includes");
            ucNode.add(includeNode);
            for (Object include : useCase.includes) {
                UseCase aCase = (UseCase) include;
                if (dupes.contains(aCase))
                    includeNode.add(new DefaultMutableTreeNode(aCase));
                else
                    decorateUseCaseNode(aCase, includeNode, dupes);
            }

        }
        Set<UseCase> ss = new HashSet<UseCase>();

        for (UseCase c : usecases.values()) {

            if (c.parents.contains(useCase)) {
                ss.add(c);
            }

        }
        if (!ss.isEmpty()) {
            DefaultMutableTreeNode extendsNode = new DefaultMutableTreeNode("extensions");

            for (UseCase aCase : ss) {
                if (dupes.contains(aCase))
                    extendsNode.add(new DefaultMutableTreeNode(aCase));
                else
                    decorateUseCaseNode(aCase, extendsNode, dupes);
            }
            ucNode.add(extendsNode);
        }
    }

    static void updateView(String content, String fname) {
        JTextPane jTextPane = new JTextPane();

        try {
            Kit.prepIFrame(new JInternalFrame(), fname, jTextPane, new Rectangle(300, 300, 300, 300));
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //TODO: verify for a purpose
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(APP_LZX_FILE_LOC + fname);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Component getFrame() {
        return Kit.getFrame();

    }

    public static JMenuBar getJMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
//        frame.setJMenuBar(jMenuBar);
        JMenu fileMenu = new JMenu("File");
        jMenuBar.add(fileMenu);


        fileMenu.add(new LoadXMIAction());

        //  fileMenu.add(new XStreamAction());

        fileMenu.add(new SlidingTabsAction());

        fileMenu.add(new FilePerActorAction());
        fileMenu.add(new WindowAction());
        fileMenu.add(new WindowTabsAction());
        fileMenu.add(new PoserAction());
        return jMenuBar;
    }

//    public static Component getFrame() {
//        return frame;
//    }

    private static final class SlidingTabsAction extends AbstractAction {
        public SlidingTabsAction() {
            super("LazloSlidingTabs");
        }

        public void actionPerformed(ActionEvent actionEvent) {

            Collection<? extends Actor> actors = useCaseView.actors.values();
            String s = "<canvas><resizelayout axis='y'/>\n";

            int c = 0;


            for (Actor actor : actors) {
                s += MessageFormat.format("<window y=''10'' x=''{0}''  height=''500''  width=''{1}'' title=''{2}'' resizable=''true'' closeable=''true''>    " +
                        "    <resizelayout axis=''y''/>\n" +
                        "<tabslider width=''{1}'' x=''1'' y=''1'' options=''releasetolayout'' spacing=''2'' slideduration=''1600''>",
                        c * winWidth, winWidth, actor.name);
                for (UseCase useCase : actor.usecases) s += handleUsecase(useCase);

                s += "</tabslider></window>";
                c++;
                dupes.clear();
            }
            s += "</canvas>";
            updateView(s, ADX_LZX);
        }

        final Set<UseCase> dupes = new HashSet<UseCase>();

        String handleUsecase(UseCase useCase) {
            String x = "";
            Set<UseCase> includes = useCase.includes;
            if (dupes.contains(useCase)) return x;
            dupes.add(useCase);
            for (UseCase aCase : includes)
                x += "<tabslider width='" + winWidth + "' options='releasetolayout' slideduration='1400'> " + handleUsecase(aCase) + "</tabslider>\n";
            String z = "<tabelement width='" + winWidth + "' options='releasetolayout'  text='" + useCase.getName() + "'>\n";
            if (x.length() > 0)
                z += "<resizelayout axis='y'/>\n" + x;
            z += "</tabelement>\n";

            return z;
        }
    }


    static final int WIN_WIDTH = 700;

//
//    enum LazWidget {
//        canvas(" "),
//        window() ,
//        tabslider("slideduration='1600'"),
//        tabelement("text="),
//        button("text="),;
//        private String extra;
//        String geom[] = {
//                "  x='11'  y='33' width='775' height='484' ",
//                "  x='2'   y='2' width='133' height='480' ",
//                "  x='144' y='2' width='629' height='480' ",
//                "  x='144' y='2' width='629' height='480' ",
//                "  x='144' y='2' width='629' height='480' ",
//                ""};
//
//public        Element getElement(  Element root )
//        {
//          return  root.addElement(name());
//        }
//
//        String getTag(Object parms) {
//            try {
//                boolean named = extra.endsWith("=");
//                Object o = null;
//                if (named) {
//                    Class<? extends Object> aClass = parms.getClass();
//                    Field field = aClass.getField("name");
//                    o = field.get(parms);
//                }
//                String val = (named ? "'" + o + "'" : "");
//                return "<" + name() + ' ' + extra + val + "> ";
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();  //TODO: verify for a purpose
//            }
//
//            return "<" + name() + ">\n\t";
//        }
//
//        String getTagClose() {
//            return "</" + name() + ">\n\t";
//        }
//
//        LazWidget(String extra) {
//            //TODO: verify for a purpose
//            this.extra = extra;
//        }
//
//        LazWidget() {
//        }
//
//
//    }

}