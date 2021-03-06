package com.pereginiak.cardscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class CardScannerActivity extends Activity {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private NFCReader nfcReader;
    private static final String RESULT_NAME = "result";

    private void init() {
        nfcReader = new NFCReaderImpl();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_scanner);

        init();

        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC", Toast.LENGTH_LONG).show();
        } else {
            pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    0
            );

            if (!nfcAdapter.isEnabled()) {
                Log.w(Constants.LOG_TAG, "NFC is disabled on this device");
                Toast.makeText(this, "NFC is disabled on this device", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i(Constants.LOG_TAG, "onNewIntent=" + intent);
        handleIntent(intent);
        finish();
    }

    private void handleIntent(Intent intent) {
        try {
            String nfcTagInfo = nfcReader.readNfcTag(intent);
            //if (nfcTagInfo == null) {
            //    nfcTagInfo = "ERROR";
            //}

            Intent returnIntent = new Intent();
            returnIntent.putExtra(RESULT_NAME, nfcTagInfo);
            setResult(Activity.RESULT_OK, returnIntent);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Can not read tag, e=", e);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Tag Info");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
