package com.zerochass.noble.a17;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by noble on 3/30/18.
 */

public class ExpensesListSQLHelper extends SQLiteOpenHelper {

    public ExpensesListSQLHelper(Context context) {
        // 1 is the expenses list database version
        super(context, AndroidSeventeenContract.DATABASE_NAME, null, AndroidSeventeenContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AndroidSeventeenContract.Expenses.CREATE_TABLE);
        db.execSQL(AndroidSeventeenContract.FinanceSources.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        onCreate(db);

        if (oldVer < 3) {
            db.execSQL(AndroidSeventeenContract.Expenses.ALTER_EXPENSES_1);
        }
    }
}
