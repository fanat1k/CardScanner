package com.pereginiak.cardscanner;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;

class NFCReaderImpl implements NFCReader {
    @Override
    public String readNfcTag(Intent intent) throws IOException {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            return readDataFromTag(intent);
        } else {
            Log.e(Constants.LOG_TAG, "NFC tag was not discovered, unknown action=" + action);
            return null;
        }
    }

    private String readDataFromTag(Intent intent) throws IOException {
        Tag nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.i(Constants.LOG_TAG, "discovered NFC tag=" + nfcTag);

        Ndef ndef = Ndef.get(nfcTag);
        StringBuilder dataBuilder = new StringBuilder();

        ndef.connect();
        Log.d(Constants.LOG_TAG, "Connected to tag");

        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        Log.d(Constants.LOG_TAG, "messages size=" + messages.length);

        for (int messageIndex = 0; messageIndex < messages.length; messageIndex++) {
            NdefMessage ndefMessage = (NdefMessage) messages[messageIndex];
            NdefRecord[] records = ndefMessage.getRecords();
            Log.d(Constants.LOG_TAG, "Message " + messageIndex + "; records size=" + records.length);

            for (int recordIndex = 0; recordIndex < messages.length; recordIndex++) {
                NdefRecord record = records[recordIndex];
                byte[] payload = record.getPayload();
                String data = new String(payload);
                Log.d(Constants.LOG_TAG, "Record " + recordIndex + "; data=" + data);

                dataBuilder.append(data);
            }
        }
        ndef.close();

        //NdefRecord record = ndefMessages[0].getRecords()[0];
        //NdefRecord[] records = ndefMessages[0].getRecords();
        //byte[] payload = record.getPayload();

        String tagData = dataBuilder.toString();
        Log.d(Constants.LOG_TAG, "tagData=" + tagData);

        return tagData;
    }


/*
    private void readDataFromTag2(Tag tagFromIntent) throws IOException {
        MifareClassic tag = MifareClassic.get(tagFromIntent);

        tag.connect();
        Log.i(Constants.LOG_TAG, "connected to tag");

        int sectorCount = tag.getSectorCount();
        int tagSize = tag.getSize();
        Log.i(Constants.LOG_TAG, "sectorCount=" + sectorCount + "tagSize=" + tagSize);

        if (tag.isConnected()){
            for (int sector = 0; sector < sectorCount; sector++) {
                boolean isAuthenticated = false;

                if (tag.authenticateSectorWithKeyA(sector, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY)) {
                    isAuthenticated = true;
                    Log.d(Constants.LOG_TAG, "Authorization=KEY_MIFARE_APPLICATION_DIRECTORY, sector" + sector);
                } else if (tag.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT)) {
                    isAuthenticated = true;
                    Log.d(Constants.LOG_TAG, "Authorization=KEY_DEFAULT, sector" + sector);
                } else if (tag.authenticateSectorWithKeyA(sector,MifareClassic.KEY_NFC_FORUM)) {
                    isAuthenticated = true;
                    Log.d(Constants.LOG_TAG, "Authorization=KEY_NFC_FORUM, sector" + sector);
                } else {
                    Log.d(Constants.LOG_TAG, "Authorization denied, sector:" + sector);
                }

                if(isAuthenticated) {
                    int blockIndex = tag.sectorToBlock(sector);

                    byte[] block = tag.readBlock(blockIndex);
                    //String data = NfcUtils.ByteArrayToHexString(block);
                    String data = new String(block);
                    Log.i(Constants.LOG_TAG, "tag_data:" + data);
                }
            }
        }
        tag.close();
    }

    // from https://android.jlelse.eu/create-a-nfc-reader-application-for-android-74cf24f38a6f
    private String dumpTagData(Tag tag, String action) {
        StringBuilder sb = new StringBuilder();
        sb.append(action).append('\n');
        byte[] id = tag.getId();
        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize() + " bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }
*/
}
