package com.vsiwest.kit;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: Mar 13, 2007
 * Time: 3:28:55 PM
 */
public final class KitChannel {

  private static boolean auth;

  private static java.sql.Connection sqlConnection;

  static String dburi;
  static Process process;
  static private final String HTML_PROMPT_MSG = "<html>" + "<img src='http://vsiwest.com/podcaster/com.vsiwest.dws.images/img_demo.gif'>" + "<p>Please Enter<blink> SSH </blink>Username.\n";


//  private static Pair<String, String> getAuthInfo() {
//    final ButtonGroup group = new ButtonGroup();
//
//    Pair<String, String> stuff;
//
//    System.out.println(System.getProperties());
//    JLabel nameLabel = new JLabel(HTML_PROMPT_MSG);
//    JTextField nameField = new JTextField(System.getProperty("user.name", System.getenv("USER")));
////Group the radio buttons.
//    JToolBar bbar = new JToolBar();
//    bbar.setOrientation(JToolBar.VERTICAL);
//    bbar.setFloatable(false);
//
//    for (final AuthMethods s : AuthMethods.values()) {
//      JRadioButton radioButton = new JRadioButton(s.getPrompt()) {
//        {
//          setActionCommand(s.name());
//        }
//      };
//      group.add(radioButton);
//      bbar.add(radioButton);
//    }
//
//
//    JPanel namePanel = new JPanel(new BorderLayout());
//    namePanel.add(nameLabel, BorderLayout.NORTH);
//    namePanel.add(nameField, BorderLayout.CENTER);
//    namePanel.add(bbar, BorderLayout.SOUTH);
//    JOptionPane.showMessageDialog(null, namePanel, "SSH User ID", JOptionPane.PLAIN_MESSAGE);
//
//    stuff = new Pair<String, String>(nameField.getText(), group.getSelection().getActionCommand());
//    return stuff;
//  }



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

//  public static void guardConnection() throws IOException, InterruptedException {
//    try {
//      if (getSqlConnection() != null || !getSqlConnection().isClosed()) {
//        return;
//      }
//      createConnection();
//    } catch (SQLException e) {
//      if (doSqlErrorMessage(e) == 1) {
//        guardConnection();
//      }
//    }
//  }

  public static java.sql.Connection getSqlConnection() {
    try {
      if (null == sqlConnection || sqlConnection.isClosed()){
    dburi = Kit.getArgs().length > 0 ? Kit.getArgs()[0] : "jdbc:postgresql://" + "localhost" + ":" + 7272 + "/db?user=foo";

    try {
      final Driver driver = Kit.getDriver();
      final Connection connect = driver.connect(dburi, new Properties());
      setSqlConnection(connect);
    } catch (SQLException e) {
      e.printStackTrace();  //todo: verify for a purpose
    } catch (ClassNotFoundException e) {
      e.printStackTrace();  //todo: verify for a purpose
    } catch (InstantiationException e) {
      e.printStackTrace();  //todo: verify for a purpose
    } catch (IllegalAccessException e) {
      e.printStackTrace();  //todo: verify for a purpose
    } finally {
    }
    sqlConnection.setReadOnly(true);
    return sqlConnection;}
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return sqlConnection;
  }

  public static void setSqlConnection(java.sql.Connection sqlConnection) {
    KitChannel.sqlConnection = sqlConnection;
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




}