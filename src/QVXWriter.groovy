/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */

import groovy.xml.MarkupBuilder

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.text.SimpleDateFormat

import java.sql.*




class QVXField {

    ResultSetMetaData metadata
    int fieldNumber

    def writeFieldHeader(builder){
        builder.QVXFieldHeader {
            FieldName( metadata.getColumnName())
        }
    }

}


class QVXWriter {

    def typeMapper = new TypeMapper()
    def writers = [];
    OutputStream outStream
    OutputStreamWriter writer
    MarkupBuilder xml
    final private byte[] HDSep = [(byte) 0].toArray(new byte[1])

    def open(fName) {
        outStream = new FileOutputStream(fName, false)
        writer = new OutputStreamWriter(outStream, "UTF-8")
        xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration([standalone: "yes", version: "1.0", encoding: "UTF-8"])

    }


    def writeFieldHeaders(builder, meta) {

        builder.Fields {
            (1..meta.columnCount).each { index ->
                QvxFieldHeader {
                    FieldName( meta.getColumnName(index) )
                    typeMapper.getFieldHeader(meta.getColumnClassName(index)).call(builder, meta.isNullable(index))
                    writers.push typeMapper.writeData(meta.getColumnClassName(index), meta.isNullable(index))
                }
            }
        }
    }

    def writeTableMetadata(meta) {
        def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))
        xml.QvxTableHeader {
            MajorVersion(1)
            MinorVersion(0)
            CreateUtcTime(dateFormatter.format(new java.util.Date()))
            TableName("SELECT ProductID, Name, ListPrice FROM AdventureWorks.Production.Product")
            UsesSeparatorByte(0)
            BlockSize(0)
            writeFieldHeaders(xml, meta)
        }

    }

    def writeHeaderDataSeperator() {
        outStream.write(HDSep)
    }

    def writeData(values, maxSize = 1024*1024) {
        ByteBuffer buf = ByteBuffer.allocate(maxSize)
        buf.order(ByteOrder.LITTLE_ENDIAN)
        (0..< values.size).each {
            def writer = writers[it]
            writer(values[it], buf)
        }

        def bytes = new byte[buf.position()]
        buf.rewind()
        buf.get(bytes)
        outStream.write(bytes)
    }

    def close() {
        writer.close()
    }

}


