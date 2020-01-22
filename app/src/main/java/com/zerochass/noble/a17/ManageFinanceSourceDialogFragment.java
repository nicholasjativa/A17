package com.zerochass.noble.a17;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

/**
 * Created by noble on 4/14/18.
 */

public class ManageFinanceSourceDialogFragment extends DialogFragment {
    private AlertDialog dialog;
    private ExpensesListSQLHelper expensesListSQLHelper;
    private ManageFinanceSourceDialogFragmentListener mListener;
    private SQLiteDatabase db;
    private int mRowId;
    public static final String DIALOG_TYPE = "DIALOG_TYPE";
    public static final String EDIT_DIALOG = "EDIT";
    public static final String ADD_DIALOG = "ADD";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (ManageFinanceSourceDialogFragmentListener) context;
        } catch(ClassCastException e) { throw new ClassCastException(context.toString() + " must implement ManageFinanceSourceDialogFragmentListener"); }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        expensesListSQLHelper = new ExpensesListSQLHelper(getActivity());
        db = expensesListSQLHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();

        String title = "";
        String positiveButtonText = "";
        final String dialogType = bundle.getString(DIALOG_TYPE);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_base_finance_source_dialog, null);

        Spinner sBankName = view.findViewById(R.id.sBankName);
        Spinner sType = view.findViewById(R.id.sType);

        ArrayAdapter<CharSequence> bankNamesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bank_names, R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> financeTypesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.finance_types, R.layout.support_simple_spinner_dropdown_item);

        sBankName.setAdapter(bankNamesAdapter);
        sType.setAdapter(financeTypesAdapter);

        if (dialogType.equals(ADD_DIALOG)) {
            title = "Add New Finance Source";
            positiveButtonText = "Add";
        }
        else if (dialogType.equals(EDIT_DIALOG)) {
            title = "Edit Existing Finance Source";
            positiveButtonText = "Update";
            mRowId = bundle.getInt("id");
            loadData(mRowId, view);
        }

        builder.setView(view);
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

    public static ManageFinanceSourceDialogFragment newInstance(String dialogType, String id) {
        ManageFinanceSourceDialogFragment fragment = new ManageFinanceSourceDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TYPE, dialogType);

        if (id != null) {
            int parsedId = Integer.parseInt(id);
            args.putInt("id", parsedId);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public void loadData(int id, View view) {
        Cursor cursor = db.rawQuery("SELECT " + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", "
                                        + AndroidSeventeenContract.FinanceSources.COL_TYPE + ", "
                                        + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME
                                        + " FROM " + AndroidSeventeenContract.FinanceSources.TABLE_NAME
                                        + " WHERE " + AndroidSeventeenContract.FinanceSources._ID + " = " + id, null);
        cursor.moveToNext();

        Spinner sBankName = view.findViewById(R.id.sBankName);
        Spinner sType = view.findViewById(R.id.sType);
        EditText etCardName = view.findViewById(R.id.etCardName);

        String bankName = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_BANK_NAME));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_TYPE));
        String cardName = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.FinanceSources.COL_CARD_NAME));

        sBankName.setSelection(((ArrayAdapter) sBankName.getAdapter()).getPosition(bankName));
        sType.setSelection(((ArrayAdapter) sType.getAdapter()).getPosition(type));
        etCardName.setText(cardName);
    }

    public void saveData(View view, String dialogType) {
        Spinner sBankName = view.findViewById(R.id.sBankName);
        Spinner sType = view.findViewById(R.id.sType);
        EditText etCardName = view.findViewById(R.id.etCardName);

        String bankName = sBankName.getSelectedItem().toString();
        String type = sType.getSelectedItem().toString();
        String cardName = etCardName.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AndroidSeventeenContract.FinanceSources.COL_BANK_NAME, bankName);
        contentValues.put(AndroidSeventeenContract.FinanceSources.COL_TYPE, type);
        contentValues.put(AndroidSeventeenContract.FinanceSources.COL_CARD_NAME, cardName);

        if (dialogType.equals(ADD_DIALOG)) {
            db.insertOrThrow(AndroidSeventeenContract.FinanceSources.TABLE_NAME, null, contentValues);
        } else if (dialogType.equals(EDIT_DIALOG)) {
            db.update(AndroidSeventeenContract.FinanceSources.TABLE_NAME, contentValues, AndroidSeventeenContract.FinanceSources._ID + " = " + mRowId, null);
        }

        mListener.onManageFinanceSourceDialogFragmentPositiveClick(dialogType);
    }

    public interface ManageFinanceSourceDialogFragmentListener {
        void onManageFinanceSourceDialogFragmentPositiveClick(String dialogType);
    }
}
