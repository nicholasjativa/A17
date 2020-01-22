package com.zerochass.noble.a17;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by noble on 4/8/18.
 */

class ExpensesManagerService {
    private Context context;
    private ExpensesListSQLHelper dbHelper;
    private Map<String, String> headerPositionsMap;
    private Map<String, String> dayPositionsMap;
    private Map<String, String> datePositionsMap;
    private Map<String, String> footerPositionsMap;
    private Map<String, Double> financeSourcesAmountMap;
    private SQLiteDatabase db;
    private static final ExpensesManagerService ourInstance = new ExpensesManagerService();

    static ExpensesManagerService getInstance() {
        return ourInstance;
    }

    private ExpensesManagerService() {
    }

    public Map<String, String> getHeaderPositionsMap() {
        return headerPositionsMap;
    }

    public Map<String, String> getDayPositionsMap() {
        return dayPositionsMap;
    }

    public Map<String, String> getDatePositionsMap() {
        return datePositionsMap;
    }

    public Map<String, String> getFooterPositionsMap() {
        return footerPositionsMap;
    }

    public Map<String, Double> getFinanceSourcesAmountMap() { return financeSourcesAmountMap; }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setContext(final Context c, final String currentlySelectedMonthAndYear) {
        context = c;
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper = new ExpensesListSQLHelper(context);
                db = dbHelper.getWritableDatabase();
                Cursor expensesCursor = db.rawQuery("SELECT * FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME
                        + " WHERE strftime('%m/%Y', " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + "/1000, "
                        + "'unixepoch') = " + "'" + currentlySelectedMonthAndYear + "'"
                        + " ORDER BY " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + " DESC", null);
                calculateHeaderAndFooterPositions(expensesCursor);
            }
        }).start();
    }

    public void updateMaps(final String currentlySelectedMonthAndYear) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor expensesCursor = db.rawQuery("SELECT * FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME
                        + " WHERE strftime('%m/%Y', " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + "/1000, "
                        + "'unixepoch') = " + "'" + currentlySelectedMonthAndYear + "'"
                        + " ORDER BY " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + " DESC", null);
                calculateHeaderAndFooterPositions(expensesCursor);
            }
        }).start();

    }

    public void calculateHeaderAndFooterPositions(Cursor cursor) {
        if (cursor.getCount() == 0) return;
        headerPositionsMap = new HashMap<>();
        dayPositionsMap = new HashMap<>();
        datePositionsMap = new HashMap<>();
        footerPositionsMap = new HashMap<>();
        financeSourcesAmountMap = new HashMap<>();
        cursor.moveToFirst();
        Double monthBalance = 0.00;
        Calendar calendar = Calendar.getInstance();

        Long dateLong = null;
        Long prevDateLong = null;
        String nameOfDay = null;
        String nameOfMonth = null;
        Double amount;
        int financeSourceId;
        int count = 0;

        dateLong = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_DATE)));
        calendar.setTimeInMillis(dateLong);

        int positionToInsertHeader = count;
        nameOfMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String sPositionToInsertHeader = String.valueOf(positionToInsertHeader);
        headerPositionsMap.put(sPositionToInsertHeader, nameOfMonth);

        while (cursor.isAfterLast() == false) {

            amount = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT)));
            dateLong = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_DATE)));
            calendar.setTime(new Date(dateLong));
            financeSourceId = cursor.getInt(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE));

            String sFinanceId = String.valueOf(financeSourceId);

            if (prevDateLong == null) {
                int positionToInsertDayHeader = count;
                nameOfDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                String date = new SimpleDateFormat("MM/dd/yyy").format(new Date(dateLong));

                dayPositionsMap.put(String.valueOf(positionToInsertDayHeader), nameOfDay);
                datePositionsMap.put(String.valueOf(positionToInsertDayHeader), date);
            } else if (!prevDateLong.equals(dateLong)) {
                int positionToInsertDayHeader = count;
                nameOfDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                String date = new SimpleDateFormat("MM/dd/yyy").format(new Date(dateLong));

                dayPositionsMap.put(String.valueOf(positionToInsertDayHeader), nameOfDay);
                datePositionsMap.put(String.valueOf(positionToInsertDayHeader), date);
            }

            if (financeSourcesAmountMap.get(sFinanceId) == null) {
                financeSourcesAmountMap.put(sFinanceId, amount);
            } else {
                Double currentAmountForSource = financeSourcesAmountMap.get(sFinanceId);
                Double newAmountForSource = currentAmountForSource + amount;
                financeSourcesAmountMap.put(sFinanceId, newAmountForSource);
            }

            monthBalance = monthBalance + amount;
            prevDateLong = dateLong;
            count++;
            cursor.moveToNext();
        }
        if (cursor.isAfterLast()) {
            int positionToInsertFooter = count - 1;
            String sPositionToInsertFooter = String.valueOf(positionToInsertFooter);
            String sMonthBalance = String.valueOf(monthBalance);
            footerPositionsMap.put(sPositionToInsertFooter, sMonthBalance);
        }
    }

}
