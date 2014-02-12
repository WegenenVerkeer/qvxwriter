import groovy.sql.Sql

/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */

class QVXDbExporter {

    private final QVXDbExportConfig config;

    String[] tableTypes = ["TABLE", "VIEW"]

    QVXDbExporter() {
        this(new QVXDbExportConfig())
    }

    QVXDbExporter(QVXDbExportConfig config) {
        this.config = config
    }

    public void run() {
        def start = System.currentTimeMillis()
        def db = groovy.sql.Sql.newInstance(config.jdbcUrl, config.dbUserName, config.dbPassword, config.jdbcDriver)
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
