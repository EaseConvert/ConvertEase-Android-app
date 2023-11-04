package com.example.convertease;

import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplitPDF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplitPDF extends Fragment {
    String inputFilePath;
    String filepath;
    private static final int REQUEST_CODE_PICK_PDF = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SplitPDF() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplitPDF.
     */
    // TODO: Rename and change types and number of parameters
    public static SplitPDF newInstance(String param1, String param2) {
        SplitPDF fragment = new SplitPDF();
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
        View   view =inflater.inflate(R.layout.fragment_split_pdf, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        ImageButton selectFileBtn = view.findViewById(R.id.selectFileBtn);
        ImageButton splitBtn = view.findViewById(R.id.splitPdfbtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPdf();
            }
        });
        splitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitPdf();
            }
        });
        return view;
    }

    private void splitPdf() {
        try{
            File sdcard = Environment.getExternalStorageDirectory();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String formattedDate = sdf.format(new Date());
            String fileName = formattedDate + ".pdf";
            File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");
            File pdfFile = new File(dir, fileName);
            filepath = pdfFile.getAbsolutePath();
            File selectedPdfFile = new File(inputFilePath);
            PDDocument document = Loader.loadPDF(selectedPdfFile);
            Splitter splitter = new Splitter();
            splitter.setStartPage(1);
            splitter.setEndPage(3);
            List <PDDocument> splitPages =  splitter.split(document);
            PDDocument newDoc = new PDDocument();
            for(PDDocument mydoc:splitPages)
            {
                newDoc.addPage(mydoc.getPage(0));
            }
            newDoc.save(filepath);
            newDoc.close();
            Toast.makeText(getContext(), "PDF Split Successfully", Toast.LENGTH_SHORT).show();
            updateHistory();
        }catch (Exception e){
            Log.d("pdfSplit",""+e);
            Log.d("pdfSplit",""+inputFilePath);
            Toast.makeText(getContext(), "Error With PDF Split", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void pickPdf() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,REQUEST_CODE_PICK_PDF);
    }
    private String getPathFromUri(Uri pdfUri) {
        String imagePath = null;
        if (pdfUri != null) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = requireActivity().getContentResolver().query(pdfUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                imagePath = cursor.getString(columnIndex);
                cursor.close();
            }
        }
        Log.d("pdfpath","path is "+imagePath);
        return imagePath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_PDF) {
            if (data != null) {
                Uri selectedPdfUri = data.getData();
                inputFilePath = getPathFromUri(selectedPdfUri);
                Log.d("PDFFilePath", "Selected PDF File Path: " + inputFilePath);
            } else {
                Toast.makeText(getContext(), "Please Select PDF File!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateHistory() {
        myDBHandler db = new myDBHandler (getContext());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("PDF Merged");
        history.setPath(filepath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }
}