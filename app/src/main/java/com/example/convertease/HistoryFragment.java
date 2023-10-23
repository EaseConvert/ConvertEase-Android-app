package com.example.convertease;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;
import com.example.convertease.RecyclerHistoryAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);


        ImageButton settingButton = view.findViewById(R.id.settingBtn);

        settingButton.setOnClickListener(v -> openSettingPage());





        RecyclerHistoryAdapter recyclerHistoryAdapter;
        ArrayList<History> historyArrayList;


        ArrayAdapter<String> arrayAdapter;
        Context thiscontext;
        thiscontext = container.getContext();

        //Recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.r_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(thiscontext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);



        // Db handle
        myDBHandler db = new myDBHandler(thiscontext);
        List<History> allHistory = db.getHistory();

        //array list for history
        historyArrayList = new ArrayList<>();



        for(History history : allHistory){
            historyArrayList.add(history);
            Log.d("dbHistoryData","data = " + allHistory);
        }

        // recycler view adapter work
        recyclerHistoryAdapter = new RecyclerHistoryAdapter(thiscontext,historyArrayList);
        recyclerView.setAdapter(recyclerHistoryAdapter);
        return view;
    }
    private void openSettingPage() {
        SettingFragment secondFragment = new SettingFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, secondFragment);
        transaction.addToBackStack(null); // Optional, for back navigation
        transaction.commit();
    }
}