package be.wegenenverkeer.qvx

import be.wegenenverkeer.qvx.QVXDbExportConfig
import be.wegenenverkeer.qvx.QVXWriter

/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */

class QVXDbExporter {

    private final QVXDbExportConfig config;
    private static final int FETCH_SIZE = 1024;
        
    String[] tableTypes = ["TABLE", "VIEW"]

    QVXDbExporter(OptionAccessor options) {
        this.config = new QVXDbExportConfig(options)
    }

    public void run() {
        def start = System.currentTimeMillis()
        def db = groovy.sql.Sql.newInstance(config.jdbcUrl, config.dbUserName, config.dbPassword, config.jdbcDriver)

        db.getConnection().setAutoCommit(false)
        //this is required for fetch-size limit to work (see oa.http://stackoverflow.com/questions/1468036/java-jdbc-ignores-setfetchsize)
        println "Setting fetch size to:" + FETCH_SIZE
        db.withStatement { stmt -> stmt.setFetchSize(FETCH_SIZE) }


        def tableMeta = getMetaData(db).getTables(null, config.dbSchemaPattern, config.dbTableNamePattern, tableTypes)

        while (tableMeta.next()) {
            String tableName = (config.dbSchemaPattern != null) ? tableMeta.getString(2) + '.' + tableMeta.getString(3) : tableMeta.getString(3)
            File fname = new File(config.outputDir, tableName + ".qvx")
            QVXWriter qvxWriter = new QVXWriter()
            qvxWriter.write(fname, tableName, db)
        }
        println "Export took ${(System.currentTimeMillis() - start) / 1000} sec."
        db.close()
    }

    private getMetaData(db) {
        return db.getConnection().getMetaData()
    }


}
