package be.wegenenverkeer.qvx
/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */
class QVXExportApp {

    def static main(args) {

        def cli = new CliBuilder(usage:'qvxwriter <options> ')
        cli.help("Prints help")
        cli.jdbcUrl(args: 1, "JDBC Url")
        cli.jdbcDriver(args:1, "JDBC Driver class name")
        cli.dbUserName(args:1, "Database user name")
        cli.dbPassword(args:1, "Database user password")
        cli.dbSchemaPattern(args:1, "database schema pattern")
        cli.dbTableNamePattern(args:1, "table name pattern")
        cli.out(args:1, "output directory")

        def options = cli.parse(args)

        if (options.help) {
            println(cli.usage())
        }

        def exporter = new QVXDbExporter(options)
        exporter.run()
    }

}
