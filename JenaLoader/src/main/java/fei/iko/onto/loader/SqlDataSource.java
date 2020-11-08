/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import static fei.iko.onto.loader.JenaLoader.LOG;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 *
 * @author igor
 */
public class SqlDataSource extends DataSource {

    private final ResultSet rset;
    
    public SqlDataSource(ResultSet rset) {
        this.rset = rset;
    }

    @Override
    public boolean getNext() {
        try {
            return rset.next();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public String asString(Object columnLabel) {
        try {
            if (columnLabel instanceof Integer) {
                Integer colNum = (Integer) columnLabel;
                return rset.getString(colNum);
            } else {
                return rset.getString(columnLabel.toString());
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
