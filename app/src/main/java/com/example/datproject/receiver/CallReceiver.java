package com.example.datproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CallReceiver extends BroadcastReceiver {
    private static int previousState = TelephonyManager.CALL_STATE_IDLE;
    private static int currentState = TelephonyManager.CALL_STATE_IDLE;

    private static String telephoneNumber;

    private static boolean isIncomingCall = true;

    private MediaRecorder recorder;
    private TelephonyManager telManager;
    private boolean recordStarted = false;
    private Context context;
    private File audioFile = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String action = intent.getAction();
        Log.d("Get intent action", "action = " + action);

        try {
            telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String number) {
            try {
                currentState = state;

                if (previousState == currentState) {
                    return;
                }

                switch (currentState) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (previousState == TelephonyManager.CALL_STATE_RINGING) {
                            onMissedCall();
                        } else if (previousState == TelephonyManager.CALL_STATE_OFFHOOK) {
                            if (isIncomingCall) {
                                onIncomingCallEnded();
                            } else {
                                onOutgoingCallEnded();
                            }
                        }
                        isIncomingCall = true;
                        telephoneNumber = "none";
                        break;

                    case TelephonyManager.CALL_STATE_RINGING:
                        if (previousState == TelephonyManager.CALL_STATE_IDLE) {
                            isIncomingCall = true;
                            telephoneNumber = number;
                        }
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (previousState == TelephonyManager.CALL_STATE_RINGING) {
                            onIncomingCallStarted();
                        } else if (previousState == TelephonyManager.CALL_STATE_IDLE) {
                            isIncomingCall = false;
                            onOutgoingCallStarted();
                        }
                        break;
                }

                previousState = currentState;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void onIncomingCallStarted() {
            Toast.makeText(context, "Received incoming call from : " + telephoneNumber, Toast.LENGTH_SHORT).show();
            if (!recordStarted) {
                startRecord("incomingCall");
            }
        }

        private void onIncomingCallEnded() {
            Toast.makeText(context, "Incoming call ended : " + telephoneNumber, Toast.LENGTH_SHORT).show();
            if (recordStarted) {
                stopRecord();
            }
        }

        private void onOutgoingCallStarted() {
            Toast.makeText(context, "Received outgoing call from : " + telephoneNumber, Toast.LENGTH_SHORT).show();
            if (!recordStarted) {
                startRecord("outgoingCall");
            }
        }

        private void onOutgoingCallEnded() {
            Toast.makeText(context, "Outgoing call ended : " + telephoneNumber, Toast.LENGTH_SHORT).show();
            if (recordStarted) {
                stopRecord();
            }
        }

        private void onMissedCall() {
            Toast.makeText(context, "Missed incoming call from : " + telephoneNumber, Toast.LENGTH_SHORT).show();
            // do nothing
        }

        private void startRecord(String callType) {
            recorder = new MediaRecorder();
            Log.d("Init Media Recorder", "recorder = new MediaRecorder();");

            File sampleDir = Environment.getExternalStorageDirectory();
            String dir = sampleDir.getAbsolutePath();
            Log.d("External path", "sampleDir.getAbsolutePath() = " + dir);
            try {
                audioFile = File.createTempFile(callType + "-" + telephoneNumber, ".3gp", sampleDir);
                Log.d("Audio file", "audiofile created successfully");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(audioFile.getAbsolutePath());
                recorder.prepare();
                recorder.start();
                recordStarted = true;
                Log.d("Starting record", "record started = " + recordStarted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void stopRecord() {
            recorder.stop();
            recordStarted = false;
            Log.d("Stopping record", "status = " + recordStarted);
        }
    };
}
