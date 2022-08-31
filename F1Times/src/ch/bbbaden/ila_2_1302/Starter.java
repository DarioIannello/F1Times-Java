package ch.bbbaden.ila_2_1302;

import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Dario
 */
public class Starter {

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws SQLException {
        LogInOrRegister lOR = new LogInOrRegister();
        lOR.registerOrLogin();
    }

}
