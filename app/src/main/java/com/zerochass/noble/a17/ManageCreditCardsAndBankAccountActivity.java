package com.zerochass.noble.a17;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class ManageCreditCardsAndBankAccountActivity extends AppCompatActivity implements ManageFinanceSourceDialogFragment.ManageFinanceSourceDialogFragmentListener {
    private ExpensesListSQLHelper dbHelper;
    private SQLiteDatabase db;
    private ListView lvFinanceSources;
    private FinanceSourcesCursorAdapter financeSourcesCursorAdapter;
    private String [] contextMenuOptions = { "Edit", "Remove" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_credit_cards_and_bank_account);
        Toolbar toolbar = findViewById(R.id.toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManageFinanceSourceDialogFragment dialog = ManageFinanceSourceDialogFragment.newInstance(ManageFinanceSourceDialogFragment.ADD_DIALOG, null);
                dialog.show(getSupportFragmentManager(), "ManageFinanceSourceDialogFragment");
            }
        });

        dbHelper = new ExpensesListSQLHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + AndroidSeventeenContract.FinanceSources._ID + ", "
                                        + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", "
                                        + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME + ", "
                                        + AndroidSeventeenContract.FinanceSources.COL_TYPE
                                        + " FROM " + AndroidSeventeenContract.FinanceSources.TABLE_NAME, null);



        lvFinanceSources = findViewById(R.id.lvFinanceSources);
        financeSourcesCursorAdapter = new FinanceSourcesCursorAdapter(this, cursor, 0);
        lvFinanceSources.setAdapter(financeSourcesCursorAdapter);
        registerForContextMenu(lvFinanceSources);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        for (int i = 0; i < contextMenuOptions.length; i++) {
            String option = contextMenuOptions[i];
            menu.add(view.getId(), i, ContextMenu.NONE, option);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View clickedItem = menuInfo.targetView;
//        int financeSourceRowId = (int) clickedItem.getTag(R.id.FINANCE_SOURCE_ROW_ID);
//
//        String selectedOption = contextMenuOptions[menuItemId];
//        switch(selectedOption) {
//            case "Edit":
//                            editFinanceSource(financeSourceRowId);
//                            break;
//            case "Remove":
//                            removeFinanceSource(financeSourceRowId);
//                            break;
//        }

        return true;
    }

    @Override
    public void onManageFinanceSourceDialogFragmentPositiveClick(String dialogType) {
        Cursor cursor = db.rawQuery("SELECT " + AndroidSeventeenContract.FinanceSources._ID + ", "
                + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_TYPE
                + " FROM " + AndroidSeventeenContract.FinanceSources.TABLE_NAME, null);
        financeSourcesCursorAdapter.swapCursor(cursor);

        String message = "";

        if (dialogType.equals(ManageFinanceSourceDialogFragment.ADD_DIALOG)) {
            message = "Added Finance Source Successfully";
        } else if (dialogType.equals(ManageFinanceSourceDialogFragment.EDIT_DIALOG)) {
            message = "Updated Finance Source Successfully";
        }

        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void editFinanceSource(int rowId) {
        ManageFinanceSourceDialogFragment dialog = ManageFinanceSourceDialogFragment.newInstance(ManageFinanceSourceDialogFragment.EDIT_DIALOG, String.valueOf(rowId));
        dialog.show(getSupportFragmentManager(), "EditManageFinanceSourceDialogFragment");
    }

    public void removeFinanceSource(int rowId) {
        db.delete(AndroidSeventeenContract.FinanceSources.TABLE_NAME, AndroidSeventeenContract.FinanceSources._ID + " = " + rowId, null);
        Cursor cursor = db.rawQuery("SELECT " + AndroidSeventeenContract.FinanceSources._ID + ", "
                + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_TYPE
                + " FROM " + AndroidSeventeenContract.FinanceSources.TABLE_NAME, null);

        financeSourcesCursorAdapter.swapCursor(cursor);
        financeSourcesCursorAdapter.notifyDataSetChanged();
        Snackbar.make(findViewById(android.R.id.content), "Deleted Source Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }



    public class FinanceSourcesCursorAdapter extends CursorAdapter {

        public FinanceSourcesCursorAdapter(Context context, Cursor cursor, int flag) {
            super(context, cursor, flag);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_finance_source, null, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvFinanceSourceName = view.findViewById(R.id.tvFinanceSourceName);

            int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources._ID));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_BANK_NAME));
            String cardName = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_CARD_NAME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_TYPE));

            String financeSourceName = bankName + " " + cardName + " " + type;
            tvFinanceSourceName.setText(financeSourceName);
//            view.setTag(R.id.FINANCE_SOURCE_ROW_ID, rowId);
        }
    }
}
