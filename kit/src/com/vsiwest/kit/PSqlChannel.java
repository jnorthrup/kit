package com.vsiwest.kit;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.*;
import com.vsiwest.kit.FileFilter.*;
import com.vsiwest.util.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: Mar 13, 2007
 * Time: 3:28:55 PM
 */
public final class PSqlChannel {
    /**
     * the actual port on database TCP socket
     */
    private static final int SQL_PORT = 5432;
    /**
     * the default jdbc driver string
     */
    private static final String JDBC_POSTGRESQL_PREFIX = "jdbc:postgresql://";
    /**
     * the default jdbc user string
     */
    private static final String vsiwest_PRODUCTION_USER_vsiwest = "/vsiwest_production?user=vsiwest";
    /**
     * the default host of the tunnel entry host
     */
    private static final String BALBOA_vsiwest_COM = "balboa.vsiwest.com";

    /**
     * the actual final jdbc database hostname default
     */
    private static final String TRON = "tron.vsiwest.com";
    private static boolean auth;

    /**
     * defaulted Tunnel entry point
     */
    private static String TUNNEL_HOSTNAME = BALBOA_vsiwest_COM;
    /**
     * the defaulted location url for JDBC
     */
    private static String JDBC_LOCATION = vsiwest_PRODUCTION_USER_vsiwest;
    /**
     * the defaulted JDBC driver info
     */
    private static String JDBC_PREFIX = JDBC_POSTGRESQL_PREFIX;
    /**
     * the defaulted jdbc connection local tunel point.
     */
    private static String JDBC_HOST = "localhost";

    /**
     * the defaulted database hostname
     */
    private static String END_HOSTNAME = TRON;
    /**
     * this is the defaulted port for jdbc connections
     */
    private static int JDBC_PORT = 7272;

    private static java.sql.Connection sqlConnection;
    private static Connection sshConnection;

    static String dburi;
    static Process process;
    static private final String HTML_PROMPT_MSG = "<html>" + "<img src='http://vsiwest.com/podcaster/com.vsiwest.dws.images/img_demo.gif'>" + "<p>Please Enter<blink> SSH </blink>Username.\n";


    private static Pair<String, String> getAuthInfo() {
        final ButtonGroup group = new ButtonGroup();

        Pair<String, String> stuff;

        System.out.println(System.getProperties());
        JLabel nameLabel = new JLabel(HTML_PROMPT_MSG);
        JTextField nameField = new JTextField(System.getProperty("user.name", System.getenv("USER")));
//Group the radio buttons.
        JToolBar bbar = new JToolBar();
        bbar.setOrientation(JToolBar.VERTICAL);
        bbar.setFloatable(false);

        for (final AuthMethods s : AuthMethods.values()) {
            JRadioButton radioButton = new JRadioButton(s.getPrompt()) {
                {
                    setActionCommand(s.name());
                }
            };
            group.add(radioButton);
            bbar.add(radioButton);
        }


        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(nameField, BorderLayout.CENTER);
        namePanel.add(bbar, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(null, namePanel, "SSH User ID", JOptionPane.PLAIN_MESSAGE);

        stuff = new Pair<String, String>(nameField.getText(), group.getSelection().getActionCommand());
        return stuff;
    }

    private static java.sql.Connection createSqlConnection() throws SQLException {

        dburi = Kit.getArgs().length > 0 ? Kit.getArgs()[0] : JDBC_PREFIX + JDBC_HOST + ":" + JDBC_PORT + JDBC_LOCATION;

        setSqlConnection(Kit.getDriver().connect(dburi, new Properties()));
        sqlConnection.setReadOnly(true);
        return sqlConnection;
    }

    private static void sshTunnel(boolean hookMeUpDawg, File sshDir) throws IOException, InterruptedException {
        sshConnection.setTCPNoDelay(false);
        sshConnection.createLocalPortForwarder(JDBC_PORT, END_HOSTNAME, SQL_PORT);
        Session session = sshConnection.openSession();
        new StreamGobbler(session.getStdout());
        new StreamGobbler(session.getStderr());
        if (hookMeUpDawg && auth) {
            JOptionPane.showConfirmDialog(null, new JLabel("<html><b>This authorization did succeed.</b><p>attempting to grab your keys."));
            SCPClient scpClient = new SCPClient(sshConnection);
            scpClient.get(new String[]{".ssh/id_dsa", ".ssh/id_dsa.pub", ".ssh/id_rsa", ".ssh/id_rsa.pub"}, sshDir.getAbsolutePath());
        }
        while (true) Thread.sleep(90000000L);
    }


    private static void passLogin(String username) throws IOException {
        JPasswordField passwordField = new JPasswordField();
        JOptionPane.showConfirmDialog(null, passwordField, "", JOptionPane.DEFAULT_OPTION);
        auth = sshConnection.authenticateWithPassword(username, String.valueOf(passwordField.getPassword()));
    }

    public static ResultSet executeSql(String sql) throws IOException, InterruptedException {
        while (true) {
            Statement statement;
            ResultSet resultSet = null;
            try {
                getSqlConnection().setAutoCommit(false);
                statement = getSqlConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                Kit.getLogger().info("sql: " + sql);
                statement.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                statement.setFetchSize(100);
                resultSet = statement.executeQuery(sql);
            } catch (SQLException e) {
                int ok = doSqlErrorMessage(e);
                if (ok == 0) try {


                    getSqlConnection().close();
                    createConnection();

                    continue;

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return resultSet;
        }
    }

    public static int doSqlErrorMessage(SQLException e) {
        int errorCode = e.getErrorCode();
        String message = e.getMessage();
        String sqlState = e.getSQLState();
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return JOptionPane.showConfirmDialog(null, ":" + errorCode + ':' + message + ':' + sqlState + ':' + stringWriter.toString(), "SQLException", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    public static void guardConnection() throws IOException, InterruptedException {
        try {
            if (!(getSqlConnection() != null || !getSqlConnection().isClosed())) createConnection();
        } catch (SQLException e) {
            if (doSqlErrorMessage(e) == 1) {
                guardConnection();
            }
        }
    }

    public static java.sql.Connection getSqlConnection() {
        try {
            if (null == sqlConnection || sqlConnection.isClosed()) createConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sqlConnection;
    }

    public static void setSqlConnection(java.sql.Connection sqlConnection) {
        PSqlChannel.sqlConnection = sqlConnection;
        try {
            sqlConnection.setReadOnly(true);
        } catch (SQLException e) {
            StringBuilder builder = new StringBuilder();

            do {
                builder.append(MessageFormat.format("{0}:{1} -- {2}\n", e.getErrorCode(), e.getSQLState(), e.getLocalizedMessage()));

                e = e.getNextException();

            } while (e != null);


            throw new Error(builder.toString());
        }
    }


    enum AuthMethods {
        NO_AUTH("just go!"),
        DSA("select your openssh id_dsa or id_rsa file. look in $HOME/.ssh/"),
        PASSWORD("enter your vsiwest unix server password."),
        CYGWIN("Cygwin SSH-key file will be used instead of a password.(last resort)");
        private String prompt;

        AuthMethods(String prompt) {
            this.prompt = prompt;
        }

        private String getPrompt() {
            return prompt;
        }
    }

    static java.sql.Connection createConnection() throws InterruptedException, IOException, SQLException {


        if (!auth) {


            boolean hookMeUpDawg;
            {
                Pair<String, String> stuff = getAuthInfo();


                Thread runJavaSsh;
                String[] strings;
                strings = new String[]{"ssh", "-N", "-L" + JDBC_PORT + ":" + END_HOSTNAME + ":" + SQL_PORT + stuff.getFirst() + '@' + TUNNEL_HOSTNAME};
                ProcessBuilder pb;
                pb = new ProcessBuilder(strings);

                AuthMethods cmd;
                cmd = null;
                String command = stuff.getSecond();
                for (AuthMethods authMethod : AuthMethods.values()) {
                    if (command.equals(authMethod.name())) {
                        cmd = authMethod;
                        break;
                    }
                }
                hookMeUpDawg = false;
                File homedir = new File(System.getProperty("user.home"));
                final File sshDir = new File(homedir, ".ssh");
                File[] files = sshDir.listFiles(IdFileFilter.getInstance());


                switch (cmd) {
                    case CYGWIN: {
                        if (process != null) process.destroy();
                        Kit.getLogger().info(pb.toString());

                        process = pb.start();
                        Runtime.getRuntime().addShutdownHook(new Thread() {
                            public void run() {
                                try {
                                    process.getErrorStream().close();
                                    process.getInputStream().close();
                                    process.getOutputStream().close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                process.destroy();
                                Kit.getLogger().info("background process has been terminated");
                            }
                        });
                        Thread.sleep(10000);
                    }
                    break;

                    case DSA:
                        hookMeUpDawg = hookMeUpDawg(hookMeUpDawg, sshDir);

                        File dsafile;
                        if (null == files || files.length < 1) {
                            JFileChooser pubkeyChooser = new JFileChooser("please select the public key id_dsa.pub");
                            pubkeyChooser.setCurrentDirectory(sshDir.getAbsoluteFile());
                            pubkeyChooser.setVisible(true);

                            if (JFileChooser.APPROVE_OPTION == pubkeyChooser.showOpenDialog(null)) {
                                dsafile = pubkeyChooser.getSelectedFile();
                                auth = sshConnection.authenticateWithPublicKey(stuff.getFirst(), dsafile, null);
                            }
                        } else
//                            dsafile = files[0];

                            RunThread(hookMeUpDawg, sshDir);
                        break;

                    case PASSWORD:
                        hookMeUpDawg(hookMeUpDawg, sshDir);
                        passLogin(stuff.getFirst());
                        if (!auth) {
                            JOptionPane.showConfirmDialog(null, new JLabel("<html><b>This authorization did not succeed.</b><p>one more chance."));
                            passLogin(stuff.getFirst());

                        }
                        RunThread(hookMeUpDawg, sshDir);
                        break;
                    case NO_AUTH:
                        auth = true;

                    default:
                        JDBC_PORT = SQL_PORT;
                        JDBC_HOST = TRON;
                        break;
                }

                if (!auth) {
                    JOptionPane.showConfirmDialog(null, new JLabel("<html><b>This authorization did not succeed.</b><p>Exiting."));
                    System.exit(1);
                } else return createSqlConnection();
            }
        }
        return getSqlConnection();
    }

    private static void RunThread(final boolean hookMeUpDawg, final File sshDir) {
        Thread runJavaSsh;
        runJavaSsh = new Thread() {

            public void run() {
                try {
                    sshTunnel(hookMeUpDawg, sshDir);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //is this informative enough?
                }
            }
        };
        runJavaSsh.start();
    }

    private static boolean hookMeUpDawg(boolean hookMeUpDawg, File sshDir) {
        if (!sshDir.exists()) {
            hookMeUpDawg = true;
            if (!sshDir.mkdirs()) new Error("couldn't create $HOME/.ssh and you don't have one!!");
            JOptionPane.showMessageDialog(null, new JLabel("<html><b>Hookin you up dawg!<p>we have created " + sshDir.getAbsolutePath()));
        }
        sshConnection = new Connection(TUNNEL_HOSTNAME);
        try {
            sshConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hookMeUpDawg;
    }
}