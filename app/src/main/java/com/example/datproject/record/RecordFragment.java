package com.example.datproject.record;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.example.datproject.R;
import com.example.datproject.databinding.FragmentRecordBinding;
import com.example.datproject.main.ExternalStorageUtils;
import com.example.datproject.main.MainActivity;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
/**
 * Fragment có chức năng thực hiện việc record audio
 */
public class RecordFragment extends Fragment implements View.OnClickListener {
    public static FragmentRecordBinding binding;
    private boolean statusRecord = false;
    private boolean statusPause = false;
    private String defaultName;

    public String AudioSavePathInDevice = null;
    public static MediaRecorder mediaRecorder;
    public static final int RequestPermissionCode = 1;
    private static final String LOG_TAG = "RecordFragment";
    private VisualizerRecord visualize;
    private RecordTime time;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        time = new RecordTime();
        visualize = new VisualizerRecord();
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnPause.setVisibility(View.INVISIBLE);
        binding.lblName.setVisibility(View.INVISIBLE);
        binding.lblTimer.setVisibility(View.INVISIBLE);
        binding.lblNow.setVisibility(View.INVISIBLE);
        binding.lblTimer1.setVisibility(View.INVISIBLE);
        binding.visualizerView.setVisibility(View.INVISIBLE);
        binding.btnRecord.setOnClickListener(this);
        binding.btnPause.setOnClickListener(this);
    }

    /*Sự kiện khi click vào các Button
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //Click button record
            case R.id.btn_record:
                if (!statusRecord)
                    onClickRecord();
                else
                    onClickStopRecord();
                break;

            //click button pause
            case R.id.btn_pause:
                if (!statusPause)
                    onClickPause();
                else
                    onClickResume();
                break;
        }

    }


    /*Set enable của tablayout
     */
    private void setEnableTabLayout(final boolean en) {
        List<View> touch = MainActivity.binding.tabLayout.getTouchables();
        for (View v : touch) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return en;
                }
            });
        }

    }
    /*Hiển thị các thay đổi hình ảnh khi click vào button Record
     */
    private void setViewRecord() {
        if (!statusRecord) {
            binding.btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.manual_record), null, null);
            binding.btnPause.setVisibility(View.INVISIBLE);
            binding.lblName.setVisibility(View.INVISIBLE);
            binding.lblTimer.setVisibility(View.INVISIBLE);
            binding.lblNow.setVisibility(View.INVISIBLE);
            binding.lblTimer1.setVisibility(View.INVISIBLE);
            binding.visualizerView.setVisibility(View.INVISIBLE);
            binding.lblSuggestion.setVisibility(View.VISIBLE);
            setEnableTabLayout(false);
        } else {
            binding.btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_stop_record), null, null);
            setViewPause();
            binding.lblName.setText(defaultName);
            setEnableTabLayout(true);
            binding.btnPause.setVisibility(View.VISIBLE);
            binding.lblName.setVisibility(View.VISIBLE);
            binding.lblTimer.setVisibility(View.VISIBLE);
            binding.lblNow.setVisibility(View.VISIBLE);
            binding.lblTimer1.setVisibility(View.VISIBLE);
            binding.lblSuggestion.setVisibility(View.INVISIBLE);
            binding.visualizerView.setVisibility(View.VISIBLE);
        }
    }

    /*Hiển thị các thay đổi hình ảnh khi click vào button Pause
     */
    private void setViewPause() {
        if (!statusPause)
            binding.btnPause.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_pause_record), null, null);
        else
            binding.btnPause.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_resume_record), null, null);
    }

    /*Khởi tạo để Record
     */
    public void MediaRecorderReady(){
        mediaRecorder = new MediaRecorder();
        //Set nguồn ghi âm là MIC
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //Set định dạng đầu ra 3gp
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //Set encoder của file đầu ra
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    /*check xem device có bộ nhớ ngoài và có để ghi không
     */
    private boolean ExternalStorageReady() {
        if (ExternalStorageUtils.isExternalStorageReadOnly() || !ExternalStorageUtils.isExternalStorageAvailable() )
            return false;
        return true;
    }


    /*Các hành động để start record
     */
    private void onClickRecord() {
        if(checkPermission()) {
            if (ExternalStorageReady()) {
                int numberRecord = MainActivity.database.recordAudioDao().getMaxId() + 1;
                defaultName = "Audio Recording " + numberRecord;
                //Đường dẫn file
                AudioSavePathInDevice = getActivity().getExternalFilesDir("file_audio").getAbsolutePath()
                        + "/" + defaultName.replaceAll("\\s+", "") + ".3gp";
                statusRecord = true;
                MediaRecorderReady();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                setViewRecord();
                visualize.startVisualize();
                time.startTime();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                String formattedDate = df.format(c.getTime());
                binding.lblNow.setText(formattedDate);
                startService();
                Log.e(LOG_TAG,"Recording started" );
            }
        } else {
            requestPermissions(new
                    String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
        }
    }

    /*Các hành động để dừng record và lưu lại
     */
    private void onClickStopRecord() {
        statusRecord = false;
        statusPause = false;
        visualize.stopVisualize();
        mediaRecorder.stop();
        mediaRecorder.release();
        stopService();
        time.stopTime();

        SaveRecordToDatabase save = new SaveRecordToDatabase(binding.lblName.getText().toString(),
                binding.lblTimer1.getText().toString());
        save.saveToDatabase();
        binding.lblTimer.setText("00:00:00");
        binding.lblTimer1.setText("00:00");
        binding.lblNow.setText("00:00");
        setViewRecord();
        Log.e(LOG_TAG,"Lưu bản ghi thành công");
    }

    /*Các hành động để tạm dừng record
     */
    private void onClickPause() {
        statusPause = true;
        visualize.pauseVisualize();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder.pause();
            stopService();
            time.pauseTime();
            setViewPause();
        } else {
            Log.e(LOG_TAG, "Feature is not supported in this SDK version");
        }

    }

    /*Các hành động để tiếp tục record
     */
    private void onClickResume() {
        statusPause = false;
        visualize.startVisualize();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder.resume();
            time.startTime();
            startService();
            setViewPause();
        } else {
            Log.e(LOG_TAG, "Feature is not supported in this SDK version");
        }
    }


    /*Nhận callback từ requestPermission()với tham 3 tham số
      requestCode: ta truyền vào requestPermissions ở trên
      permissions: là mảng các tên quyền: WRITE_EXTERNAL_STORAGE, RECORD_AUDIO
      grantResults: là mảng kết quả trả về sau khi request: được cấp quyền chưa
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestPermissionCode) {

            if (grantResults.length> 0) {
                boolean StoragePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED;

                if (StoragePermission && RecordPermission) {
                    Log.e(LOG_TAG, "Permission Granted");
                } else {
                    Log.e(LOG_TAG, "Permission Denied");
                }
            }
        }
    }

    /*Kiểm tra quyền đã được cấp chưa
     */
    public boolean checkPermission() {
        //Kiểm tra quyền ghi file
        int result = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        //Kiểm tra quyền ghi âm
        int result1 = ContextCompat.checkSelfPermission(getContext(),
                RECORD_AUDIO);

        //PERMISSION_GRANTED: Quyền đã được cấp
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    /*Chạy Service thông báo ghi âm đang được chạy
     */
    public void startService() {

                Intent serviceIntent = new Intent(getActivity(), RecordForegroundService.class);
                serviceIntent.putExtra("inputExtra", time.getPauseTime());
                ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }

    /*Dừng service
     */
    public void stopService() {
        Intent serviceIntent = new Intent(getActivity(), RecordForegroundService.class);
        getActivity().stopService(serviceIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        time.stopTime();
        stopService();
        mediaRecorder.stop();
        mediaRecorder.release();
        SaveRecordToDatabase save = new SaveRecordToDatabase(binding.lblName.getText().toString(),
                binding.lblTimer.getText().toString());
        save.saveToDatabase();
    }


}
