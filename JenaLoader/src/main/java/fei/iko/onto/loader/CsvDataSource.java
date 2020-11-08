/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author igor
 */
public class CsvDataSource extends DataSource {

    private final Iterable<CSVRecord> rs;
    private final Iterator<CSVRecord> it;
    private CSVRecord record;
    private final String separator;

    public CsvDataSource(String path, String csvfile, boolean withHeader, char withDelim, String sep) throws FileNotFoundException, IOException {
        separator = sep;
        File f = new File(path, csvfile);
        Reader in = new FileReader(f);
        if (withHeader) {
            rs = CSVFormat.DEFAULT.withFirstRecordAsHeader().withDelimiter(withDelim).parse(in);
        } else {
            rs = CSVFormat.DEFAULT.withDelimiter(withDelim).parse(in);
        }
        it = rs.iterator();
    }

    @Override
    public boolean getNext() {
        if (!it.hasNext()) {
            record = null;
            return false;
        }
        record = it.next();
        return true;
    }

    @Override
    public String asString(Object index) {
        if (index instanceof Integer) {
            return record.get(((Integer) index)).trim();
        }
        return null;
    }

    @Override
    public Collection<String> asStrings(Object name) {
        String val = asString(name);
        if (val == null) {
            return null;
        }

        if (separator == null || separator.isEmpty()) {
            return Collections.singleton(val);
        }

        String[] vals = val.split(separator);
        List<String> valuelist = Arrays.asList(vals);
        return valuelist;
    }

}
