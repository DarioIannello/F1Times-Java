/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bbbaden.ila_2_1302;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Dario
 */
public class LogInOrRegister {

    private final Times times = new Times();
    private String driverID;
    private Connection con;
    private final JTextField userNameField = new JTextField(10);
    private final JPasswordField passwordField = new JPasswordField(10);
    private final JPasswordField confirmPwField = new JPasswordField(10);
    private String username;
    private String password;
    private String confimPw;
    private String postIn;
    private boolean check;

    public void registerOrLogin() throws SQLException {
        Object[] options = {"Yes [Log-In]", "No [Create Account]"};
        int input = JOptionPane.showOptionDialog(null, "Do you already have an account?", "Already have an account", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        switch (input) {
            case 0:
                logIn();
                break;
            case 1:
                register();
                break;
            case JOptionPane.CANCEL_OPTION:
                System.exit(0);
            default:
                break;
        }

    }

    public void register() throws SQLException {
        JPanel pane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
        pane.add(new JLabel("Username:"), gbc);

        gbc.gridy = 1;
        pane.add(new JLabel("Password:"), gbc);

        gbc.gridy = 2;
        pane.add(new JLabel("Confirm Password: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pane.add(userNameField, gbc);

        gbc.gridy = 1;
        pane.add(passwordField, gbc);

        gbc.gridy = 2;
        pane.add(confirmPwField, gbc);
        do {

            int reply = JOptionPane.showConfirmDialog(null, pane, "Please create an Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (reply == JOptionPane.OK_OPTION) {
                username = userNameField.getText();
                password = new String(passwordField.getPassword());
                confimPw = new String(confirmPwField.getPassword());
            } else if (reply == JOptionPane.OK_CANCEL_OPTION) {
                System.exit(0);
            }

            if (password.equals(confimPw)) {
                try {
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/f1times?zeroDateTimeBehavior=convertToNull", "root", "");
                    String sql = "SELECT * FROM driver WHERE username=? AND password=?";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setString(1, username);
                    pst.setString(2, password);
                    ResultSet rS = pst.executeQuery();
                    if (rS.next()) {
                        JOptionPane.showMessageDialog(null, "Your already registered!", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                        logIn();
                        check = false;
                    } else {
                        postIn = "INSERT INTO driver(username, password) VALUES (?, ?)";
                        PreparedStatement pstIn = con.prepareStatement(postIn);
                        pstIn.setString(1, username);
                        pstIn.setString(2, password);
                        pstIn.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Your username and password have been saved!", "All clear!", JOptionPane.INFORMATION_MESSAGE);
                        check = true;
                        logIn();
                    }

                } catch (HeadlessException | SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                    check = false;
                } finally {
                    closeConnection();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Your passwords aren't the same. Please try again.", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                check = false;
            }
        } while (!check);
    }

    public void logIn() throws SQLException {
        JPanel pane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
        pane.add(new JLabel("Username:"), gbc);

        gbc.gridy = 1;
        pane.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pane.add(userNameField, gbc);

        gbc.gridy = 1;
        pane.add(passwordField, gbc);
        do {

            int reply = JOptionPane.showConfirmDialog(null, pane, "Please Log-In", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (reply == JOptionPane.OK_OPTION) {
                username = userNameField.getText();
                password = new String(passwordField.getPassword());
            } else if (reply == JOptionPane.OK_CANCEL_OPTION) {
                System.exit(0);
            }

            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/f1times?zeroDateTimeBehavior=convertToNull", "root", "");
                String sql = "SELECT * FROM driver WHERE username=? AND password=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rS = pst.executeQuery();
                if (rS.next()) {
                    JOptionPane.showMessageDialog(null, "Your username and password are correct!", "All clear!", JOptionPane.INFORMATION_MESSAGE);
                    sql = "SELECT id_driver FROM driver WHERE username=? AND password=?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, username);
                    pst.setString(2, password);
                    rS = pst.executeQuery();
                    if (rS.next()) {
                        driverID = rS.getString("id_driver");
                    }
                    check = true;
                    times.watchOrInsert(driverID);
                } else {
                    JOptionPane.showMessageDialog(null, "Your username or password are incorrect!", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                    check = false;
                    registerOrLogin();
                }

            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
                check = false;
            } finally {
                closeConnection();
            }
        } while (!check);
    }

    public void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ignore) {
            }
        }
    }
}
