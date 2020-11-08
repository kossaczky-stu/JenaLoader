/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.io.IOException;

/**
 *
 * @author igor
 */
public class CsvDataSourceFactory implements DataSourceFactory {

    private final String path;
    private final String separator;
    private final char delim;
    private final boolean withHeader; 

    public CsvDataSourceFactory(String path, boolean withHeader,  char delim, String separator ) {
        this.path = path;
        this.separator = separator;
        this.delim = delim;
        this.withHeader = withHeader;
    }

    @Override
    public DataSource createDataSource(String csvfilename) throws IOException {
        return new CsvDataSource(path, csvfilename, withHeader, delim, separator);
    }
    
}
