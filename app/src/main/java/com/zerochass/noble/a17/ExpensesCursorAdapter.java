package com.zerochass.noble.a17;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by noble on 3/30/18.
 */

public class ExpensesCursorAdapter extends CursorAdapter {
    private Double monthlyBalance = 0.00;
    private int ROW_WITH_HEADER = 0;
    private int ROW_WITH_FOOTER = 1;
    private int ROW_REGULAR = 2;
    private int ROW_WITH_HEADER_AND_FOOTER = 3;
    private int ROW_WITH_HEADER_AND_DAY_AND_FOOTER = 4;
    private int ROW_WITH_HEADER_AND_DAY = 5;
    private int ROW_WITH_DAY_AND_FOOTER = 6;
    private int ROW_WITH_DAY = 7;
    private ExpensesManagerService expensesManagerService = ExpensesManagerService.getInstance();
    private Map<String, String> headerPositionsMap;
    private Map<String, String> dayPositionsMap;
    private Map<String, String> datePositionsMap;
    private Map<String, String> footerPositionsMap;


    public ExpensesCursorAdapter(Context context, Cursor cursor, int flag) {
        super(context, cursor, flag);
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    @Override
    public int getItemViewType(int position) {
        this.headerPositionsMap = expensesManagerService.getHeaderPositionsMap();
        this.datePositionsMap = expensesManagerService.getDatePositionsMap();
        this.dayPositionsMap = expensesManagerService.getDayPositionsMap();
        this.footerPositionsMap = expensesManagerService.getFooterPositionsMap();

        String pos = String.valueOf(position);
        boolean hasMonthHeader = this.headerPositionsMap.get(pos) != null;
        boolean hasDayHeader = this.dayPositionsMap.get(pos) != null;
        boolean hasMonthFooter = this.footerPositionsMap.get(pos) != null;

        if (hasMonthHeader && hasDayHeader && hasMonthFooter) {
            return ROW_WITH_HEADER_AND_DAY_AND_FOOTER;
        } else if (hasMonthHeader && hasDayHeader) {
            return ROW_WITH_HEADER_AND_DAY;
        } else if (hasMonthFooter && hasDayHeader) {
            return ROW_WITH_DAY_AND_FOOTER;
        } else if (hasDayHeader) {
            return ROW_WITH_DAY;
        } else if (hasMonthHeader && hasMonthFooter) {
            return ROW_WITH_HEADER_AND_FOOTER;
        } else if (hasMonthHeader) {
            return ROW_WITH_HEADER;
        } else if (hasMonthFooter) {
            return ROW_WITH_FOOTER;
        } else {
            return ROW_REGULAR;
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final int position = cursor.getPosition();
        final int type = getItemViewType(position);
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_expenses, parent, false);
        RelativeLayout rl = view.findViewById(R.id.rl_expense_list_item_base);


        if (type == this.ROW_WITH_HEADER) {
            TextView tvMonthHeader = view.findViewById(R.id.tvMonthHeader);
            tvMonthHeader.setVisibility(View.VISIBLE);
        } else if (type == this.ROW_WITH_FOOTER) {
            LinearLayout footer = view.findViewById(R.id.ll_footer);
            footer.setVisibility(View.VISIBLE);

        } else if (type == this.ROW_WITH_HEADER_AND_FOOTER) {
            TextView tvMonthHeader = view.findViewById(R.id.tvMonthHeader);
            tvMonthHeader.setVisibility(View.VISIBLE);
            LinearLayout footer = view.findViewById(R.id.ll_footer);
            footer.setVisibility(View.VISIBLE);

        } else if (type == this.ROW_WITH_HEADER_AND_DAY_AND_FOOTER) {
            TextView tvMonthHeader = view.findViewById(R.id.tvMonthHeader);
            tvMonthHeader.setVisibility(View.VISIBLE);
            LinearLayout footer = view.findViewById(R.id.ll_footer);
            footer.setVisibility(View.VISIBLE);
            LinearLayout dayHeader = view.findViewById(R.id.ll_day_header);
            dayHeader.setVisibility(View.VISIBLE);

        } else if (type == this.ROW_WITH_HEADER_AND_DAY) {
            TextView tvMonthHeader = view.findViewById(R.id.tvMonthHeader);
            tvMonthHeader.setVisibility(View.VISIBLE);
            LinearLayout dayHeader = view.findViewById(R.id.ll_day_header);
            dayHeader.setVisibility(View.VISIBLE);
        } else if (type == this.ROW_WITH_DAY_AND_FOOTER) {
            LinearLayout dayHeader = view.findViewById(R.id.ll_day_header);
            dayHeader.setVisibility(View.VISIBLE);
            LinearLayout footer = view.findViewById(R.id.ll_footer);
            footer.setVisibility(View.VISIBLE);

        } else if (type == this.ROW_WITH_DAY) {
            LinearLayout dayHeader = view.findViewById(R.id.ll_day_header);
            dayHeader.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvExpenseCategory = view.findViewById(R.id.tvExpenseCategory);
        TextView tvExpenseDescription = view.findViewById(R.id.tvExpenseDescription);
        TextView tvExpenseAmount = view.findViewById(R.id.tvExpenseAmount);
        TextView tvExpenseFinanceSource = view.findViewById(R.id.tvExpenseFinanceSource);

        String category = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_CATEGORY));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_DESCRIPTION));
        String amount = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_DATE));
        String bankName = cursor.getString(cursor.getColumnIndex(AndroidSeventeenContract.FinanceSources.COL_BANK_NAME));
        String cardName = cursor.getString(cursor.getColumnIndex(AndroidSeventeenContract.FinanceSources.COL_CARD_NAME));
        String financeType = cursor.getString(cursor.getColumnIndex(AndroidSeventeenContract.FinanceSources.COL_TYPE));
        int id = cursor.getInt(cursor.getColumnIndex(AndroidSeventeenContract.Expenses._ID));

        Long dateLong = Long.parseLong(date);
        String financeSourceName = bankName + " " + cardName + " " + financeType;
        final int position = cursor.getPosition();
        final int type = getItemViewType(position);

//        view.setTag(R.id.ROW_POSITION, position);
//        view.setTag(R.id.ROW_ID, id);
        tvExpenseCategory.setText(category);
        tvExpenseDescription.setText(description);
        tvExpenseAmount.setText("$" + amount);
        tvExpenseFinanceSource.setText(financeSourceName);

        if (type == this.ROW_WITH_HEADER) {
            TextView tvMonthHeader = view.findViewById(R.id.tvMonthHeader);
            String nameOfMonth = this.headerPositionsMap.get(String.valueOf(position));
            tvMonthHeader.setText(nameOfMonth);
        } else if (type == this.ROW_WITH_FOOTER) {
            TextView tvMonthFooter = view.findViewById(R.id.tvMonthBalanceFooter);
            Double monthBalance = Double.parseDouble(this.footerPositionsMap.get(String.valueOf(position)));
            tvMonthFooter.setText("$" + String.format("%.2f", monthBalance));
        } else if (type == this.ROW_WITH_HEADER_AND_FOOTER) {
            TextView tvMonthHeader = view.findViewById(R.id.tvMonthHeader);
            String nameOfMonth = this.headerPositionsMap.get(String.valueOf(position));
            tvMonthHeader.setText(nameOfMonth);

            TextView tvMonthFooter = view.findViewById(R.id.tvMonthBalanceFooter);
            Double monthBalance = Double.parseDouble(this.footerPositionsMap.get(String.valueOf(position)));
            tvMonthFooter.setText("$" + String.format("%.2f", monthBalance));
        } else if (type == this.ROW_WITH_HEADER_AND_DAY_AND_FOOTER) {
            TextView tvMonthHeader = view.findViewById(R.id.tvMonthHeader);
            String nameOfMonth = this.headerPositionsMap.get(String.valueOf(position));
            tvMonthHeader.setText(nameOfMonth);

            TextView tvDay = view.findViewById(R.id.tvDay);
            String nameOfDay = this.dayPositionsMap.get(String.valueOf(position));
            tvDay.setText(nameOfDay);

            TextView tvDate = view.findViewById(R.id.tvDate);
            String dateStr = this.datePositionsMap.get(String.valueOf(position));
            tvDate.setText(dateStr);

            TextView tvMonthFooter = view.findViewById(R.id.tvMonthBalanceFooter);
            Double monthBalance = Double.parseDouble(this.footerPositionsMap.get(String.valueOf(position)));
            tvMonthFooter.setText("$" + String.format("%.2f", monthBalance));
        } else if (type == this.ROW_WITH_HEADER_AND_DAY) {
            TextView tvMonthHeader = view.findViewById(R.id.tvMonthHeader);
            String nameOfMonth = this.headerPositionsMap.get(String.valueOf(position));
            tvMonthHeader.setText(nameOfMonth);

            TextView tvDay = view.findViewById(R.id.tvDay);
            String nameOfDay = this.dayPositionsMap.get(String.valueOf(position));
            tvDay.setText(nameOfDay);

            TextView tvDate = view.findViewById(R.id.tvDate);
            String dateStr = this.datePositionsMap.get(String.valueOf(position));
            tvDate.setText(dateStr);
        } else if (type == this.ROW_WITH_DAY_AND_FOOTER) {
            TextView tvDay = view.findViewById(R.id.tvDay);
            String nameOfDay = this.dayPositionsMap.get(String.valueOf(position));
            tvDay.setText(nameOfDay);

            TextView tvDate = view.findViewById(R.id.tvDate);
            String dateStr = this.datePositionsMap.get(String.valueOf(position));
            tvDate.setText(dateStr);

            TextView tvMonthFooter = view.findViewById(R.id.tvMonthBalanceFooter);
            Double monthBalance = Double.parseDouble(this.footerPositionsMap.get(String.valueOf(position)));
            tvMonthFooter.setText("$" + String.format("%.2f", monthBalance));
        } else if (type == this.ROW_WITH_DAY) {
            TextView tvDay = view.findViewById(R.id.tvDay);
            String nameOfDay = this.dayPositionsMap.get(String.valueOf(position));
            tvDay.setText(nameOfDay);

            TextView tvDate = view.findViewById(R.id.tvDate);
            String dateStr = this.datePositionsMap.get(String.valueOf(position));
            tvDate.setText(dateStr);
        }
    }


}
