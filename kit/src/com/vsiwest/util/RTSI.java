package com.vsiwest.util;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import static java.lang.Class.forName;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class RTSI {
    private static final FileNameExtensionFilter classFilenameFilter = new FileNameExtensionFilter("class files", "class");


    public static void find(Class toSubclass) {
        //            Class tosubclass = Class.forName(tosubclassname);
        Package[] pcks = Package.getPackages();
        for (Package pck : pcks) {
            find(pck, toSubclass);
        }
    }

    public static void find(Package pckgname, Class tosubclass) {
        Collection<Serializable> s = findnames(pckgname, tosubclass);

        for (Object value : s)
            System.out.println(value);
    }


    public static Collection<Serializable> findnames(Package pkg, Class tosubclass) {
        Vector<Serializable> v = new Vector<Serializable>();
        // Code from JWhich
        // ======
        // Translate the package name into an absolute path
//        String name = pkg;
        String name = pkg.getName();
        if (!name.startsWith("/"))
            name = "/" + name;

        name = name.replace('.', '/');

        // Get a File object for the package

        URL url = RTSI.class.getResource(name);

        // URL url = tosubclass.getResource(name);
        // URL url = ClassLoader.getSystemClassLoader().getResource(name);
        //System.out.println(name+"->"+url);

        // Happens only if the jar file is not well constructed, i.e.
        // if the directories do not appear alone in the jar file like here:
        //
        //          meta-inf/
        //          meta-inf/manifest.mf
        //          commands/                  <== IMPORTANT
        //          commands/Command.class
        //          commands/DoorClose.class
        //          commands/DoorLock.class
        //          commands/DoorOpen.class
        //          commands/LightOff.class
        //          commands/LightOn.class
        //          RTSI.class
        //
        if (url == null) return null;

        File directory = new File(url.getFile());

        // New code
        // ======
        if (directory.exists()) {
            // Get the list of the fileNames contained in the package
//            String[] fileNames = directory.list();
            final FileNameExtensionFilter nameExtensionFilter = new FileNameExtensionFilter("classes", "class");
            for (File file : directory.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return nameExtensionFilter.accept(pathname);

                }
            })) {
                final String fname = file.getName();

                String classname = fname.substring(0, fname.length() - 6);
                try {
                    // Try to create an instance of the object
                    final Class<?> aClass = forName(pkg + "." + classname);
                    Object o = aClass.newInstance();
                    if (tosubclass.isInstance(o)) {
                        //System.out.println(classname);
                        v.add(aClass);
                    }
                } catch (ClassNotFoundException cnfex) {
                    System.err.println(cnfex);
                } catch (InstantiationException iex) {
                    // We try to instanciate an interface
                    // or an object that does not have a
                    // default constructor
                } catch (IllegalAccessException iaex) {

                }
            }
        } else {
            try {
                // It does not work with the filesystem: we must
                // be in the case of a package contained in a jar file.
                JarURLConnection conn = (JarURLConnection) url.openConnection();
                String starts = conn.getEntryName();
                JarFile jfile = conn.getJarFile();
                Enumeration<JarEntry> e = jfile.entries();
                while (e.hasMoreElements()) {
                    ZipEntry entry = e.nextElement();
                    String entryname = entry.getName();
                    if (entryname.startsWith(starts)
                            && (entryname.lastIndexOf('/') <= starts.length())
                            && entryname.endsWith(".class")) {
                        String classname = entryname.substring(0, entryname.length() - 6);
                        if (classname.startsWith("/"))
                            classname = classname.substring(1);
                        classname = classname.replace('/', '.');
                        try {
                            // Try to create an instance of the object
                            Object o = forName(classname).newInstance();
                            if (tosubclass.isInstance(o)) {
                                //System.out.println(classname.substring(classname.lastIndexOf('.')+1));
                                v.add(classname.substring(classname.lastIndexOf('.') + 1));
                            }
                        } catch (ClassNotFoundException cnfex) {
                            System.err.println(cnfex);
                        } catch (InstantiationException iex) {
                            // We try to instanciate an interface
                            // or an object that does not have a
                            // default constructor
                        } catch (IllegalAccessException iaex) {
                            // The class is not public
                        }
                    }
                }
            } catch (IOException ioex) {
                System.err.println(ioex);
            }
        }

        return v;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length == 2) {
            try {
                Class tosubclass = forName(args[1]);
                final Package pkg = Package.getPackage(args[0]);
                find(pkg, tosubclass);
            } catch (ClassNotFoundException ex) {
                System.err.println("Class " + args[1] + " not found!");
            }
        } else {
            if (args.length == 1) {
                find(Class.forName(args[0]));
            } else {
                System.out.println("Usage: java RTSI [<package>] <subclass>");
            }
        }
    }
}// RTSI