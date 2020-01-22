package com.zerochass.noble.a17;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Long selectedDate;
    private Calendar calendar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExpensesListSQLHelper expensesListSQLHelper;
    private SQLiteDatabase db;
    private ListView lvSummaryCategories;
    private SummaryCursorAdapter summaryCursorAdapter;

    private OnFragmentInteractionListener mListener;

    public SummaryFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        try {
            String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            selectedDate = new SimpleDateFormat("MM/dd/yyyy").parse(today).getTime();
            mListener.onFragmentInteraction(selectedDate);
        } catch (Exception e) {}
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

        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        final EditText etSummaryDate = view.findViewById(R.id.summary_date);

        String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(selectedDate));
        etSummaryDate.setText(dateString);

        final DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                String dateString = new SimpleDateFormat("MM/dd/yyyy").format(calendar.getTime());
                try {
                    Long date = new SimpleDateFormat("MM/dd/yyyy").parse(dateString).getTime();
                    selectedDate = date;
                    mListener.onFragmentInteraction(selectedDate);
                    updateData();
                } catch(Exception e) {}

                etSummaryDate.setText(dateString);
            }
        };

        etSummaryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), dpd, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Cursor cursor = db.rawQuery("SELECT " + AndroidSeventeenContract.Expenses._ID + ", "
                + AndroidSeventeenContract.Expenses.COL_NAME_CATEGORY + ", "
                + "SUM(CAST(" + AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT + " AS REAL)) as amount"
                + " FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME
                + " WHERE " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + " = " + selectedDate
                + " GROUP BY " + AndroidSeventeenContract.Expenses.COL_NAME_CATEGORY, null);

        lvSummaryCategories = view.findViewById(R.id.lvSummaryCategories);
        summaryCursorAdapter = new SummaryCursorAdapter(getActivity(), cursor, 0);
        lvSummaryCategories.setAdapter(summaryCursorAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Long date);
    }

    public void updateData() {
        Cursor cursor = db.rawQuery("SELECT " + AndroidSeventeenContract.Expenses._ID + ", "
                + AndroidSeventeenContract.Expenses.COL_NAME_CATEGORY + ", "
                + "SUM(CAST(" + AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT + " AS REAL)) as amount"
                + " FROM " + AndroidSeventeenContract.Expenses.TABLE_NAME
                + " WHERE " + AndroidSeventeenContract.Expenses.COL_NAME_DATE + " = " + selectedDate
                + " GROUP BY " + AndroidSeventeenContract.Expenses.COL_NAME_CATEGORY, null);
        summaryCursorAdapter.swapCursor(cursor);

    }
    public class SummaryCursorAdapter extends CursorAdapter {

        public SummaryCursorAdapter(Context context, Cursor cursor, int flag) {
            super(context, cursor, flag);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvSummaryCategory = view.findViewById(R.id.tvSummaryCategory);
            TextView tvSummaryAmount = view.findViewById(R.id.tvSummaryAmount);

            String category = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_CATEGORY));
            String amount = cursor.getString(cursor.getColumnIndexOrThrow(AndroidSeventeenContract.Expenses.COL_NAME_AMOUNT));

            tvSummaryCategory.setText(category);
            tvSummaryAmount.setText("$" +  String.format("%.2f", Double.parseDouble(amount)));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_summary_category_amount, parent, false);
            return view;
        }
    }
}
