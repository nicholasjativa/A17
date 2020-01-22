package com.zerochass.noble.a17;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity
        implements AddEditExpenseDialogFragment.AndroidSeventeenBaseDialogFragmentListener,
        NavigationView.OnNavigationItemSelectedListener {

    private ExpensesManagerService expensesManagerService = ExpensesManagerService.getInstance();
    private ExpensesCursorAdapter expensesCursorAdapter;
    private SQLiteDatabase db;
    private ExpensesListSQLHelper dbHelper;
    private FinanceSourceMonthlyAmountCursorAdapter financeSourceAdapter;
    private ListView lvExpenses;
    private ListView lvFinanceSourcesMonthBalances;
    private SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
    private String currentlySelectedMonthAndYear = format.format(new Date());
    private static final String[] contextMenuOptions = {"Edit", "Delete"};
    public static final String EDIT_EXPENSE_ROW_ID_EXTRA = "com.zerochass.noble.a17.EDIT_EXPENSE_ROW_ID";
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View view) -> {
        });

        setupDatePicker();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lvExpenses = findViewById(R.id.lvExpenses);
        registerForContextMenu(lvExpenses);

//        setupFinanceSourcesMonthBalances();
    }

    @Override
    public void onAndroidSeventeenBaseDialogFragmentPositiveClick(String dialogType) {
        expensesManagerService.updateMaps(currentlySelectedMonthAndYear);
        refreshAllData();

        String message = "";

        if (dialogType.equals(AddEditExpenseDialogFragment.ADD_DIALOG)) {
            message = getString(R.string.added_expense_snackbar_msg);
        } else if (dialogType.equals(AddEditExpenseDialogFragment.EDIT_DIALOG)) {
            message = getString(R.string.edited_expense_snackbar_msg);
        }

        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        for (int i = 0; i < contextMenuOptions.length; i++) {
            String option = contextMenuOptions[i];
            menu.add(view.getId(), i, ContextMenu.NONE, option);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View clickedItem = menuInfo.targetView;
//        int rowId = (int) clickedItem.getTag(R.id.ROW_ID);
//
//
//        String optionSelected = contextMenuOptions[menuItemId];
//        switch (optionSelected) {
//            case "Edit":
//                editExpense(rowId);
//                break;
//            case "Delete":
//                deleteExpense(rowId);
//                break;
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent nextActivityIntent;

        if (id == R.id.nav_daily_summary) {
            nextActivityIntent = new Intent(this, DailySummaryActivity.class);
            startActivity(nextActivityIntent);
        } else if (id == R.id.nav_monthly_summary) {

        } else if (id == R.id.nav_manage_credit_cards_bank_account) {
            nextActivityIntent = new Intent(this, ManageCreditCardsAndBankAccountActivity.class);
            startActivity(nextActivityIntent);
        } else if (id == R.id.nav_goals_budgeting) {

        } else if (id == R.id.nav_my_profile) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_sign_out) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void editExpense(int expenseRowId) {
        AddEditExpenseDialogFragment fragment = AddEditExpenseDialogFragment.newInstance(AddEditExpenseDialogFragment.EDIT_DIALOG, String.valueOf(expenseRowId));
//        fragment.show(getFragmentManager(), "EditExpenseDialogTag");
    }

    private void deleteExpense(int rowId) {
        db.delete(AndroidSeventeenContract.Expenses.TABLE_NAME, AndroidSeventeenContract.Expenses._ID + "=" + rowId, null);
        expensesManagerService.updateMaps(currentlySelectedMonthAndYear);

        refreshAllData();

        Snackbar.make(findViewById(android.R.id.content), R.string.deleted_expense_snackbar_msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void setupDatePicker() {
        final TextView tvMonthPicker = findViewById(R.id.tvMonthPicker);
        final Calendar calendar = Calendar.getInstance();
        final Context context = this;

        String dateString = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR);
        tvMonthPicker.setText(dateString);

        final DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                currentlySelectedMonthAndYear = format.format(calendar.getTime());

                String dateString = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR);
                tvMonthPicker.setText(dateString);

                expensesManagerService.updateMaps(currentlySelectedMonthAndYear);
                refreshAllData();
            }
        };

        tvMonthPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context, dpd, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setupFinanceSourcesMonthBalances() {
        Cursor cursor = db.rawQuery("SELECT "
                + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", " + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_TYPE + ", "
                + AndroidSeventeenContract.FinanceSources.TABLE_NAME + "." + AndroidSeventeenContract.Expenses._ID + ", "
                + AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE + ", "
                + " SUM(CAST(" + AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT + " AS REAL)) AS amount"
                + " FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME + " JOIN " + AndroidSeventeenContract.FinanceSources.TABLE_NAME
                + " ON " + AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE + " = "
                + AndroidSeventeenContract.FinanceSources.TABLE_NAME + "." + AndroidSeventeenContract.FinanceSources._ID
                + " WHERE strftime('%m/%Y', " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + "/1000, "
                + "'unixepoch') = " + "'" + currentlySelectedMonthAndYear + "'"
                + " GROUP BY " + AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE, null);

        lvFinanceSourcesMonthBalances = findViewById(R.id.lvFinanceSourcesMonthBalances);
        financeSourceAdapter = new FinanceSourceMonthlyAmountCursorAdapter(this, cursor, 0);
        lvFinanceSourcesMonthBalances.setAdapter(financeSourceAdapter);
    }

    public void refreshAllData() {
        Cursor cursor = db.rawQuery("SELECT "
                + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", " + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_TYPE + ", "
                + AndroidSeventeenContract.FinanceSources.TABLE_NAME + "." + AndroidSeventeenContract.Expenses._ID + ", "
                + AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE + ", "
                + " SUM(CAST(" + AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT + " AS REAL)) AS amount"
                + " FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME + " JOIN " + AndroidSeventeenContract.FinanceSources.TABLE_NAME
                + " ON " + AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE + " = "
                + AndroidSeventeenContract.FinanceSources.TABLE_NAME + "." + AndroidSeventeenContract.FinanceSources._ID
                + " WHERE strftime('%m/%Y', " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + "/1000, "
                + "'unixepoch') = " + "'" + currentlySelectedMonthAndYear + "'"
                + " GROUP BY " + AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE, null);
        financeSourceAdapter.swapCursor(cursor);
        financeSourceAdapter.notifyDataSetChanged();

        Cursor expensesCursor = db.rawQuery("SELECT " + AndroidSeventeenContract.Expenses.TABLE_NAME + ".*, "
                + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", " + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_TYPE
                + " FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME
                + " LEFT JOIN " + AndroidSeventeenContract.FinanceSources.TABLE_NAME + " ON "
                + AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE + " = " + AndroidSeventeenContract.FinanceSources.TABLE_NAME + "." + AndroidSeventeenContract.FinanceSources._ID
                + " WHERE strftime('%m/%Y', " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + "/1000, "
                + "'unixepoch') = " + "'" + currentlySelectedMonthAndYear + "'"
                + " ORDER BY " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + " DESC", null);
        expensesCursorAdapter.swapCursor(expensesCursor);
        expensesCursorAdapter.notifyDataSetChanged();
    }

    public class FinanceSourceMonthlyAmountCursorAdapter extends CursorAdapter {

        public FinanceSourceMonthlyAmountCursorAdapter(Context context, Cursor cursor, int flag) {
            super(context, cursor, flag);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvFinanceSourceName = view.findViewById(R.id.tvFinanceSourceName);
            TextView tvFinanceSourceBalance = view.findViewById(R.id.tvFinanceSourceBalance);

            String bankName = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_BANK_NAME));
            String cardName = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_CARD_NAME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_TYPE));
            String monthBalance = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT));

            String financeSourceName = bankName + " " + cardName + " " + type;

            tvFinanceSourceName.setText(financeSourceName);
            tvFinanceSourceBalance.setText("$" + String.format("%.2f", Double.parseDouble(monthBalance)));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_finance_source_item, parent, false);
            return view;
        }
    }

}
