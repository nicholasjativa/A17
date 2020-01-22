package com.zerochass.noble.a17;

import android.provider.BaseColumns;

/**
 * Created by noble on 3/30/18.
 */

public final class AndroidSeventeenContract {
    public static final int DATABASE_VERSION = 3;
    public static final String AUTHORITY = "com.zerochass.noble.a17";
    public static final String DATABASE_NAME = "a17.db";

    private AndroidSeventeenContract() {
    }

    public static final class Expenses implements BaseColumns {
        public static final String TABLE_NAME = "EXPENSES";
        public static final String COL_NAME_AMOUNT = "amount";
        public static final String COL_NAME_CATEGORY = "category";
        public static final String COL_NAME_DATE = "date";
        public static final String COL_NAME_DESCRIPTION= "description";
        public static final String COL_FINANCE_SOURCE = "finance_source";
        public static final String _ID = BaseColumns._ID;

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
                                                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                    + COL_NAME_DESCRIPTION + " TEXT, "
                                                    + COL_NAME_CATEGORY + " TEXT, "
                                                    + COL_NAME_DATE + " TEXT, "
                                                    + COL_NAME_AMOUNT + " TEXT, "
                                                    + COL_FINANCE_SOURCE + " INTEGER "
                                                    + " )";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String ALTER_EXPENSES_1 = "ALTER TABLE " + TABLE_NAME
                + " ADD COLUMN " + COL_FINANCE_SOURCE + " INTEGER";
    }

    public static final class FinanceSources implements BaseColumns {
        public static final String TABLE_NAME = "FINANCE_SOURCES";
        public static final String COL_BANK_NAME = "bank_name";
        public static final String COL_CARD_NAME = "card_name";
        public static final String COL_DIGITS = "digits";
        public static final String COL_PAYMENT_NETWORK = "payment_network";
        public static final String COL_TYPE = "type";
        public static final String _ID = BaseColumns._ID;

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
                                                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                    + COL_BANK_NAME + " TEXT, "
                                                    + COL_CARD_NAME + " TEXT, "
                                                    + COL_DIGITS + " TEXT, "
                                                    + COL_PAYMENT_NETWORK + " TEXT, "
                                                    + COL_TYPE + " TEXT "
                                                    + " )";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }


}
