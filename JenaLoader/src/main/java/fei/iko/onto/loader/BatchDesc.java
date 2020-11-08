package fei.iko.onto.loader;

/**
 *
 * @author igor
 */
class DbConn {

    public String user;
    public String pwd;
    public String dburl;
}

public class BatchDesc {

    public LoaderConfiguration config;  // loader configuration
    public DataDesc[] data;             // Destription of datasets to be loaded from data source
    // two types of data sources are supported: csv (datasers are csvfiles) and mysql db (datasets are sql-selects)
    public String csvdir;               // full path to directory containing csv-datafiles for csv datasource
    public DbConn connection;           // structure describing connection to database for mysql db datasource

}
