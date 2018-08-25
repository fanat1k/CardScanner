package com.pereginiak.cardscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CardScannerMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_scanner_main);
    }

    public void startIdScanner(View view) {
        Intent intent = new Intent(getBaseContext(), CardScannerActivity.class);
        startActivity(intent);
    }
}
