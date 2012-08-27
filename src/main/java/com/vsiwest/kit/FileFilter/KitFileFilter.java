package com.vsiwest.kit.FileFilter;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA for vsiwest.com
 * User: jim
 * Date: Apr 5, 2007
 * Time: 7:25:21 PM
 * <p/>
 * It's not cool to violate the slightest nuance of our IP and/or copyrights
 * and we'll pursue such actions with a vengeance befitting an iraqi dictator
 * if you feel it is neccessary to do such things.
 */
public final class KitFileFilter extends FileFilter implements FilenameFilter {
    private static final KitFileFilter instance = new KitFileFilter();

    private KitFileFilter() {
    }

    public boolean accept(File file) {
        return file.getName().endsWith(".kit") || !file.isFile();
    }

    public String getDescription() {
        return "com.vsiwest.kit.ViewUtil.Kit Formulae";
    }

    public static KitFileFilter createKitFileFilter() {
        return instance;
    }

    public static FileFilter getInstance() {
        return instance;
    }

    public boolean accept(File dir, String name) {
        File file = new File(dir, name);
        return accept(file);
    }
}

 