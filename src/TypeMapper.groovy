import java.nio.charset.Charset
import java.text.SimpleDateFormat

class TypeMapper {
    def typeMap = [:]

    def addType(clazz, fieldHeader, dataFormatter) {
        typeMap[clazz] = ['header': fieldHeader, 'formatter': dataFormatter]
    }

    def getFieldHeader(clazz) {
        typeMap[clazz]['header']
    }

    def getFieldDataFormatter(clazz) {
        typeMap[clazz]['formatter']
    }

    def markNullRepr = { builder, isNullable ->
        if (isNullable) {
            builder.NullRepresentation("QVX_NULL_FLAG_SUPPRESS_DATA")
        } else {
            builder.NullRepresentation("QVX_NULL_NEVER")
        }
    }

    def writeData(clazz, isNullable) {
        return { value, buffer ->
            if (isNullable) {
                if (value == null) {
                    buffer.put((byte) 1)
                } else {
                    buffer.put((byte) 0)
                    getFieldDataFormatter(clazz).call(value, buffer)
                }
            } else {
                getFieldDataFormatter(clazz).call(value, buffer)
            }

        }
    }

    TypeMapper() {

        //Add how to handle Long objects
        addType(Long.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_SIGNED_INTEGER")
                    builder.Extent("QVX_FIX")
                    markNullRepr(builder, isNullable)
                    builder.BigEndian(0)
                    builder.ByteWidth(8)
                    builder.FieldFormat {
                        Type("INTEGER")
                        nDec(0)
                    }
                },
                { value, buffer -> buffer.putLong(value) }
        )

        addType(Double.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_IEEE_REAL")
                    builder.Extent("QVX_FIX")
                    markNullRepr(builder, isNullable)
                    builder.BigEndian(0)
                    builder.ByteWidth(8)
                    builder.FieldFormat {
                        Type("REAL")
                    }
                },
                { value, buffer -> buffer.putDouble(value) }
        )

        addType(Float.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_IEEE_REAL")
                    builder.Extent("QVX_FIX")
                    markNullRepr(builder, isNullable)
                    builder.BigEndian(0)
                    builder.ByteWidth(4)
                    builder.FieldFormat {
                        Type("REAL")
                    }
                },
                { value, buffer -> buffer.putFloat(value) }
        )

        addType(String.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_TEXT")
                    builder.EXTENT("QVX_ZERO_TERMINATED")
                    markNullRepr(builder, isNullable)
                    builder.FieldFormat {
                        Type("ASCII")
                    }
                },
                { value, buffer ->
                    buffer.put(value.getBytes(Charset.forName("UTF-8")))
                    buffer.put((byte) 0)
                }
        )

        addType(Boolean.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_TEXT")
                    builder.EXTENT("QVX_ZERO_TERMINATED")
                    markNullRepr(builder, isNullable)
                    builder.FieldFormat {
                        Type("ASCII")
                    }
                },
                { value, buffer ->
                    String s = value ? "J" : "N"
                    buffer.put(s.getBytes(Charset.forName("UTF-8")))
                    buffer.put((byte) 0)
                }
        )


        addType(Integer.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_SIGNED_INTEGER")
                    builder.Extent("QVX_FIX")
                    markNullRepr(builder, isNullable)
                    builder.BigEndian(0)
                    builder.ByteWidth(4)
                    builder.FieldFormat {
                        Type("INTEGER")
                        nDec(0)
                    }
                },
                { value, buffer -> buffer.putInt(value) }
        )

        addType(java.sql.Date.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_TEXT")
                    builder.Extent("QVX_ZERO_TERMINATED")
                    markNullRepr(builder, isNullable)
                    builder.FieldFormat {
                        Type("DATE")
                        Fmt("YYYY-M-D")
                    }
                },
                { value, buffer ->
                    def dateFormatter = new SimpleDateFormat("yyyy-MM-dd")
                    buffer.put(dateFormatter.format(new java.util.Date(value.getTime())).getBytes(Charset.forName("UTF-8")))
                    buffer.put((byte) 0)
                }
        )

        addType(java.sql.Time.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_TEXT")
                    builder.Extent("QVX_ZERO_TERMINATED")
                    markNullRepr(builder, isNullable)
                    builder.FieldFormat {
                        Type("TIME")
                        Fmt("h:mm:ss")
                    }
                },
                { value, buffer ->
                    def dateFormatter = new SimpleDateFormat("h:m:s")
                    buffer.put(dateFormatter.format(new java.util.Date(value.getTime())).getBytes(Charset.forName("UTF-8")))
                    buffer.put((byte) 0)
                }
        )

        addType(java.sql.Timestamp.class.getCanonicalName(),
                { builder, isNullable ->
                    builder.Type("QVX_TEXT")
                    builder.Extent("QVX_ZERO_TERMINATED")
                    markNullRepr(builder, isNullable)
                    builder.FieldFormat {
                        Type("TIMESTAMP")
                        Fmt("YYYY-M-D h:mm:ss")
                    }
                },
                { value, buffer ->
                    def dateFormatter = new SimpleDateFormat("yyyy-MM-dd h:m:s")
                    buffer.put(dateFormatter.format(new java.util.Date(value.getTime())).getBytes(Charset.forName("UTF-8")))
                    buffer.put((byte) 0)
                }
        )

        addType(java.math.BigDecimal.class.getCanonicalName(),
                { builder, scale, isNullable ->
                    builder.Type("QVX_SIGNED_INTEGER")
                    builder.Extent("QVX_FIX")
                    markNullRepr(builder, isNullable)
                    builder.BigEndian(0)
                    builder.ByteWidth(8)
                    builder.FixPointDecimals(scale)
                    builder.FieldFormat {
                        Type("FIX")
//                        nDec(scale)
                    }
                },
                { value, buffer -> buffer.putLong(value.unscaledValue().longValue()) }
        )


    }
}
