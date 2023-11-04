package com.example.convertease;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFParagraph;
//import org.apache.poi.xwpf.usermodel.XWPFRun;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConvertToDOCX#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConvertToDOCX extends Fragment {
    private static final int REQUEST_CODE_PICK_PDF = 1;
    Context thiscontext;
    String pdfPath;
    String outputFilepath;
    String  inputFilePath;
    private ArrayList<String> selectedPdfPath = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConvertToDOCX() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConvertToDOCX.
     */
    // TODO: Rename and change types and number of parameters
    public static ConvertToDOCX newInstance(String param1, String param2) {
        ConvertToDOCX fragment = new ConvertToDOCX();
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
        View   view =inflater.inflate(R.layout.fragment_convert_to_docx, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        thiscontext = container.getContext();
        ImageButton selectFileBtn = view.findViewById(R.id.selectFileBtn);
        ImageButton convertToDocxBtn = view.findViewById(R.id.convertToDocxBtn);
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
                // Navigate back to the previous fragment
                pickPdf();
            }
        });
        convertToDocxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertPDFtoDOC(inputFilePath);
            }
        });
        return view;
    }
    private void pickPdf() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,REQUEST_CODE_PICK_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_PDF) {
            if (data != null) {
                Uri selectedPdfUri = data.getData();
                inputFilePath = getPathFromUri(selectedPdfUri);
                Log.d("path",""+inputFilePath);
            } else {
                Toast.makeText(thiscontext, "Problem With Selected File", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Downloads.DATA}; // Use Downloads.DATA for PDFs
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATA); // Use Downloads.DATA for PDFs
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }
    private void convertPDFtoDOC(String inputpath) {
        try {
            // Load the PDF file
            File pdfFile = new File(Environment.getExternalStorageDirectory(), inputpath);
            PDDocument document = Loader.loadPDF(pdfFile);
            // Extract text from the PDF
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            // Create a DOCX file and write the extracted text
            XWPFDocument docxDocument = new XWPFDocument();
            XWPFParagraph paragraph = docxDocument.createParagraph();
            paragraph.createRun().setText(text);
            // Save the DOCX file in the specified directory
            File sdcard = Environment.getExternalStorageDirectory();
            File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String formattedDate = sdf.format(new Date());
            String fileName = formattedDate + ".docx";
            File docxFile = new File(dir, fileName);
            outputFilepath = docxFile.getAbsolutePath();
            FileOutputStream docxOutputStream = new FileOutputStream(docxFile);
            docxDocument.write(docxOutputStream);
            docxOutputStream.close();
            // Clean up
            document.close();
            Toast.makeText(thiscontext, "Docx Generated Successfully...", Toast.LENGTH_SHORT).show();
            updateHistory();
        } catch (Exception e) {
            Toast.makeText(thiscontext, "Problem With Conversion!!!", Toast.LENGTH_SHORT).show();
            Log.d("conversion",""+inputFilePath);
            e.printStackTrace();
        }
    }


    private void updateHistory() {
        myDBHandler db = new myDBHandler (getContext());
        Calendar calendar = Calendar.getInstance();
        android.icu.text.SimpleDateFormat dateFormat = new android.icu.text.SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("PDF to DOCX");
        history.setPath(outputFilepath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }

}