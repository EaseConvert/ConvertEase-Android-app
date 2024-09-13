package com.example.convertease;

import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextToPDF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextToPDF extends Fragment {
    ImageButton generatePdfBtn;
    EditText pdfText;
    String filepath;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TextToPDF() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TextToPDF.
     */
    // TODO: Rename and change types and number of parameters
    public static TextToPDF newInstance(String param1, String param2) {
        TextToPDF fragment = new TextToPDF();
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
        View view =  inflater.inflate(R.layout.fragment_text_to_pdf, container, false);

        ImageButton backButton = view.findViewById(R.id.backBtn);
        generatePdfBtn = view.findViewById(R.id.generatePDF);
        pdfText = view.findViewById(R.id.PdfText);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
        
        generatePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePDF();

            }
        });
        
        return view;
    }

    private void generatePDF() {
        String text = pdfText.getText().toString();
        if (!text.isEmpty()) {
            File sdcard = Environment.getExternalStorageDirectory();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String formattedDate = sdf.format(new Date());
            String fileName = formattedDate + ".pdf";
            File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");

            // Check if the ConvertEase folder exists, if not, create it
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File pdfFile = new File(dir, fileName);
            filepath = pdfFile.getAbsolutePath();

            // Create a PDF document
            PdfDocument pdfDoc;
            try {
                FileOutputStream fos = new FileOutputStream(pdfFile);
                pdfDoc = new PdfDocument(new PdfWriter(fos));
                Document doc = new Document(pdfDoc);

                // Add the user's text to the PDF
                doc.add(new Paragraph(text));

                // Close the PDF document
                doc.close();
                updateHistory();
                Toast.makeText(getContext(), "PDF Generated Successfully...", Toast.LENGTH_SHORT).show();

                // Provide feedback to the user, e.g., show a success message
                // You can also launch a PDF viewer to view the generated PDF
            } catch (IOException e) {
                Toast.makeText(getContext(), "Problem With PDF Generation!!!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Enter Text in Text Area", Toast.LENGTH_SHORT).show();
            // Handle the case when the input is empty
        }
    }

    private void updateHistory() {
        myDBHandler db = new myDBHandler (getContext());
        Calendar calendar = Calendar.getInstance();
        android.icu.text.SimpleDateFormat dateFormat = new android.icu.text.SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("Text To PDF");
        history.setPath(filepath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }
}