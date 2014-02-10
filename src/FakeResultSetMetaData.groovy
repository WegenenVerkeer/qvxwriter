/**
 * @author Karel Maesen, Geovise BVBA, 2014
 */
class FakeResultSetMetaData {
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
