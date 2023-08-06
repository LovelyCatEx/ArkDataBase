package com.lovelycatv.ark.compiler;

public class ProcessorVars {
    private static final String PACKAGE_NAME = "com.lovelycatv.ark";

    private static final String PACKAGE_NAME_DAO = "com.lovelycatv.ark";

    private static final String TYPE_CONVERTER_CLASSNAME = "TypeConverters";

    public static String getPackageName(String databaseName) {
        return PACKAGE_NAME + "." + databaseName.toLowerCase();
    }

    public static String getDAOPackageName(String databaseName) {
        return PACKAGE_NAME_DAO + "." + databaseName.toLowerCase();
    }

    public static String getTypeConverterClassname(String databaseName) {
        return databaseName + TYPE_CONVERTER_CLASSNAME;
    }
}
