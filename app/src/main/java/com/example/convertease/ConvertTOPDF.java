package com.example.convertease;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConvertTOPDF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConvertTOPDF extends Fragment {
    Context thiscontext;
    private static final int REQUEST_CODE_PICK_DOCX = 1;
    String filePath;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConvertTOPDF() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConvertTOPDF.
     */
    // TODO: Rename and change types and number of parameters
    public static ConvertTOPDF newInstance(String param1, String param2) {
        ConvertTOPDF fragment = new ConvertTOPDF();
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
        View   view =inflater.inflate(R.layout.fragment_convert_to_pdf, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        thiscontext = container.getContext();
        ImageButton selectFileBtn = view.findViewById(R.id.selectFileBtn);
        ImageButton convertToPDFBtn = view.findViewById(R.id.convertToPDFBtn);
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
                pickDocxFile();
            }
        });
        convertToPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertDocxToPdf(filePath);
            }
        });

        return view;
    }

    private void pickDocxFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"); // Specify DOCX MIME type
        startActivityForResult(intent, REQUEST_CODE_PICK_DOCX);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_DOCX && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedFileUri = data.getData();
                filePath = getFilePathFromUri(selectedFileUri);
                Log.d("docxis","path is "+filePath);
                System.out.println(filePath);
            }
            else {
                Log.d("docxis","path is null ");
            }
        }
    }
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        if (uri != null) {
            if (uri.getScheme() != null && uri.getScheme().equals("content")) {
                Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index != -1) {
                        filePath = cursor.getString(index);
                    }
                    cursor.close();
                }
            } else if (uri.getScheme() != null && uri.getScheme().equals("file")) {
                filePath = uri.getPath();
            }
        }
        return filePath;
    }

    public void convertDocxToPdf(String inputDocxPath) {
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String formattedDate = sdf.format(new Date());
            String fileName = formattedDate + ".pdf";
            File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");
            File pdfFile = new File(dir, fileName);
            String pdfFilePath = pdfFile.getAbsolutePath();
            XWPFDocument doc = new XWPFDocument(new FileInputStream(inputDocxPath));
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfFilePath));
            Document document = new Document(pdfDocument);
            PdfDocumentInfo pdfDocumentInfo = pdfDocument.getDocumentInfo();
            pdfDocumentInfo.setTitle("Converted PDF");
            for (XWPFPictureData picture : doc.getAllPackagePictures()) {
                byte[] bytes = picture.getData();
                Image img = new Image(ImageDataFactory.create(bytes));
                document.add(img);
            }
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String text = paragraph.getText();
                Paragraph para = new Paragraph(text);
                document.add(para);
            }
            document.close();
            pdfDocument.close();
            Toast.makeText(getContext(), "DOCX Converted Successfully...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Problem Occurred!!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}