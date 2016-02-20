package com.recycleview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.recycleview.pojo.Aya;
import com.recycleview.pojo.Quran;
import com.recycleview.pojo.Sura;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class MainActivity extends AppCompatActivity {

    private LinearLayoutManager linearLayoutManager;
    private ArrayList<String> allAudioPaths;
    private MediaPlayer mediaPlayer;
    private int clickedCounter;
    private int ayaNumber;
    File baseDir;
    FileDescriptor fd;
    FileInputStream fis;

    @Bind(R.id.next_button)
    ImageButton nextButton;

    @Bind(R.id.play_button)
    ImageButton playButton;

    @Bind(R.id.previous_button)
    ImageButton previousButton;

    @Bind(R.id.volumeSeekBar)
    SeekBar volumeSeekBar;

    @Bind(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;


    private int surahNumber;
    ArrayList<ArrayList<MyClickableSpan>> allSpans;
    private AudioManager audioManager;
    private int scrollPosition;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        baseDir = Environment.getExternalStorageDirectory();

        toolbar.setTitle("Quran");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);

        Typeface ayaSeparatorFont = Typeface.createFromAsset(getAssets(), "fonts/Scheherazade-Regular.ttf");
        String ayaSeparator = Character.toString((char) 1757);
        mediaPlayer = new MediaPlayer();


        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        // Create adapter passing in the sample user data

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playButton.setImageResource(R.drawable.play);
                    } else {
                        mediaPlayer.start();
                        playButton.setImageResource(R.drawable.pause);
                    }
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {

                    ArrayList<MyClickableSpan> clickableSpans = allSpans.get(surahNumber - 1);

                    if (ayaNumber > 1){

                        clickableSpans.get(ayaNumber - 1).setHighlightWord(false);
                        System.out.println("Before: " + clickedCounter);
                        ayaNumber--;
                        clickedCounter = 0;

                        MyClickableSpan clickableSpan = clickableSpans.get(ayaNumber - 1);
                        clickableSpan.setHighlightWord(true);

                        System.out.println(clickableSpan.getAyaText());
                        System.out.println("After: " + clickedCounter);

                        String audioFolderPath = baseDir.getAbsolutePath() + "/BeABetterMuslim/Audio/" + surahNumber + "/";

                        try {

                            allAudioPaths.clear();

                            for (int i = ayaNumber - 1; i < clickableSpans.size(); i++) {
                                String path = audioFolderPath + HelperFunctions.formatNumber(surahNumber) +
                                        HelperFunctions.formatNumber(i + 1) + ".mp3";
                                System.out.println("Aya: " + path);

                                allAudioPaths.add(path);
                            }

                            fis = new FileInputStream(allAudioPaths.get(0));
                            fd = fis.getFD();

                            if (fd != null && fd.valid()) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(fd);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        recyclerView.getAdapter().notifyDataSetChanged();
                    }

                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer != null) {

                    ArrayList<MyClickableSpan> clickableSpans = allSpans.get(surahNumber - 1);

                    if (ayaNumber < clickableSpans.size()) {

                        clickableSpans.get(ayaNumber - 1).setHighlightWord(false);
                        ayaNumber++;
                        clickedCounter++;

                        MyClickableSpan clickableSpan = clickableSpans.get(ayaNumber - 1);
                        clickableSpan.setHighlightWord(true);

                        System.out.println(clickableSpan.getAyaText());
                        System.out.println(clickableSpan.getAya());


                        try {

                            String audioFolderPath = baseDir.getAbsolutePath() + "/BeABetterMuslim/Audio/" + surahNumber + "/";
                            String path = audioFolderPath + HelperFunctions.formatNumber(surahNumber) +
                                    HelperFunctions.formatNumber(ayaNumber) + ".mp3";

                            fis = new FileInputStream(path);
                            fd = fis.getFD();

                            if (fd != null && fd.valid()) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(fd);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }

            }

        });

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        volumeSeekBar.setMax(1000);

        try
        {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekBar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));

            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<WithoutTranslationHolder> withoutTranslationHolders = new ArrayList<>();
        ArrayList<WithTranslationHolder> withTranslationHolders = new ArrayList<>();

        List<SimpleSectionedRecyclerViewAdapter.Section> sections = new ArrayList<>();

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(recyclerView);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerView.setOnScrollListener(fastScroller.getOnScrollListener());


        XmlParserCreator parserCreator = new XmlParserCreator() {
            @Override
            public XmlPullParser createParser() {
                try {
                    return XmlPullParserFactory.newInstance().newPullParser();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        GsonXml gsonXml = new GsonXmlBuilder()
                .setXmlParserCreator(parserCreator).setSameNameLists(true)
                .create();

        String arabicXML = new HelperFunctions().readXMLFromAssets(MainActivity.this, "Arabic.xml");
        String translationXML = new HelperFunctions().readXMLFromAssets(MainActivity.this, "Urdu.xml");

        Quran quranArabic = gsonXml.fromXml(arabicXML, Quran.class);
        Quran quranTranslation = gsonXml.fromXml(translationXML, Quran.class);

        Sura[] surahArabic = quranArabic.getSura();
        Sura[] surahTranslation = quranTranslation.getSura();

        int sectionIndex = 0;

//        Aya[] firstAya = surahArabic[0].getAya();
//        String bismillah = firstAya[0].getText();
//
//        allSpans = new ArrayList<>();


        for (int i = 0; i < surahArabic.length; i++) {

            Aya[] aya = surahArabic[i].getAya();
            Aya[] transAya = surahTranslation[i].getAya();

            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(sectionIndex, surahArabic[i].getName()));

            for (int j = 0; j < aya.length; j++) {
                withTranslationHolders.add(new WithTranslationHolder(
                        Integer.parseInt(surahArabic[i].getIndex()),
                        Integer.parseInt(aya[j].getIndex()),
                        aya[j].getText(),
                        transAya[j].getText(),
                        aya.length
                ));

                sectionIndex++;
            }
        }


//        int positionCounter = 0;
//
//        for (int i = 0; i < surahArabic.length; i++) {
//            Aya[] aya = surahArabic[i].getAya();
//
//            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(sectionIndex, surahArabic[i].getName()));
//
//            SpannableStringBuilder sb = new SpannableStringBuilder();
//            final ArrayList<MyClickableSpan> clickableSpanArrayList = new ArrayList<>();
//
//            for (int j = 0; j < aya.length; j++) {
//
//                clickableSpanArrayList.add(new MyClickableSpan(aya[j].getText(),
//                        Integer.parseInt(surahArabic[i].getIndex()),
//                        Integer.parseInt(aya[j].getIndex()),
//                        positionCounter) {
//                    @Override
//                    public void onClick(final View widget) {
//
//                        for (int i = 0; i < clickableSpanArrayList.size(); i++) {
//                            clickableSpanArrayList.get(i).setHighlightWord(false);
//                        }
//
//                        setHighlightWord(true);
//
//                        widget.invalidate();
//
//                        surahNumber = getSurah();
//                        ayaNumber = getAya();
//                        int totalAya = clickableSpanArrayList.size();
//
//                        ArrayList<String> sourceURLS = new ArrayList<>();
//                        ArrayList<String> destinationURLS = new ArrayList<>();
//
//                        String stringSurahNumber = HelperFunctions.formatNumber(surahNumber);
//                        String stringAyaNumber;
//
//                        String audioFolderPath = baseDir.getAbsolutePath() + "/BeABetterMuslim/Audio/" + surahNumber + "/";
//                        File audioFolder = new File(audioFolderPath);
//
//                        String reciter = "saood";
//
//                        boolean isFolderCreated = false;
//                        if (!audioFolder.exists()) {
//                            isFolderCreated = audioFolder.mkdirs();
//                        }
//
//                        for (int i = ayaNumber; i <= totalAya; i++) {
//
//                            stringAyaNumber = HelperFunctions.formatNumber(i);
//
//                            String fileName = stringSurahNumber + stringAyaNumber + ".mp3";
//                            sourceURLS.add("http://www.collagewebtech.com/quranData/quranAudio/" + reciter + "/" + surahNumber + "/" + fileName);
//                            destinationURLS.add(audioFolderPath + stringSurahNumber + stringAyaNumber + ".mp3");
//                        }
//
//                        allAudioPaths = new ArrayList<>();
//
//                        File[] audioFiles = audioFolder.listFiles();
//
//                        if (FileDownloader.getImpl().isServiceConnected()){
//                            FileDownloader.getImpl().pauseAll();
//                        }
//
//                        if (audioFiles.length == totalAya){
//
//                            for (int i = 0; i < destinationURLS.size(); i++) {
//                                allAudioPaths.add(destinationURLS.get(i));
//                            }
//
//                            try {
//                                fis = new FileInputStream(allAudioPaths.get(0));
//                                fd = fis.getFD();
//
//                                if (fd != null && fd.valid()) {
//
//                                    mediaPlayer.reset();
//                                    mediaPlayer.setDataSource(fd);
//                                    mediaPlayer.prepare();
//                                    mediaPlayer.start();
//                                    playButton.setImageResource(R.drawable.pause);
//                                    playButton.setEnabled(true);
//                                    nextButton.setEnabled(true);
//                                    previousButton.setEnabled(true);
//                                }
//
//                                System.out.println("Audio File Length: " + audioFiles.length + ", Total Aya: " + totalAya);
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        else{
//
//                            final FileDownloadListener queueTarget = new FileDownloadListener() {
//                                @Override
//                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                                }
//
//                                @Override
//                                protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
//                                    Log.d("Connected", "" + task.getDownloadId());
//                                }
//
//                                @Override
//                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                                }
//
//                                @Override
//                                protected void blockComplete(BaseDownloadTask task) {
//                                }
//
//                                @Override
//                                protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
//                                    task.start();
//                                }
//
//                                @Override
//                                protected void completed(BaseDownloadTask task) {
////                                Log.d("Completed", "Completed");
//
//                                    allAudioPaths.add(task.getPath());
//
//                                    if (allAudioPaths.size() == 1) {
//                                        FileDescriptor fd = null;
//                                        FileInputStream fis = null;
//
//                                        try {
//                                            playButton.setImageResource(R.drawable.pause);
//                                            playButton.setEnabled(true);
//                                            nextButton.setEnabled(true);
//                                            previousButton.setEnabled(true);
//                                            fis = new FileInputStream(allAudioPaths.get(0));
//                                            fd = fis.getFD();
//
//                                            if (fd != null && fd.valid()) {
//
//                                                mediaPlayer.reset();
//                                                mediaPlayer.setDataSource(fd);
//                                                mediaPlayer.prepare();
//                                                mediaPlayer.start();
//                                            }
//
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                                }
//
//                                @Override
//                                protected void error(BaseDownloadTask task, Throwable e) {
//                                    e.printStackTrace();
//                                }
//
//                                @Override
//                                protected void warn(BaseDownloadTask task) {
//                                }
//                            };
//
//                            for (int i = 0; i < sourceURLS.size(); i++) {
//                                FileDownloader.getImpl().create(sourceURLS.get(i)).setPath(destinationURLS.get(i)).setAutoRetryTimes(5)
//                                        .setCallbackProgressTimes(0) // why do this? in here i assume do not need callback each task's `FileDownloadListener#progress`, so in this way reduce ipc will be effective optimization
//                                        .setListener(queueTarget)
//                                        .ready();
//                            }
//
//                            if (HelperFunctions.isOnline(MainActivity.this)) {
//                                FileDownloader.getImpl().start(queueTarget, true);
//                            } else {
//                                Toast.makeText(MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        clickedCounter = 0;
//
//                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mp) {
//                                mediaPlayer.stop();
//                                mediaPlayer.reset();
//
//                                clickedCounter++;
//
//                                    if (clickedCounter < allAudioPaths.size()) {
//                                        try {
//                                            fis = new FileInputStream(allAudioPaths.get(clickedCounter));
//
//                                            fd = fis.getFD();
//
//                                            if (fd != null) {
//                                                mediaPlayer.setDataSource(fd);
//                                                mediaPlayer.prepare();
//                                                mediaPlayer.start();
//                                                clickableSpanArrayList.get(ayaNumber - 1).setHighlightWord(false);
//                                                MyClickableSpan currentSpan = clickableSpanArrayList.get(ayaNumber++);
//                                                currentSpan.setHighlightWord(true);
//
//                                                TextView textView = (TextView) widget;
//                                                SpannableString completeText = (SpannableString)(textView).getText();
//                                                Layout textViewLayout = textView.getLayout();
//
//                                                String tempString = currentSpan.getAyaText();
//
//                                                System.out.println("Scroll Positon: " + currentSpan.getPosition() + 1);
//
//                                                recyclerView.getAdapter().notifyDataSetChanged();
//                                                linearLayoutManager.scrollToPositionWithOffset(currentSpan.getPosition() + 1, 0);
//
//                                            }
//
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    } else {
//                                        mp.stop();
//                                        mp.reset();
//                                    }
//                            }
//                        });
//
//                    }
//                });
//
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    sb.append(aya[j].getText(), clickableSpanArrayList.get(j), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    sb.append(" " + ayaSeparator + HelperFunctions.getCodeFromNumber(aya[j].getIndex()) + " ", new CustomTypefaceSpan("", ayaSeparatorFont), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                }
//
//                if ((j + 1) % 10 == 0){
//                    withoutTranslationHolders.add(new WithoutTranslationHolder(new SpannableString(sb)));
//                    positionCounter++;
//                    sb = new SpannableStringBuilder();
//                    sectionIndex++;
//                }
//
//            }
//
//            withoutTranslationHolders.add(new WithoutTranslationHolder(new SpannableString(sb)));
//            positionCounter+=2;
//            sectionIndex++;
//
//            allSpans.add(clickableSpanArrayList);
//        }

//        final WithoutTranslationAdapter withoutTranslationAdapter = new WithoutTranslationAdapter(withoutTranslationHolders);


        final WithTranslationAdapter withTranslationAdapter = new WithTranslationAdapter(withTranslationHolders, new CustomOnClickListener() {
            @Override
            public void onItemClick(View v, int rowPosition, WithTranslationHolder holder) {
                Log.d("OnCLick", holder.getArabicAya());
                Log.d("Length", String.valueOf(holder.getTotalAya()));
                Log.d("Row Position", String.valueOf(rowPosition));
                Log.d("Aya", String.valueOf(holder.getAyaNumber()));

//                TextView arabicTextView = (TextView) v.findViewById(R.id.arabic_verse_textview);
//                TextView translationTextView = (TextView) v.findViewById(R.id.translation_verse_textview);


                v.setSelected(true);
                v.invalidate();

                ArrayList<String> sourceURLS = new ArrayList<>();
                ArrayList<String> destinationURLS = new ArrayList<>();

                scrollPosition = rowPosition + holder.getSurahNumber();

                String audioFolderPath = baseDir.getAbsolutePath() + "/BeABetterMuslim/Audio/" + holder.getSurahNumber() + "/";
                File audioFolder = new File(audioFolderPath);
                String reciter = "saood";

                String stringSurahNumber = HelperFunctions.formatNumber(holder.getSurahNumber());
                String stringAyaNumber;

                for (int i = holder.getAyaNumber(); i <= holder.getTotalAya(); i++) {
                    stringAyaNumber = HelperFunctions.formatNumber(i);
                    String fileName = stringSurahNumber + stringAyaNumber + ".mp3";
                    sourceURLS.add("http://www.collagewebtech.com/quranData/quranAudio/" + reciter + "/" + holder.getSurahNumber() + "/" + fileName);
                    destinationURLS.add(audioFolderPath + stringSurahNumber + stringAyaNumber + ".mp3");
                }

                if (!audioFolder.exists()){
                    audioFolder.mkdirs();
                }

                File[] audioFiles = audioFolder.listFiles();
                Log.d("Files Length", "" + audioFiles.length);

                if (audioFiles.length == holder.getTotalAya()) {

                    allAudioPaths = new ArrayList<>();
                    for (int i = holder.getAyaNumber() - 1; i < holder.getTotalAya(); i++) {
                        allAudioPaths.add(destinationURLS.get(i));
                        Log.d("Path", destinationURLS.get(i));
                    }

                    try {
                        fis = new FileInputStream(allAudioPaths.get(0));
                        fd = fis.getFD();

                        if (fd != null && fd.valid()) {

                            Log.d("Playing", "Here");

                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(fd);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            playButton.setImageResource(R.drawable.pause);
                            playButton.setEnabled(true);
                            nextButton.setEnabled(true);
                            previousButton.setEnabled(true);
                        }

                        System.out.println("Audio File Length: " + audioFiles.length + ", Total Aya: " + holder.getTotalAya());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else{
                    allAudioPaths = new ArrayList<>();

                    final FileDownloadListener queueTarget = new FileDownloadListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        }

                        @Override
                        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                            Log.d("Connected", "" + task.getDownloadId());
                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        }

                        @Override
                        protected void blockComplete(BaseDownloadTask task) {
                        }

                        @Override
                        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                            task.start();
                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
//                                Log.d("Completed", "Completed");

                            allAudioPaths.add(task.getPath());

                            if (allAudioPaths.size() == 1) {

                                try {
                                    playButton.setImageResource(R.drawable.pause);
                                    playButton.setEnabled(true);
                                    nextButton.setEnabled(true);
                                    previousButton.setEnabled(true);
                                    fis = new FileInputStream(allAudioPaths.get(0));
                                    fd = fis.getFD();

                                    if (fd != null && fd.valid()) {

                                        mediaPlayer.reset();
                                        mediaPlayer.setDataSource(fd);
                                        mediaPlayer.prepare();
                                        mediaPlayer.start();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {
                        }
                    };

                    for (int i = 0; i < sourceURLS.size(); i++) {
                        FileDownloader.getImpl().create(sourceURLS.get(i)).setPath(destinationURLS.get(i)).setAutoRetryTimes(5)
                                .setCallbackProgressTimes(0)
                                .setListener(queueTarget)
                                .ready();
                    }

                    if (HelperFunctions.isOnline(MainActivity.this)) {
                        FileDownloader.getImpl().start(queueTarget, true);
                    } else {
                        Toast.makeText(MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }


                clickedCounter = 0;

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();

                        clickedCounter++;
                        scrollPosition++;

                        if (clickedCounter < allAudioPaths.size()) {
                            try {
                                fis = new FileInputStream(allAudioPaths.get(clickedCounter));

                                fd = fis.getFD();

                                if (fd != null) {
                                    mediaPlayer.setDataSource(fd);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();

                                    System.out.println("Scroll Positon: " + scrollPosition);
                                    linearLayoutManager.scrollToPositionWithOffset(scrollPosition, 0);

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            mp.stop();
                            mp.reset();
                        }
                    }
                });
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);

        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(this,R.layout.section,R.id.section_text, withTranslationAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        recyclerView.setAdapter(mSectionedAdapter);
    }


    public abstract class MyClickableSpan extends ClickableSpan{
        String ayaText;
        int surah;
        int aya;
        int position;

        boolean highlightWord = false;

        public boolean isHighlightWord() {
            return highlightWord;
        }

        public void setHighlightWord(boolean highlightWord) {
            this.highlightWord = highlightWord;
        }

        public MyClickableSpan(String ayaText, int surah, int aya, int position) {
            super();
            this.ayaText = ayaText;
            this.surah = surah;
            this.aya = aya;
            this.position = position;
        }

        public String getAyaText() {
            return ayaText;
        }

        @Override
        public void updateDrawState(TextPaint ds) {

            if (highlightWord){
                ds.setColor(Color.RED);
            }else{
                ds.setUnderlineText(false);
                ds.setColor(Color.BLACK);
                ds.bgColor = Color.TRANSPARENT;
            }

        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setAyaText(String ayaText) {
            this.ayaText = ayaText;
        }

        public int getSurah() {
            return surah;
        }

        public void setSurah(int surah) {
            this.surah = surah;
        }

        public int getAya() {
            return aya;
        }

        public void setAya(int aya) {
            this.aya = aya;
        }
    }


}
