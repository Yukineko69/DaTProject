package com.example.datproject.recordlist;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.datproject.R;
import com.example.datproject.databinding.DialogPlayRecordBinding;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Dialog hiển thị thông tin bản record
 * Có thể thực thiện play record
 * Chỉnh sửa tên record
 */
public class DialogPlayRecord extends DialogFragment implements View.OnClickListener {
    public static final String RECORD_NAME = "record_name";
    public static final String RECORD_TIME = "record_time";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String URL;
    private DialogPlayRecordBinding binding;
    private boolean prepareState = false;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;


    public static DialogPlayRecord newInstance(String name, String time) {
        DialogPlayRecord frag = new DialogPlayRecord();
        Bundle bundle = new Bundle();
        bundle.putString(RECORD_NAME, name);
        bundle.putString(RECORD_TIME, time);
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogPlayRecordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Dialog Play Record");
        binding.edtNameRecord.setText(getArguments().getString(RECORD_NAME));
        binding.lblFinalTime.setText(getArguments().getString(RECORD_TIME));
        URL = getActivity().getExternalFilesDir("file_audio").getAbsolutePath()
                + "/" + getArguments().getString(RECORD_NAME).replaceAll("\\s+", "") + ".3gp";
        binding.btnSave.setEnabled(false);
        onChangeName();
        binding.btnSave.setOnClickListener(this);
        binding.btnExit.setOnClickListener(this);
        binding.btnPlay.setOnClickListener(this);
        binding.btnForward.setOnClickListener(this);
        binding.btnBackward.setOnClickListener(this);
        setThumSeekbar();

    }

    private void setThumSeekbar() {
        binding.seekTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(progressChangedValue);
            }
        });
    }
    private void onChangeName() {
        binding.edtNameRecord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnSave.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        String data = binding.edtNameRecord.getText().toString().trim();
        Intent intent = new Intent();
        switch (id){
            case R.id.btn_save:
                if (!data.isEmpty()) {
                intent.putExtra("STRING_RESULT", data);
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);
                stopPlay();
                myHandler.removeCallbacks(UpdateSongTime);
                getDialog().dismiss();
                } else {
                binding.edtNameRecord.setHint("Không để trống tên...");
                binding.edtNameRecord.setHintTextColor(Color.RED);
                }
                break;
            case R.id.btn_exit:
                stopPlay();
                getDialog().dismiss();
                myHandler.removeCallbacks(UpdateSongTime);
                break;
            case R.id.btn_play:
                playRecord();
                break;

            case R.id.btn_forward:
                forwardAction();
                break;

            case R.id.btn_backward:
                backwardAction();
                break;

        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            binding.lblStartTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            binding.seekTime.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    public void playAction(){
        mediaPlayer.start();
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        binding.seekTime.setMax((int) finalTime);
        binding.lblFinalTime.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
        );
        binding.lblStartTime.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
        );
        binding.seekTime.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);
    }

    private void playRecord() {
        if (!mediaPlayer.isPlaying()) {
            binding.btnPlay.setImageResource(R.drawable.ic_pause_play);
            if (!prepareState) {
                try {
                    mediaPlayer.setDataSource(URL);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                prepareState = true;
            }
            playAction();
            CompletePlay();
        } else {
            binding.btnPlay.setImageResource(R.drawable.ic_play_arrow);
            mediaPlayer.pause();
        }
    }

    public void forwardAction() {
        int temp = (int)startTime;
        if((temp+forwardTime)<=finalTime){
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            mediaPlayer.seekTo((int) finalTime);
        }
    }

    private void stopPlay() {

        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void backwardAction() {
        int temp = (int)startTime;
        if((temp-backwardTime)>0){
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            mediaPlayer.seekTo( 0);
        }
    }
    private void CompletePlay() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.setLooping(false);
                binding.btnPlay.setImageResource(R.drawable.ic_play_arrow);
            }
        });
    }
}
