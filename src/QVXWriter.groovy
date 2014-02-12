/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */

import groovy.xml.MarkupBuilder

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat

class QVXWriter {

    def typeMapper = new TypeMapper()
    def writers = [];
    OutputStream outStream
    OutputStreamWriter writer
    MarkupBuilder xml
    final private byte[] HDSep = [(byte) 0].toArray()

    def open(fName) {
        outStream = new FileOutputStream(fName, false)
        writer = new OutputStreamWriter(outStream, "UTF-8")
        xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration([standalone: "yes", version: "1.0", encoding: "UTF-8"])
    }


    def writeFieldHeaders(meta) {
        xml.Fields {
            (1..meta.columnCount).each { index ->
                QvxFieldHeader {
                    FieldName( meta.getColumnName(index) )
                    def className = meta.getColumnClassName(index)
                    if (className.contains("BigDecimal")) {
                        typeMapper.getFieldHeader(className).call(xml, meta.getScale(index), meta.isNullable(index))
                    } else {
                        typeMapper.getFieldHeader(className).call(xml, meta.isNullable(index))
                    }
                    writers.push typeMapper.writeData( meta.getColumnClassName(index), meta.isNullable(index) )
                }
            }
        }
    }

    def writeTableMetadata(meta, sqlStmt) {
        def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))
        xml.QvxTableHeader {
            MajorVersion(1)
            MinorVersion(0)
            CreateUtcTime(dateFormatter.format(new java.util.Date()))
            TableName(sqlStmt)
            UsesSeparatorByte(0)
            BlockSize(0)
            writeFieldHeaders(meta)
        }
    }

    def writeHeaderDataSeperator() {
        outStream.write(HDSep)
    }

    def writeData(values, maxSize = 1024*1024) {
        ByteBuffer buf = ByteBuffer.allocate(maxSize)
        buf.order(ByteOrder.LITTLE_ENDIAN)
        (0..< values.size() ).each {
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


