package com.zerochass.noble.a17;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by noble on 4/1/18.
 */

public class AddEditExpenseDialogFragment extends DialogFragment {
    private AlertDialog dialog;
    private ExpensesListSQLHelper expensesListSQLHelper;
    private AndroidSeventeenBaseDialogFragmentListener mListener;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private int mExpenseRowId;
    private long mFinanceSourceId;
    private SQLiteDatabase db;
    public static final String DIALOG_TYPE_KEY = "DIALOG_TYPE";
    public static final String EDIT_DIALOG = "EDIT";
    public static final String ADD_DIALOG = "ADD";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (AndroidSeventeenBaseDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AndroidSeventeenBaseDialogFragmentListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        expensesListSQLHelper = new ExpensesListSQLHelper(getActivity());
        db = expensesListSQLHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();

        String title = "";
        String positiveButtonText = "";

        final String dialogType = bundle.getString(DIALOG_TYPE_KEY);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_base_expense_dialog, null);

        populateSpinners(view);

        if (dialogType.equals(ADD_DIALOG)) {
            title = getString(R.string.add_expense_title);
            positiveButtonText = getString(R.string.add_expense_positive_button);
            Long dateLong = new Date().getTime();
            String dateString = mDateFormat.format(new Date(dateLong));
            EditText etDate = view.findViewById(R.id.etDate);
            etDate.setText(dateString);
        } else if (dialogType.equals(EDIT_DIALOG)) {
            title = getString(R.string.edit_expense_title);
            positiveButtonText = getString(R.string.edit_expense_positive_button);
            mExpenseRowId = bundle.getInt("id");
            loadData(mExpenseRowId, view);
        }

        builder.setView(view);
        setupDatePicker(view);
        builder.setTitle(title);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveData(view, dialogType);
            }
        });

        dialog = builder.create();
        return dialog;
    }

    public static AddEditExpenseDialogFragment newInstance(String dialogType, String expenseRowId) {
        AddEditExpenseDialogFragment fragment = new AddEditExpenseDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TYPE_KEY, dialogType);

        if (expenseRowId != null) {
            int parsedId = Integer.parseInt(expenseRowId);
            args.putInt("id", parsedId);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public void loadData(int rowId, View view) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME
                + " WHERE " + AndroidSeventeenContract.Expenses._ID + " = " + rowId, null);
        cursor.moveToNext();

        String category = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_CATEGORY));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_DESCRIPTION));
        String amount = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_DATE));
        int financeSourceId = cursor.getInt(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE));

        Long dateLong = Long.parseLong(date);
        String dateString = mDateFormat.format(new Date(dateLong));

        Spinner sExpenseCategory = view.findViewById(R.id.sCategory);
        Spinner sFinanceSource = view.findViewById(R.id.sFinanceSource);
        EditText etExpenseDate = view.findViewById(R.id.etDate);
        EditText etExpenseDescription = view.findViewById(R.id.etDescription);
        EditText etExpenseAmount = view.findViewById(R.id.etAmount);

        sFinanceSource.setSelection(financeSourceId - 1); // for now until i figure out how to do it with ID from db
        sExpenseCategory.setSelection(((ArrayAdapter) sExpenseCategory.getAdapter()).getPosition(category));
        etExpenseDescription.setText(description);
        etExpenseAmount.setText(amount);
        etExpenseDate.setText(dateString);

        cursor.close();
    }

    public void populateSpinners(View view) {
        Spinner sCategory = view.findViewById(R.id.sCategory);
        Spinner sFinanceSource = view.findViewById(R.id.sFinanceSource);

        sFinanceSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mFinanceSourceId = (int) l;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Cursor cursor = db.rawQuery("SELECT " + AndroidSeventeenContract.FinanceSources._ID + ", "
                + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_TYPE + ", "
                + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME
                + " FROM " + AndroidSeventeenContract.FinanceSources.TABLE_NAME, null);

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.expense_categories, android.R.layout.simple_spinner_item);
        SimpleCursorAdapter financeSourcesAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, cursor, new String[]{AndroidSeventeenContract.FinanceSources.COL_BANK_NAME}, new int[]{android.R.id.text1}, 0);
        financeSourcesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                String bankName = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_BANK_NAME));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_TYPE));
                String cardName = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_CARD_NAME));

                TextView dropdown = (TextView) view;
                dropdown.setText(bankName + " " + cardName + " " + type);
                return true;
            }
        });

        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        financeSourcesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sCategory.setAdapter(categoriesAdapter);
        sFinanceSource.setAdapter(financeSourcesAdapter);
    }

    public void saveData(View view, String dialogType) {
        ContentValues contentValues = new ContentValues();
        Long dateLong = new Long(0);

        Spinner sCategory = view.findViewById(R.id.sCategory);
        EditText etDescription = view.findViewById(R.id.etDescription);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etDate = view.findViewById(R.id.etDate);

        String category = sCategory.getSelectedItem().toString();
        String description = etDescription.getText().toString();
        String amount = etAmount.getText().toString();
        String date = etDate.getText().toString();

        if (category.equals("") || description.equals("") || amount.equals("") || date.equals("")) {
            Toast.makeText(getActivity(), "Fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            amount = String.format("%.2f", Double.parseDouble(amount));
            try {
                dateLong = mDateFormat.parse(date).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        contentValues.put(AndroidSeventeenContract.Expenses.COL_NAME_CATEGORY, category);
        contentValues.put(AndroidSeventeenContract.Expenses.COL_NAME_DESCRIPTION, description);
        contentValues.put(AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT, amount);
        contentValues.put(AndroidSeventeenContract.Expenses.COL_NAME_DATE, dateLong);
        contentValues.put(AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE, mFinanceSourceId);

        if (dialogType.equals(ADD_DIALOG)) {
            db.insertOrThrow(AndroidSeventeenContract.Expenses.TABLE_NAME, null, contentValues);
        } else if (dialogType.equals(EDIT_DIALOG)) {
            db.update(AndroidSeventeenContract.Expenses.TABLE_NAME, contentValues, AndroidSeventeenContract.Expenses._ID + "=" + mExpenseRowId, null);
        }

        mListener.onAndroidSeventeenBaseDialogFragmentPositiveClick(dialogType);
    }

    public void setupDatePicker(View view) {
        final EditText etDate = view.findViewById(R.id.etDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker dView, int year, int month, int day) {
                        int realMonth = month + 1;
                        String dateString = realMonth + "/" + day + "/" + year;
                        etDate.setText(dateString);
                    }
                };


                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), dpd, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
    }

    public interface AndroidSeventeenBaseDialogFragmentListener {
        void onAndroidSeventeenBaseDialogFragmentPositiveClick(String dialogType);
    }

}
