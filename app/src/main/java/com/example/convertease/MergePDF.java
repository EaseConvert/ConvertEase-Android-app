package com.example.convertease;

import static android.app.Activity.RESULT_OK;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MergePDF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MergePDF extends Fragment {
    private static final int REQUEST_CODE_PICK_PDF = 1;
    private List<Uri> mSelectedPdf = new ArrayList<>();
    Context thiscontext;
    File mergedPdfFile;
    String  mergedPdfFilePath;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MergePDF() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MergePDF.
     */
    // TODO: Rename and change types and number of parameters
    public static MergePDF newInstance(String param1, String param2) {
        MergePDF fragment = new MergePDF();
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
        View   view =inflater.inflate(R.layout.fragment_merge_pdf, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        ImageButton selectFileBtn = view.findViewById(R.id.selectFileBtn);
        ImageButton mergePdfBtn = view.findViewById(R.id.mergePdfbtn);
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
        mergePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSelectedPdf.isEmpty()){
                    Toast.makeText(getContext(), "Please Select PDF File", Toast.LENGTH_SHORT).show();
                }
                else {
                    try{
                        File sdcard = Environment.getExternalStorageDirectory();
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                        String formattedDate = sdf.format(new Date());
                        String fileName = formattedDate + ".pdf";
                        File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");

                        // Check if the ConvertEase folder exists, if not, create it
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }                        File mergedPdfFile = new File(dir, fileName);
                        mergedPdfFilePath = mergedPdfFile.getAbsolutePath();
                        FileOutputStream outputStream = new FileOutputStream(mergedPdfFile);
                        Document document = new Document();
                        PdfCopy copy = new PdfCopy(document , outputStream);
                        document.open();
                        for(Uri pdfUri:mSelectedPdf){
                            PdfReader reader = new PdfReader(getContext().getContentResolver().openInputStream(pdfUri));
                            copy.addDocument(reader);
                            reader.close();
                        }
                        document.close();
                        outputStream.close();
                        Toast.makeText(getContext(), "PDFs Merged Successfully...", Toast.LENGTH_SHORT).show();

                        updateHistory();
                    }catch (Exception e){
                        Toast.makeText(getContext(), "PDFs Merged Failed!!!", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    private void pickPdf() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,REQUEST_CODE_PICK_PDF);
    }

    @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == REQUEST_CODE_PICK_PDF && resultCode == RESULT_OK){
                mSelectedPdf.clear();
                if(data != null) {
                    if(data.getData() != null){
                        mSelectedPdf.add(data.getData());
                    }
                    else if(data.getClipData() != null){
                        for (int i = 0;i<data.getClipData().getItemCount();i++){
                            mSelectedPdf.add(data.getClipData().getItemAt(i).getUri());
                        }
                    }
//                    Toast.makeText(getContext(), "Selected " + mSelectedPdf.size() + " PDF Files", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Error With PDF", Toast.LENGTH_SHORT).show();
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
        history.setPath(mergedPdfFilePath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }

}