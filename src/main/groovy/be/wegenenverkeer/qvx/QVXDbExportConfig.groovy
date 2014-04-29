package be.wegenenverkeer.qvx
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

    QVXDbExportConfig(OptionAccessor options) {
        this.jdbcUrl = options.jdbcUrl
        this.dbUserName = options.dbUserName
        this.dbPassword = options.dbPassword
        this.jdbcDriver = options.jdbcDriver
        this.dbSchemaPattern = options.dbSchemaPattern
        this.dbTableNamePattern = options.dbTableNamePattern
        this.outputDir = options.out
    }

    String toString(){
        return "[jdbcUrl = $jdbcUrl, dbUserName=$dbUserName, dbPassword=$dbPassword, jdbcDriver=$jdbcDriver, " +
                "dbSchemaPattern=$dbSchemaPattern, dbTableNamePattern=$dbTableNamePattern, outputDir=$outputDir]"
    }

}
