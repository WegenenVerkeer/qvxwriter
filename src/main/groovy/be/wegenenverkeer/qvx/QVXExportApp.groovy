package be.wegenenverkeer.qvx
/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */
class QVXExportApp {

    def static main(args) {
        def configFileName = ( args.length == 0 ? null : args[0] )
        def exporter = new QVXDbExporter(configFileName)
        exporter.run()
    }

}
