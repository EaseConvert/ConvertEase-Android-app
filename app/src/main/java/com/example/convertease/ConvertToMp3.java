package com.example.convertease;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_PICK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;

import java.io.FileOutputStream;
import java.io.IOException;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.convertease.ConvertorFiles.AudioExtractor;
import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConvertToMp3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConvertToMp3 extends Fragment {
    Context thiscontext;
    String newVideoPath;
    File dir;
    ImageButton pickVideoBtn;
    String selectedVideoPath;
    String destinationAudioPath;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConvertToMp3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConvertToMp3.
     */
    // TODO: Rename and change types and number of parameters
    public static ConvertToMp3 newInstance(String param1, String param2) {
        ConvertToMp3 fragment = new ConvertToMp3();
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
        View view = inflater.inflate(R.layout.fragment_convert_to_mp3, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        ImageButton convertToMp3Btn = view.findViewById(R.id.convertToMp3Btn);
        pickVideoBtn = view.findViewById(R.id.selectVideoBtn);
        thiscontext = container.getContext();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
        convertToMp3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                extractAudioFromVideo();
                Toast.makeText(thiscontext,"Conversion Successfull...", Toast.LENGTH_SHORT).show();
                updateHistory();


            }
        });
        pickVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickVideo();
            }
        });
        return view;
    }


    private void updateHistory() {
        myDBHandler db = new myDBHandler(thiscontext);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history = new History();
        history.setName("MP4 To MP3");
        history.setPath(destinationAudioPath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory", "Name " + history.getName());
    }

    @SuppressLint("IntentReset")
    private void pickVideo() {
        Intent iGallery = new Intent(ACTION_PICK);
        iGallery.setType("video/*");
        iGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(iGallery, "Select Video"), 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri videoUri = data.getData();
            selectedVideoPath = videoUri != null ? videoUri.getPath() : null;
            //            sourceVideoPath = getRealPathFromUri(videoUri);
            Log.d("video compress"," ddsd"+selectedVideoPath);
        }
        else{
            Toast.makeText(thiscontext,"Permission Denied !", Toast.LENGTH_SHORT).show();
        }
    }
    private void extractAudioFromVideo() {
        MediaExtractor extractor = new MediaExtractor();
        MediaCodec codec = null;
        FileOutputStream outputStream = null;

        try {
            // Path to the input video file
            String videoPath = selectedVideoPath;

            // Set the data source to the video file
            extractor.setDataSource(videoPath);

            int audioTrackIndex = -1;

            // Find the audio track in the video
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    audioTrackIndex = i;
                    break;
                }
            }

            if (audioTrackIndex >= 0) {
                extractor.selectTrack(audioTrackIndex);

                // Output directory for the extracted audio
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ConvertEase");
                if (!dir.exists()) {
                    dir.mkdirs(); // Create the directory if it doesn't exist
                }

                // Output file path for the extracted audio
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String formattedDate = sdf.format(new Date());
                String fileName = formattedDate + ".mp3";
                String outputPath = new File(dir, fileName).getAbsolutePath();

                File outputAudioFile = new File(outputPath);
                outputStream = new FileOutputStream(outputAudioFile);

                MediaFormat audioFormat = extractor.getTrackFormat(audioTrackIndex);

                codec = MediaCodec.createDecoderByType(audioFormat.getString(MediaFormat.KEY_MIME));
                codec.configure(audioFormat, null, null, 0);
                codec.start();

                ByteBuffer[] codecInputBuffers = codec.getInputBuffers();
                ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

                boolean sawInputEOS = false;
                boolean sawOutputEOS = false;

                while (!sawOutputEOS) {
                    if (!sawInputEOS) {
                        int inputBufferIndex = codec.dequeueInputBuffer(-1);
                        if (inputBufferIndex >= 0) {
                            ByteBuffer inputBuffer = codecInputBuffers[inputBufferIndex];
                            int sampleSize = extractor.readSampleData(inputBuffer, 0);
                            if (sampleSize < 0) {
                                codec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                sawInputEOS = true;
                            } else {
                                codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                                extractor.advance();
                            }
                        }
                    }

                    int outputBufferIndex = codec.dequeueOutputBuffer(info, 0);
                    if (outputBufferIndex >= 0) {
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            sawOutputEOS = true;
                        }

                        ByteBuffer outputBuffer = codecOutputBuffers[outputBufferIndex];
                        byte[] data = new byte[info.size];
                        outputBuffer.get(data);
                        outputStream.write(data);

                        codec.releaseOutputBuffer(outputBufferIndex, false);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (codec != null) {
                codec.stop();
                codec.release();
            }
            extractor.release();
        }

        Toast.makeText(thiscontext, "Audio extracted successfully", Toast.LENGTH_SHORT).show();
    }
    }
