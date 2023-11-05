package com.example.convertease;

import static com.iceteck.silicompressorr.FileUtils.getDataColumn;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;

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
    String outputPdfPath;
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
                Log.d("inputfile",""+inputFilePath);
                setPasswordForPdf(inputFilePath,"Divesh");
            }
        });
        return view;
    }

    private void pickPdf() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,REQUEST_CODE_PICK_PDF);
    }

    private String getPathFromUri(Uri pdfUri) {
        String pdfPath = null;
        if (pdfUri != null) {
            if (DocumentsContract.isDocumentUri(getContext(), pdfUri)) {
                if ("com.android.externalstorage.documents".equals(pdfUri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(pdfUri);
                    String[] split = docId.split(":");
                    if ("primary".equalsIgnoreCase(split[0])) {
                        pdfPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if ("com.android.providers.downloads.documents".equals(pdfUri.getAuthority())) {
                    String id = DocumentsContract.getDocumentId(pdfUri);
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                    pdfPath = getDataColumn(getContext(), contentUri, null, null);
                }
            } else if ("content".equalsIgnoreCase(pdfUri.getScheme())) {
                pdfPath = getDataColumn(getContext(), pdfUri, null, null);
            } else if ("file".equalsIgnoreCase(pdfUri.getScheme())) {
                pdfPath = pdfUri.getPath();
            }
        }
        return pdfPath;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_PDF) {
            if (data != null) {
                Uri selectedPdfUri = data.getData();
                inputFilePath  = getPathFromUri(selectedPdfUri);
                Log.d("PDFFilePath", "Selected PDF File Path: " + inputFilePath);
                if(inputFilePath == null){
                    Toast.makeText(getContext(), "Failed to get PDF path", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please Select PDF File!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void setPasswordForPdf(String inputPdfPath, String ownerPassword) {
        try {
            File pdfFile = new File(inputPdfPath);

            if (pdfFile.exists()) {
                File dir = new File(Environment.getExternalStorageDirectory() + "/Download/ConvertEase/");

                if (!dir.exists()) {
                    dir.mkdirs(); // Create the directory if it doesn't exist
                }
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String formattedDate = sdf.format(new Date());
                String fileName = formattedDate + ".pdf";
                File securedPdf = new File(dir, fileName);
                outputPdfPath = securedPdf.getAbsolutePath();

                Log.d("Debug", "Before PdfReader initialization");

                PdfReader reader = new PdfReader(inputPdfPath);

                Log.d("Debug", "Before PdfWriter initialization");

                PdfWriter writer = new PdfWriter(outputPdfPath,
                        new WriterProperties().setStandardEncryption(
                                "".getBytes(), // Empty user password
                                ownerPassword.getBytes(),
                                EncryptionConstants.ALLOW_PRINTING,
                                EncryptionConstants.ENCRYPTION_AES_256
                        )
                );

                Log.d("Debug", "Before PdfDocument initialization");

                PdfDocument pdfDoc = new PdfDocument(reader, writer);

                Log.d("Debug", "Before pdfDoc.close()");

                pdfDoc.close();
                Toast.makeText(getContext(), "PDF Secured Successfully...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "PDF file not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "Error: " + e.getMessage());
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void updateHistory() {
        myDBHandler db = new myDBHandler (getContext());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("PDF Merged");
        history.setPath(outputPdfPath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }

}