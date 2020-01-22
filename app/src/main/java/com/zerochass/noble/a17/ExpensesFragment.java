package com.zerochass.noble.a17;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpensesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpensesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Long selectedDate;
    private Calendar calendar;

    private ExpensesListSQLHelper expensesListSQLHelper;
    private SQLiteDatabase db;
    private ListView lvSummaryExpenses;

    private OnFragmentInteractionListener mListener;

    public ExpensesFragment() {
    }


    // TODO: Rename and change types and number of parameters
    public static ExpensesFragment newInstance() {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        expensesListSQLHelper = new ExpensesListSQLHelper(getActivity());
        db = expensesListSQLHelper.getReadableDatabase();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        Cursor cursor = db.rawQuery("SELECT " + AndroidSeventeenContract.Expenses.TABLE_NAME + ".*, "
                + AndroidSeventeenContract.FinanceSources.COL_BANK_NAME + ", " + AndroidSeventeenContract.FinanceSources.COL_CARD_NAME + ", "
                + AndroidSeventeenContract.FinanceSources.COL_TYPE
                + " FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME
                + " LEFT JOIN " + AndroidSeventeenContract.FinanceSources.TABLE_NAME + " ON "
                + AndroidSeventeenContract.Expenses.COL_FINANCE_SOURCE + " = " + AndroidSeventeenContract.FinanceSources.TABLE_NAME + "." + AndroidSeventeenContract.FinanceSources._ID
                + " WHERE  " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + " = " + selectedDate, null);

        lvSummaryExpenses = view.findViewById(R.id.lvSummaryExpenses);
        ExpensesCursorAdapter expensesCursorAdapter = new ExpensesCursorAdapter(getActivity(), cursor, 0);
        lvSummaryExpenses.setAdapter(expensesCursorAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            selectedDate = mListener.onFragmentInteraction();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        Long onFragmentInteraction();
    }

    public class ExpensesCursorAdapter extends CursorAdapter {

        public ExpensesCursorAdapter(Context context, Cursor cursor, int flag) {
            super(context, cursor, flag);
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
            String bankName = cursor.getString(cursor.getColumnIndex(AndroidSeventeenContract.FinanceSources.COL_BANK_NAME));
            String cardName = cursor.getString(cursor.getColumnIndex(AndroidSeventeenContract.FinanceSources.COL_CARD_NAME));
            String financeType = cursor.getString(cursor.getColumnIndex(AndroidSeventeenContract.FinanceSources.COL_TYPE));
            int id = cursor.getInt(cursor.getColumnIndex(AndroidSeventeenContract.Expenses._ID));

            String financeSourceName = bankName + " " + cardName + " " + financeType;

            tvExpenseCategory.setText(category);
            tvExpenseDescription.setText(description);
            tvExpenseAmount.setText("$" + amount);
            tvExpenseFinanceSource.setText(financeSourceName);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_summary_expenses, parent, false);
            return view;
        }

    }
}
