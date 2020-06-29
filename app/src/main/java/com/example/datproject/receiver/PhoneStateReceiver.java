package com.example.datproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.example.datproject.record.RecordFragment;

import com.example.datproject.R;

public class PhoneStateReceiver extends BroadcastReceiver {
    private static int previousState = TelephonyManager.CALL_STATE_IDLE;
    private static int currentState = TelephonyManager.CALL_STATE_IDLE;

    private static boolean isIncomingCall = true;

    private static String telephoneNumber;

    private RecordFragment rec = new RecordFragment();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                Log.d("TelephoneCallReceiver", "onReceive : " + "NEW_OUTGOING_CALL");
                telephoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            } else {
                String callState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;

                if (callState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Log.d("TelephoneCallReceiver", "onReceive : " + "CALL_STATE_IDLE");
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (callState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    Log.d("TelephoneCallReceiver", "onReceive : " + "CALL_STATE_OFFHOOK");
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Log.d("TelephoneCallReceiver", "onReceive : " + "CALL_STATE_RINGING");
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                onCallStateChanged(context, state, incomingNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCallStateChanged(Context context, int state, String number) {
        try {
            currentState = state;

            if (previousState == currentState) {
                return;
            }


            switch (currentState) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (previousState == TelephonyManager.CALL_STATE_RINGING) {
                        onMissedCall(context, telephoneNumber);
                    } else if (previousState == TelephonyManager.CALL_STATE_OFFHOOK) {
                        if (isIncomingCall) {
                            onIncomingCallEnded(context, telephoneNumber);
                        } else {
                            onOutgoingCallEnded(context, telephoneNumber);
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
                        onIncomingCallStarted(context, telephoneNumber);
                    } else if (previousState == TelephonyManager.CALL_STATE_IDLE) {
                        isIncomingCall = false;
                        onOutgoingCallStarted(context, telephoneNumber);
                    }
                    break;
            }

            previousState = currentState;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onOutgoingCallStarted(Context context, String telephoneNumber) {
        Toast.makeText(context, "onOutgoingCallStarted from " + telephoneNumber, Toast.LENGTH_SHORT).show();
    }

    private void onIncomingCallStarted(Context context, String telephoneNumber) {
        Toast.makeText(context, "onIncomingCallStarted from " + telephoneNumber, Toast.LENGTH_SHORT).show();
        rec.onClickRecord();
    }

    private void onOutgoingCallEnded(Context context, String telephoneNumber) {
        Toast.makeText(context, "onOutgoingCallEnded from " + telephoneNumber, Toast.LENGTH_SHORT).show();
    }

    private void onIncomingCallEnded(Context context, String telephoneNumber) {
        Toast.makeText(context, "onIncomingCallEnded from " + telephoneNumber, Toast.LENGTH_SHORT).show();
        rec.onClickRecord();
    }

    private void onMissedCall(Context context, String telephoneNumber) {
        Toast.makeText(context, "onMissedCall from " + telephoneNumber, Toast.LENGTH_SHORT).show();
    }


//    @Override
//    public void onReceive(Context context, Intent intent) {
//        try {
//            System.out.println("Receiver start");
//            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//
//            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
//                Toast.makeText(context,"Ringing state",Toast.LENGTH_SHORT).show();
//            }
//            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
//                Toast.makeText(context,"Received State",Toast.LENGTH_SHORT).show();
//            }
//            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                Toast.makeText(context,"Idle State",Toast.LENGTH_SHORT).show();
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
