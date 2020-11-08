/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

/**
 * Abstract factory for creating EntityDataSources
 * 
 * @author igor
 */
public interface DataSourceFactory {    
    public DataSource createDataSource(String data) throws Exception;    
}
