/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bbbaden.ila_2_1302;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Dario
 */
public class Times {

    private String tracks;
    private Connection con;
    private PreparedStatement pst;
    private final JRadioButton rDry = new JRadioButton("Dry");
    private final JRadioButton rWet = new JRadioButton("Wet");
    private final JComboBox track = new JComboBox();
    private String laptime;
    private String condition;
    private String selTrack;
    private boolean check;
    private String laptimes;
    private String lapMinute;
    private String lapSecond;
    private String lapMilli;

    public void watchOrInsert(String driverID) throws SQLException {
        Object[] options = {"Watch current Laptimes", "Compare new Laptime"};
        int input = JOptionPane.showOptionDialog(null, "Do you want to compare a new Laptime or watch your best Laptime?", "Laptime", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        switch (input) {
            case 0:
                WatchBestLap(driverID);
                break;
            case 1:
                InsertNewLap(driverID);
                break;
            case JOptionPane.CANCEL_OPTION:
                System.exit(0);
            default:
                break;
        }
    }

    public void WatchBestLap(String driverID) throws SQLException {
        do {
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/f1times?zeroDateTimeBehavior=convertToNull", "root", "");
                String sql = "SELECT country, city FROM circuit";
                pst = con.prepareStatement(sql);
                ResultSet rS = pst.executeQuery(sql);
                check = true;
                while (rS.next()) {
                    track.addItem(rS.getString("country") + ", " + rS.getString("city"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
                check = false;
            } finally {
                closeConnection();
            }
        } while (!check);
        JPanel pane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
        pane.add(new JLabel("Conditions: "), gbc);

        gbc.gridy = 1;

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pane.add(rDry, gbc);
        gbc.gridy = 1;
        pane.add(rWet, gbc);

        gbc.gridy = -2;
        gbc.gridx = 0;
        pane.add(new JLabel("Track: "), gbc);

        gbc.gridx = 1;
        pane.add(track, gbc);
        do {
            int reply = JOptionPane.showConfirmDialog(null, pane, "Please select", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (reply == JOptionPane.OK_OPTION) {
                selTrack = track.getSelectedItem().toString();
                if (rDry.isSelected() && rWet.isSelected()) {
                    JOptionPane.showMessageDialog(null, "You can only select one!", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                    check = false;
                } else if (!rDry.isSelected() && !rWet.isSelected()) {
                    JOptionPane.showMessageDialog(null, "You need to select one!", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                    check = false;
                } else if (rDry.isSelected()) {
                    condition = "1";
                    check = true;
                    getTracks(selTrack);
                } else if (rWet.isSelected()) {
                    condition = "2";
                    check = true;
                    getTracks(selTrack);
                }
                do {
                    try {
                        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/f1times?zeroDateTimeBehavior=convertToNull", "root", "");
                        String sql = "SELECT lapMinutes, lapSeconds, lapMillis FROM laptime WHERE id_circuit=? AND id_tc=? AND id_driver=?";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, tracks);
                        pst.setString(2, condition);
                        pst.setString(3, driverID);
                        ResultSet rS = pst.executeQuery();
                        if (rS.next()) {
                            lapMinute = rS.getString("lapMinutes");
                            lapSecond = rS.getString("lapSeconds");
                            lapMilli = rS.getString("lapMillis");
                            if (lapSecond.length() < 2) {
                                lapSecond = "0" + rS.getString("lapSeconds");
                            }
                            if (lapMilli.length() < 3) {
                                lapMilli = "0" + rS.getString("lapMillis");
                            }
                            laptime = rS.getString("lapMinutes") + ":" + rS.getString("lapSeconds") + "." + rS.getString("lapMillis");
                            JOptionPane.showMessageDialog(null, "Your laptime is: " + laptime, "All clear!", JOptionPane.INFORMATION_MESSAGE);
                            check = true;
                            watchOrInsert(driverID);
                        } else {
                            JOptionPane.showMessageDialog(null, "You don't have a laptime here", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                            Object[] options = {"Insert Laptime", "Try different Track"};
                            int input = JOptionPane.showOptionDialog(null, "Do you want to insert a new Laptime or try another Track?", "Laptime", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            if (input == 0) {
                                InsertNewLap(driverID);
                            } else if (input == 1) {
                                watchOrInsert(driverID);
                            }
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                        check = false;
                    } finally {
                        closeConnection();
                    }
                } while (check);
            } else if (reply == JOptionPane.OK_CANCEL_OPTION) {
                System.exit(0);
            }

        } while (!check);

    }

    public void InsertNewLap(String driverID) throws SQLException {
        final JTextField lapTime = new JTextField();
        String lap;
        do {
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/f1times?zeroDateTimeBehavior=convertToNull", "root", "");
                String sql = "SELECT country, city FROM circuit";
                pst = con.prepareStatement(sql);
                ResultSet rS = pst.executeQuery(sql);
                check = true;
                while (rS.next()) {
                    track.addItem(rS.getString("country") + ", " + rS.getString("city"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
                check = false;
            } finally {
                closeConnection();
            }
        } while (!check);
        JPanel pane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
        pane.add(new JLabel("Conditions: "), gbc);

        gbc.gridy = 1;

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pane.add(rDry, gbc);
        gbc.gridy = 1;
        pane.add(rWet, gbc);

        gbc.gridy = -2;
        gbc.gridx = 0;
        pane.add(new JLabel("Track: "), gbc);

        gbc.gridx = 1;
        pane.add(track, gbc);

        gbc.gridy = -3;
        gbc.gridx = 0;

        pane.add(new JLabel("Laptime: "), gbc);

        gbc.gridx = 1;
        lapTime.setText("");
        pane.add(lapTime, gbc);

        gbc.gridy = -4;
        gbc.gridx = 1;

        pane.add(new JLabel("Format: MM:SS.sss"), gbc);
        do {
            int reply = JOptionPane.showConfirmDialog(null, pane, "Please select", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (reply == JOptionPane.OK_OPTION) {
                selTrack = track.getSelectedItem().toString();
                if (lapTime == null) {
                    JOptionPane.showMessageDialog(null, "You need to input a laptime!", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                    check = false;
                }
                if (rDry.isSelected() && rWet.isSelected()) {
                    JOptionPane.showMessageDialog(null, "You can only select dry or wet!", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                    check = false;
                } else if (!rDry.isSelected() && !rWet.isSelected()) {
                    JOptionPane.showMessageDialog(null, "You need to select either dry or wet!", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                    check = false;
                } else if (rDry.isSelected()) {
                    condition = "1";
                    check = true;
                    getTracks(selTrack);
                } else if (rWet.isSelected()) {
                    condition = "2";
                    check = true;
                    getTracks(selTrack);
                }
                lap = lapTime.getText();
                if (lap.length() == 9) {
                    lapMinute = lap.substring(0, 2);
                    lapSecond = lap.substring(3, 5);
                    lapMilli = lap.substring(6, 9);
                    laptimes = lapMinute + ":" + lapSecond + "." + lapMilli;

                    while (check) {
                        try {
                            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/f1times?zeroDateTimeBehavior=convertToNull", "root", "");
                            String sql = "SELECT lapMinutes, lapSeconds, lapMillis FROM laptime WHERE id_circuit=? AND id_tc=? AND id_driver=?";
                            pst = con.prepareStatement(sql);
                            pst.setString(1, tracks);
                            pst.setString(2, condition);
                            pst.setString(3, driverID);
                            ResultSet rS = pst.executeQuery();
                            if (rS.next()) {
                                String laptimes2 = rS.getString("lapminutes") + ":" + rS.getString("lapSeconds") + "." + rS.getString("lapMillis");
                                JPanel pane2 = new JPanel(new GridBagLayout());
                                gbc.gridy = 0;
                                gbc.gridx = 1;
                                pane2.add(new JLabel("Old Laptime: "), gbc);
                                gbc.gridx = 2;
                                pane2.add(new JLabel(laptimes2), gbc);
                                gbc.gridy = -1;
                                gbc.gridx = 1;
                                pane2.add(new JLabel("New Laptime: "), gbc);
                                gbc.gridx = 2;
                                pane2.add(new JLabel(laptimes), gbc);
                                JOptionPane.showConfirmDialog(null, pane2, "Laptimes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                                check = true;
                                int lapMin = Integer.parseInt(lapMinute);
                                int lapSec = Integer.parseInt(lapSecond);
                                int lapMil = Integer.parseInt(lapMilli);
                                int lapMin2 = Integer.parseInt(laptimes2.substring(0, 2));
                                int lapSec2 = Integer.parseInt(laptimes2.substring(3, 5));
                                int lapMil2 = Integer.parseInt(laptimes2.substring(6, 9));
                                if (lapMin <= lapMin2) {
                                    if (lapSec <= lapSec2) {
                                        if (lapMil < lapMil2) {
                                            insertNewLap(lapMinute, lapSecond, lapMilli, condition, driverID);
                                        } else if (lapSec < lapSec2) {
                                            insertNewLap(lapMinute, lapSecond, lapMilli, condition, driverID);
                                        }
                                    } else if (lapMin < lapMin2) {
                                        insertNewLap(lapMinute, lapSecond, lapMilli, condition, driverID);
                                    }
                                }
                            } else {
                                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/f1times?zeroDateTimeBehavior=convertToNull", "root", "");
                                sql = "INSERT INTO laptime(lapMinutes, lapSeconds, lapMillis, id_circuit, id_tc, id_driver) VALUES (?, ?, ?, ?, ?, ?)";
                                pst = con.prepareStatement(sql);
                                pst.setString(1, lapMinute);
                                pst.setString(2, lapSecond);
                                pst.setString(3, lapMilli);
                                pst.setString(4, tracks);
                                pst.setString(5, condition);
                                pst.setString(6, driverID);
                                pst.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Your laptime " + lapMinute + ":" + lapSecond + "." + lapMilli + " has been saved!", "All clear!", JOptionPane.INFORMATION_MESSAGE);
                            }
                            watchOrInsert(driverID);
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(null, e);
                            check = false;
                        } finally {
                            closeConnection();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You need to insert the Time the correct way (MM:SS.sss)!", "Something went wrong!", JOptionPane.ERROR_MESSAGE);
                    check = false;
                }
            } else if (reply == JOptionPane.OK_CANCEL_OPTION) {
                System.exit(0);
            }
        } while (!check);
    }

    public String getTracks(String selTrack) {
        switch (selTrack) {
            case "Bahrain, Sakhir":
                tracks = "1";
                break;
            case "Emilia-Romagna, Imola":
                tracks = "2";
                break;
            case "Portugal, Portimão":
                tracks = "3";
                break;
            case "Spain, Barcelona":
                tracks = "4";
                break;
            case "Monaco, Monte Carlo":
                tracks = "5";
                break;
            case "Azerbaijan, Baku":
                tracks = "6";
                break;
            case "Canada, Montréal":
                tracks = "7";
                break;
            case "France, Le Castellet":
                tracks = "8";
                break;
            case "Austria, Spielberg":
                tracks = "9";
                break;
            case "Great Britian, Silverstone":
                tracks = "10";
                break;
            case "Hungary, Budapest":
                tracks = "11";
                break;
            case "Belgium, Spa-Francorchamps":
                tracks = "12";
                break;
            case "Netherlands, Zandvoort":
                tracks = "13";
                break;
            case "Italy, Monza":
                tracks = "14";
                break;
            case "Russia, Sochi":
                tracks = "15";
                break;
            case "Singapore, Singapore":
                tracks = "16";
                break;
            case "Japan, Suzuka":
                tracks = "17";
                break;
            case "United States, Austin":
                tracks = "18";
                break;
            case "Mexico, Mexico City":
                tracks = "19";
                break;
            case "Brazil, São Paulo":
                tracks = "20";
                break;
            case "Australia, Melbourne":
                tracks = "21";
                break;
            case "Saudi Arabia, Jeddah":
                tracks = "22";
                break;
            case "Abu Dhabi, Yas Island":
                tracks = "23";
                break;
            case "China, Shanghai":
                tracks = "24";
                break;
            default:
                break;
        }
        return tracks;
    }

    public void insertNewLap(String lapMinute, String lapSecond, String lapMilli, String condition, String driverID) throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/f1times?zeroDateTimeBehavior=convertToNull", "root", "");
        String sql = "UPDATE laptime SET lapMinutes=?, lapSeconds=?, lapMillis=? WHERE id_circuit=? AND id_tc=? AND id_driver=?";
        pst = con.prepareStatement(sql);
        pst.setString(1, lapMinute);
        pst.setString(2, lapSecond);
        pst.setString(3, lapMilli);
        pst.setString(4, tracks);
        pst.setString(5, condition);
        pst.setString(6, driverID);
        pst.executeUpdate();
        JOptionPane.showMessageDialog(null, "Your laptime " + lapMinute + ":" + lapSecond + "." + lapMilli + " has been saved!", "All clear!", JOptionPane.INFORMATION_MESSAGE);
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
