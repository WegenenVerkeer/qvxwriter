/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */





import groovy.sql.GroovyResultSet
import groovy.sql.ResultSetMetaDataWrapper
import groovy.sql.Sql

import javax.sql.DataSource
import java.sql.ResultSet
import java.sql.ResultSetMetaData

@Grab(group='postgresql', module='postgresql', version='8.3-603.jdbc4')
def createDb() {
    groovy.sql.Sql.newInstance('jdbc:postgresql://localhost:5432/historia-devt', "postgres", "postgres","org.postgresql.Driver")
}

def getMetaData (db) {
    return db.getConnection().getMetaData()
}


def export(db, dirName, table) {
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

    db = createDb()
    db.eachRow(sqlStmt, writeHeader, writeRow)

    qvx.close()
    println "Export complete."
    println ""

}

def dir = "/Users/maesenka/Downloads/qvx"

def db = createDb()

String[] tableTypes = ["TABLE", "VIEW"]
String schema = "external"
String tableNamePattern = "vw_%"

def start = System.currentTimeMillis()
def tableMeta = getMetaData(db).getTables(null, schema , tableNamePattern, tableTypes )

while ( tableMeta.next() ) {
    String tableName = (schema != null) ? schema + '.' + tableMeta.getString(3) : tableMeta.getString(3)
    export(db, dir, tableName)
}

println "Export took ${(System.currentTimeMillis() - start) / 1000} sec."
db.close()

