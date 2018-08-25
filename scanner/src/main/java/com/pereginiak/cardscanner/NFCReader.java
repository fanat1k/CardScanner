package com.pereginiak.cardscanner;

import android.content.Intent;

public interface NFCReader {
    String readNfcTag(Intent intent);
}
