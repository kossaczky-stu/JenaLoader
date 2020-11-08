/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author igor
 */
public class SqlDataSourceFactory implements DataSourceFactory {
    private final Connection conn;

    public SqlDataSourceFactory(Connection conn) {
        this.conn = conn;
    }

    @Override
    public DataSource createDataSource(String query) throws SQLException {
                Statement st = conn.createStatement();
        ResultSet rset = st.executeQuery(query);
        return new SqlDataSource(rset);
    }
    
}
