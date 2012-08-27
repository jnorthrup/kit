package com.vsiwest.lockpick;

import static com.vsiwest.kit.Kit.*;
import static com.vsiwest.lockpick.BoxFileModel.*;
import com.vsiwest.util.*;

import javax.swing.*;
import static javax.swing.JFileChooser.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;
import java.io.*;
import java.nio.*;
import java.text.*;
import java.util.*;

/**
 * Produces and displays a tree graph
 *
 * @author jim
 * @created Jun 19, 2007 2:38:31 PM
 * @copyright vsiwest, all rights reserved; unauthorized access and use is prohibitted by law.
 */
public class BoxFileTreeView {
    public BoxFileTreeView() {
    }

    public JTree createMediaChunkTree(final Iterator<Pair<Integer, ByteBuffer>> iterator) {
        final DefaultMutableTreeNode pivot = getRootNode(iterator);
        final JTree tree = new JTree(pivot);
        tree.expandRow(0);
        return tree;
    }

    public DefaultMutableTreeNode getRootNode(final Iterator<Pair<Integer, ByteBuffer>> iterator) {

        return getRootNode(SimpleDateFormat.getDateTimeInstance().format(new Date()), null);
    }

    static public DefaultMutableTreeNode getRootNode(final String context, final BoxFileModel parentModel) {
        final DefaultMutableTreeNode pivot = new DefaultMutableTreeNode(context);
        populateTreeNode(pivot, parentModel);
        return pivot;
    }

    public static void main(String[] args) throws Exception {
        final DefaultMutableTreeNode topRootNode = createMultiTree(args);
        JTree view = new JTree(topRootNode);
        JScrollPane pane = new JScrollPane(view);

        JFrame frame = new JFrame();
        frame.setResizable(true);
        frame.setContentPane(pane);
        frame.setBounds(20, 20, 820, 620);

        frame.setVisible(true);
    }

    static public DefaultMutableTreeNode createMultiTree(String[] filenames) throws IOException {

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(SimpleDateFormat.getInstance().format(new Date()));
        for (File file : getFiles(filenames)) {
            final BoxFileModel model = new BoxFileModel(file);
            final DefaultMutableTreeNode fhead = getRootNode(file.getAbsolutePath(), model);
            root.add(fhead);
        }
        return root;
    }

    private static File[] getFiles(String[] filenames) {
        if (filenames == null || filenames.length == 0) {
            final JFileChooser chooser = new JFileChooser("Select Media");
            chooser.setFileFilter(new FileNameExtensionFilter("Quicktime/MP4", getMp4Extensions()));
            chooser.setMultiSelectionEnabled(true);
//            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
            if (chooser.showOpenDialog(null) == APPROVE_OPTION) return chooser.getSelectedFiles();
        }
        List<File> flist = new ArrayList<File>();
        for (final String fnam : filenames)
            flist.add(new File(fnam));
        return (File[]) flist.toArray();

    }

    public static void populateTreeNode(final DefaultMutableTreeNode pivot, BoxFileModel parentModel) {
        for (Pair<Integer, ByteBuffer> handle : parentModel) {
            if (handle == null) return;
            parentModel.printChunkHeader(handle);
            final String cname = cString(handle.first);
            final DefaultMutableTreeNode spoke = new DefaultMutableTreeNode(cname);
            pivot.add(spoke);
            final ByteBuffer subchunk = handle.second;
            BoxTypeMeta boxTypeMeta;
            try {
                boxTypeMeta = BoxTypeMeta.valueOf(cname);
            } catch (IllegalArgumentException e) {
                boxTypeMeta = BoxTypeMeta.UnKn;
            }
            if (boxTypeMeta.parent) {
                final BoxFileModel model = new BoxFileModel(subchunk, parentModel);
                populateTreeNode(spoke, model);
            } else boxTypeMeta.leafNodeView(new Pair<DefaultMutableTreeNode, ByteBuffer>(spoke, subchunk));
            spoke.setUserObject(cname + " - " + boxTypeMeta.getDescription());
        }
    }
}
