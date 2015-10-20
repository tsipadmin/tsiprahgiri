package com.tstracker.tstracker;


import android.provider.BaseColumns;
/**
 * Created by ali on 9/26/15.
 */
public class DatabaseContracts {
    public DatabaseContracts() {
    }

    public static abstract class Settings implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_logo = "logo";
        public static final String COLUMN_NAME_tell = "tell";
        public static final String COLUMN_NAME_key = "key";
        public static final String COLUMN_NAME_site = "site";
        public static final String COLUMN_NAME_days = "days";
        public static final String COLUMN_NAME_fromTime = "fromTime";
        public static final String COLUMN_NAME_endTime = "endTime";
        public static final String COLUMN_NAME_interval = "interval";
        public static final String COLUMN_NAME_RunningAlarm = "RunningAlarm";
        public static final String COLUMN_NAME_Accurate = "Accurate";

        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + Settings.TABLE_NAME + " (" +
                        Settings.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        Settings.COLUMN_NAME_days + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_endTime + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_fromTime + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_key + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_logo + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_site + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_tell + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_Accurate + " char(1) " + COMMA_SEP +
                        Settings.COLUMN_NAME_RunningAlarm + " INTEGER " + COMMA_SEP +
                        Settings.COLUMN_NAME_interval + " varchar(20) " +  " )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + Settings.TABLE_NAME;
    }
    public static abstract class AVLData implements BaseColumns {
        public static final String TABLE_NAME = "AVLData";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_Data = "data";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + AVLData.TABLE_NAME + " (" +
                        AVLData.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        AVLData.COLUMN_NAME_Data + " varchar(20) )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + AVLData.TABLE_NAME;
    }
}

