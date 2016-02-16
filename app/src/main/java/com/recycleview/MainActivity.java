package com.recycleview;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
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

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class MainActivity extends AppCompatActivity {

    private GsonXml gsonXml;
    private LinearLayoutManager linearLayoutManager;
    private Typeface ayaSeparatorFont;
    private String ayaSeparator;
    private ArrayList<String> allAudioPaths;
    private MediaPlayer mediaPlayer;
    private int clickedCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);

        ayaSeparatorFont = Typeface.createFromAsset(getAssets(), "fonts/Scheherazade-Regular.ttf");
        mediaPlayer = new MediaPlayer();

        ayaSeparator = Character.toString((char) 1757);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        // Create adapter passing in the sample user data

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);


        ArrayList<AyaHolder> arrayList = new ArrayList<>();

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

        gsonXml = new GsonXmlBuilder()
                .setXmlParserCreator(parserCreator).setSameNameLists(true)
                .create();

        String arabicXML = new HelperFunctions().readXMLFromAssets(MainActivity.this, "Arabic.xml");

        Quran quranArabic = gsonXml.fromXml(arabicXML, Quran.class);

        Sura[] surahArabic = quranArabic.getSura();

        int sectionIndex = 0;

//        for (int i = 0; i < surahArabic.length; i++) {
//            Aya[] aya = surahArabic[i].getAya();
//
//            ArrayList<Integer> start = new ArrayList<>();
//            ArrayList<Integer> end = new ArrayList<>();
//
//            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(sectionIndex, surahArabic[i].getName()));
//            String temp = new String();
//
//            for (int j = 0; j < aya.length; j++) {
//                start.add(temp.length());
//                temp += aya[j].getText() + "\n";
//                end.add(temp.length());
//            }
//
//            SpannableString spannableString = new SpannableString(temp);
//
//            final ArrayList<MyClickableSpan> clickableSpanArrayList = new ArrayList<>();
//
//            for (int j = 0; j < aya.length; j++) {
//
//                clickableSpanArrayList.add(new MyClickableSpan(aya[j].getText(),
//                        Integer.parseInt(surahArabic[i].getIndex()),
//                        Integer.parseInt(aya[j].getIndex())) {
//                    @Override
//                    public void onClick(View widget) {
//
//                        TextPaint ds = new TextPaint();
//                        ds.setColor(Color.RED);
//                        updateDrawState(ds);
//
//                        if (!isHighlightWord()) {
//                            setHighlightWord(true);
//                        }else{
//                            setHighlightWord(false);
//                        }
//
//
//                        recyclerView.smoothScrollToPosition(getSurah());
//                        widget.invalidate();
//
//
//                        System.out.println("Aya Text: " + getAyaText());
//                        System.out.println("Surah Index: " + getSurah());
//                        System.out.println("Aya Index: " + getAya());
//                        System.out.println("---------------------------");
//
//                        System.out.println("Length: " + clickableSpanArrayList.size());
//
//                    }
//                });
//
//                spannableString.setSpan(clickableSpanArrayList.get(j), start.get(j), end.get(j), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//            }

        Aya[] firstAya = surahArabic[0].getAya();
        String bismillah = firstAya[0].getText();

        ArrayList<ArrayList<MyClickableSpan>> allSpans = new ArrayList<>();
        for (int i = 0; i < surahArabic.length; i++) {
            Aya[] aya = surahArabic[i].getAya();

            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(sectionIndex, surahArabic[i].getName()));

            SpannableStringBuilder sb = new SpannableStringBuilder();
            final ArrayList<MyClickableSpan> clickableSpanArrayList = new ArrayList<>();

            for (int j = 0; j < aya.length; j++) {

                clickableSpanArrayList.add(new MyClickableSpan(aya[j].getText(),
                        Integer.parseInt(surahArabic[i].getIndex()),
                        Integer.parseInt(aya[j].getIndex())) {
                    @Override
                    public void onClick(final View widget) {

                        for (int i = 0; i < clickableSpanArrayList.size(); i++) {
                            clickableSpanArrayList.get(i).setHighlightWord(false);
                        }

                        setHighlightWord(true);

                        widget.invalidate();

                        System.out.println("Aya Text: " + getAyaText());
                        System.out.println("Surah Index: " + getSurah());
                        System.out.println("Aya Index: " + getAya());
                        System.out.println("---------------------------");

                        System.out.println("Length: " + clickableSpanArrayList.size());

                        int surahNumber = getSurah();
                        int ayaNumber = getAya();
                        int totalAya = clickableSpanArrayList.size();

//                        if (surahNumber == 1 || surahNumber == 9) {
//                            sourceURLS = new String[totalAya - ayaNumber + 1];
//                            destinationURLS = new String[totalAya - ayaNumber + 1];
//                        } else {
//                            sourceURLS = new String[totalAya - ayaNumber];
//                            destinationURLS = new String[totalAya - ayaNumber];
//                        }

                        ArrayList<String> sourceURLS = new ArrayList<String>();
                        ArrayList<String> destinationURLS = new ArrayList<String>();


                        String stringSurahNumber;
                        String stringAyaNumber;

                        if (surahNumber < 10) {
                            stringSurahNumber = "00" + surahNumber;
                        } else if (surahNumber >= 10 && surahNumber < 100) {
                            stringSurahNumber = "0" + surahNumber;
                        } else {
                            stringSurahNumber = String.valueOf(surahNumber);
                        }

                        File baseDir = Environment.getExternalStorageDirectory();
                        String audioFolderPath = baseDir.getAbsolutePath() + "/BeABetterMuslim/Audio/" + surahNumber + "/";
                        File audioFolder = new File(audioFolderPath);

                        String reciter = "saood";

                        boolean isFolderCreated = false;
                        if (!audioFolder.exists()) {
                            isFolderCreated = audioFolder.mkdirs();
                        }

                        int tempCounter = 0;

                            for (int i = ayaNumber; i <= totalAya; i++) {

                                if (i < 10) {
                                    stringAyaNumber = "00" + i;
                                } else if (i >= 10 && i < 100) {
                                    stringAyaNumber = "0" + i;
                                } else {
                                    stringAyaNumber = String.valueOf(i);
                                }

                                String fileName = stringSurahNumber + stringAyaNumber + ".mp3";
                                sourceURLS.add("http://www.collagewebtech.com/quranData/quranAudio/" + reciter + "/" + surahNumber + "/" + fileName);
                                destinationURLS.add(audioFolderPath + stringSurahNumber + stringAyaNumber + ".mp3");
                            }
//                    else {
//                            for (int i = ayaNumber; i < totalAya; i++) {
//
//                                if (i < 10) {
//                                    stringAyaNumber = "00" + i;
//                                } else if (i >= 10 && i < 100) {
//                                    stringAyaNumber = "0" + i;
//                                } else {
//                                    stringAyaNumber = String.valueOf(i);
//                                }
//
//                                String fileName = stringSurahNumber + stringAyaNumber + ".mp3";
//                                sourceURLS[tempCounter] = "http://www.collagewebtech.com/quranData/quranAudio/" + reciter + "/" + surahNumber + "/" + fileName;
//                                destinationURLS[tempCounter++] = audioFolderPath + stringSurahNumber + stringAyaNumber + ".mp3";
//                            }
//                        }

                        for (int i = 0; i < sourceURLS.size(); i++) {
                            System.out.println(sourceURLS.get(i));
                            System.out.println(destinationURLS.get(i));
                            System.out.println("------------------------------");
                        }

                        File[] audioFiles = audioFolder.listFiles();

                        if (isFolderCreated){
                            System.out.println("Audio File Length: " + audioFiles.length + ", Total Aya: " + totalAya);
                        }

                        allAudioPaths = new ArrayList<String>();

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
                                Log.d("Completed", "Completed");

                                allAudioPaths.add(task.getPath());

                                if (allAudioPaths.size() == 1) {
                                    FileDescriptor fd = null;
                                    FileInputStream fis = null;


                                    try {
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

                        clickedCounter = 0;

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();

                                FileDescriptor fd = null;
                                FileInputStream fis = null;
                                clickableSpanArrayList.get(clickedCounter).setHighlightWord(false);
                                clickedCounter++;

                                    if (clickedCounter < allAudioPaths.size()) {
                                        try {
                                            fis = new FileInputStream(allAudioPaths.get(clickedCounter));

                                            fd = fis.getFD();

                                            if (fd != null) {
                                                mediaPlayer.setDataSource(fd);
                                                mediaPlayer.prepare();
                                                mediaPlayer.start();
                                                clickableSpanArrayList.get(clickedCounter).setHighlightWord(true);
                                                widget.invalidate();
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

                        for (int i = 0; i < sourceURLS.size(); i++) {
                            FileDownloader.getImpl().create(sourceURLS.get(i)).setPath(destinationURLS.get(i)).setAutoRetryTimes(5)
                                    .setCallbackProgressTimes(0) // why do this? in here i assume do not need callback each task's `FileDownloadListener#progress`, so in this way reduce ipc will be effective optimization
                                    .setListener(queueTarget)
                                    .ready();
                        }

                        if (HelperFunctions.isOnline(MainActivity.this)) {
                            FileDownloader.getImpl().start(queueTarget, true);
                        } else {
                            Toast.makeText(MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sb.append(aya[j].getText(), clickableSpanArrayList.get(j), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sb.append(" " + ayaSeparator + HelperFunctions.getCodeFromNumber(aya[j].getIndex()) + " ", new CustomTypefaceSpan("", ayaSeparatorFont), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if ((j + 1) % 10 == 0){
                    arrayList.add(new AyaHolder(new SpannableString(sb)));
                    sb = new SpannableStringBuilder();
                    sectionIndex++;
                }
            }

            arrayList.add(new AyaHolder(new SpannableString(sb)));
            sectionIndex++;

            allSpans.add(clickableSpanArrayList);
        }

        final CustomAdapter adapter = new CustomAdapter(arrayList);

        recyclerView.setLayoutManager(linearLayoutManager);

        //Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(this,R.layout.section,R.id.section_text, adapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        recyclerView.setAdapter(mSectionedAdapter);
    }


    public abstract class MyClickableSpan extends ClickableSpan{
        String ayaText;
        int surah;
        int aya;

        boolean highlightWord = false;

        public boolean isHighlightWord() {
            return highlightWord;
        }

        public void setHighlightWord(boolean highlightWord) {
            this.highlightWord = highlightWord;
        }

        public MyClickableSpan(String ayaText, int surah, int aya) {
            super();
            this.ayaText = ayaText;
            this.surah = surah;
            this.aya = aya;
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

    public class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private  void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }


}
