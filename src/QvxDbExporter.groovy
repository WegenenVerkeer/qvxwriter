/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */

class QVXDbExporter {

    private final QVXDbExportConfig config;

    String[] tableTypes = ["TABLE", "VIEW"]

    QVXDbExporter(){
        this(new QVXDbExportConfig())
    }

    QVXDbExporter(QVXDbExportConfig config) {
        this.config = config
    }

    public void run() {
        def start = System.currentTimeMillis()
        def db = groovy.sql.Sql.newInstance(config.jdbcUrl, config.dbUserName, config.dbPassword,config.jdbcDriver)
        def tableMeta = getMetaData(db).getTables(null, config.dbSchemaPattern, config.dbTableNamePattern, tableTypes)

        while (tableMeta.next()) {
            String tableName = (config.dbSchemaPattern != null) ? tableMeta.getString(2) + '.' + tableMeta.getString(3) : tableMeta.getString(3)
            export(db, config.outputDir, tableName)
        }
        println "Export took ${(System.currentTimeMillis() - start) / 1000} sec."
        db.close()
    }

    private getMetaData (db) {
        return db.getConnection().getMetaData()
    }

    private export(db, dirName, table) {
        QVXWriter qvx = new QVXWriter()
        def fname = new File(dirName, table + ".qvx")
        println "Exporting $table to $fname ..."
        qvx.open(fname)

        def sqlStmt = 'select * from ' + table


        def writeHeader = {meta ->
            // write header
            qvx.writeTableMetadata(meta, sqlStmt)
            qvx.writeHeaderDataSeperator()
        }

        def writeRow = { row ->
            qvx.writeData( row.toRowResult() )
        }

        db.eachRow(sqlStmt, writeHeader, writeRow)

        qvx.close()
        println "Export complete."
        println ""

    }

}
