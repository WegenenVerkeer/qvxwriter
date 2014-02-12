/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */
class QVXDbExportConfig {

    final String jdbcUrl

    final String dbUserName

    final String dbPassword

    final String jdbcDriver

    final String dbSchemaPattern

    final String dbTableNamePattern

    final String outputDir

    QVXDbExportConfig() {
        this("qvxDbExportConfig.properties")
    }

    QVXDbExportConfig(String propFile) {
        def props = new Properties()
        def inStream = this.class.getClassLoader().getResourceAsStream(propFile)
        if (inStream != null) {
            props.load(inStream)
        } else {
            throw new IOException( "Configuration file $propFile not found on classpath." )
        }

        this.jdbcUrl = props.getProperty("jdbcUrl")
        this.dbUserName = props.getProperty("dbUserName")
        this.dbPassword = props.getProperty("dbPassword")
        this.jdbcDriver = props.getProperty("jdbcDriver")
        this.dbSchemaPattern = props.getProperty("dbSchemaPattern")
        this.dbTableNamePattern = props.getProperty("dbTableNamePattern")
        this.outputDir = props.getProperty("outputDir")

    }

    String toString(){
        return "[jdbcUrl = $jdbcUrl, dbUserName=$dbUserName, dbPassword=$dbPassword, jdbcDriver=$jdbcDriver, " +
                "dbSchemaPattern=$dbSchemaPattern, dbTableNamePattern=$dbTableNamePattern, outputDir=$outputDir]"
    }

}
