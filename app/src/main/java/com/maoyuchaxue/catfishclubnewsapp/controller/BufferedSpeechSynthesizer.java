package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by catfish on 17/9/12.
 */

public class BufferedSpeechSynthesizer
    implements SynthesizerListener {
    private SpeechSynthesizer synthesizer;
    private String[] speeches;
    private List<String> processedSpeeches;
    private int index = 0;
    private static final int MAX_CHARACTOR = 2000;

    public BufferedSpeechSynthesizer(SpeechSynthesizer synthesizer) {
        this.synthesizer = synthesizer;
        this.speeches = null;
    }

    public void startSpeaking(String[] content) {
        speeches = content;
        processedSpeeches = new ArrayList<>();

        String cur = "";
        for (String s : content) {
            if (cur.length() + s.length() > MAX_CHARACTOR) {
                if (!cur.isEmpty()) {
                    processedSpeeches.add(cur);
                    cur = "";
                }
            } else {
                cur += " " + s;
            }
        }
        processedSpeeches.add(cur);
        Log.i("speaker", "speak processed: " + processedSpeeches.size());
        for (int i = 0; i < processedSpeeches.size(); i++) {
            Log.i("speaker", "speak processed length : "+ i + " " + processedSpeeches.get(i));
        }
        index = -1;
        speakNextParagraph();
    }

    private void speakNextParagraph() {
        Log.i("speaker", "speak started: " + index);
        index++;
        synthesizer.stopSpeaking();
        synthesizer.startSpeaking(processedSpeeches.get(index), this);
    }

    public void stopSpeaking() {
        if (synthesizer.isSpeaking()) {
            synthesizer.stopSpeaking();
        }
    }

    @Override
    public void onSpeakBegin() {}

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {}

    @Override
    public void onSpeakPaused() {}

    @Override
    public void onSpeakResumed() {}

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {}

    @Override
    public void onCompleted(SpeechError speechError) {
        Log.i("speaker", "speak completed: " + index);
        if (index < processedSpeeches.size()) {
            speakNextParagraph();
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {}


}
