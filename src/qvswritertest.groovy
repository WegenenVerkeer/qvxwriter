/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */





import groovy.sql.GroovyResultSet
import groovy.sql.ResultSetMetaDataWrapper
import groovy.sql.Sql

import java.sql.ResultSet
import java.sql.ResultSetMetaData

//@GrabConfig(systemClassLoader=true)
@Grab(group='postgresql', module='postgresql', version='8.3-603.jdbc4')
def createDb() {
   source = new org.postgresql.ds.PGSimpleDataSource()
   source.databaseName = 'historia-devt'
   source.user = "postgres"
   source.password = "postgres"
   db = new groovy.sql.Sql(source)

}


//def dump (tablename){
//    println " CONTENT OF TABLE ${tablename} ".center(40,'-')
//    db = createDb()
//    def buildQvxWriter = { ResultSetMetaData meta ->
//        outFName = meta.getTableName() + ".qvx"
//        tableName = meta.getTableName()
//        fldNames = []
//        fldTypes = []
//        (1..meta.columnCount).each{
//            fldNames << meta.getColumnName(it)
//            fldTypes << meta.getColumnTypeName(it)
//            println "${meta.getColumnName(it)} : ${meta.getColumnType(it)}"
//        }
//        qvxWriter = new QVXWriter(outFName, tableName, true, 0,
//                "#,###,##0.00", ".", ",", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss.sss", fldNames, fldTypes);
//
//    }
//
//    def writeRow = { row ->
//        qvxWriter.writeRecord( row.toRowResult().values*.toString())
//    }
//
//
//    db.eachRow('SELECT * FROM '+tablename, buildQvxWriter, writeRow)
//    qvxWriter.close()
//    db.close()
//}
//
//    def  printColNames = { meta ->
//        def width = meta.columnCount * 18
//        println " CONTENT OF TABLE ${tablename} ".center(width, '-')
//        (1..meta.columnCount).each {
//            print meta.getColumnLabel(it).padRight(18)
//        }
//        println()
//        println '-' * width
//    }
//
//def printRow = { row ->
//        row.toRowResult().values().each {
//            print it.toString().padRight(18)
//        }
//        println()
//    }

class FakeMeta {
    String[] names, fqdns
    boolean[] nullables

    def getColumnName(int i) {
        return names[i-1]
    }

    def getColumnCount() { return names.length}

    def isNullable(int i ) {
        return nullables[i-1]
    }

    def getColumnClassName(int i) {
        return fqdns[i-1]
    }

}

def fname = "/Users/maesenka/Downloads/test.qvx"
qvx = new QVXWriter()
println "Printing $fname"
qvx.open(fname)

fakeMeta = new FakeMeta( names: ['ProductId', 'Cost', 'Desc', 'Date'] ,
        fqdns: [Integer.class, Double.class, String.class, java.sql.Date]*.getCanonicalName() ,
        nullables: [true, true,true , true] )

qvx.writeTableMetadata(fakeMeta)
qvx.writeHeaderDataSeperator()
qvx.writeData([Integer.valueOf(123), Double.valueOf(1234.5d), null, new java.sql.Date(2010 - 1900, 2,20)])
qvx.close()
