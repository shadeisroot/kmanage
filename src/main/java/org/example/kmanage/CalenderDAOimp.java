package org.example.kmanage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CalenderDAOimp implements CalenderDAO{

    private Connection con;

    public CalenderDAOimp() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=Kmanager2023;userName=CSe2023t_t_2;password=CSe2023tT2#23;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e) {
            System.out.println("Cant connect to Database" + e);
        }
    }
}
