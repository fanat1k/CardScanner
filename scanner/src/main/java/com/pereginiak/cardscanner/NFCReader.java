package com.pereginiak.cardscanner;

import android.content.Intent;

import java.io.IOException;

public interface NFCReader {
    String readNfcTag(Intent intent) throws IOException;
}
