package com.arulvakku.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.arulvakku.ui.model.Bookmark;
import com.arulvakku.ui.model.Verses;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class UtilSingleton {

    private static UtilSingleton utilSingleton;
    private HashMap<String, String> mVerse;

    //private constructor.
    private UtilSingleton() {
    }

    public static UtilSingleton getInstance() {
        if (utilSingleton == null) {
            utilSingleton = new UtilSingleton();
        }
        return utilSingleton;
    }


    // to maintain the selected verse to highlight the lines
    private HashMap<String, Verses> selectedVerseList = new HashMap<>();

    private List<Verses> sortedVerseList = new ArrayList<>();

    private List<String> bookmarkedVerseList = new ArrayList<>();


    public void addSelectedVerse(String verseNo, Verses verse) {

        addBookmark(verseNo);

        if (selectedVerseList.containsKey(verseNo)) {
            selectedVerseList.remove(verseNo);
            return;
        }
        selectedVerseList.put(verseNo, verse);

    }

    public void addBookmark(String verseNo) {
        for (int index = 0; index < bookmarkedVerseList.size(); index++) {
            String bookmark = bookmarkedVerseList.get(index);
            if (bookmark.equals(verseNo)) {
                bookmarkedVerseList.remove(bookmark);
                return;
            }
        }
        bookmarkedVerseList.add(verseNo);
    }


    public boolean isBookMarked(List<Bookmark> bookmarks, String id) {
        for (int index = 0; index < bookmarks.size(); index++) {
            Bookmark bookmark = bookmarks.get(index);
            if (bookmark.getVerse().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getBookmarkedVerseList() {
        return bookmarkedVerseList;
    }

    public HashMap<String, Verses> getSelectedVerseList() {
        return selectedVerseList;
    }


    public boolean isSelected(String verseNo) {
        return selectedVerseList.containsKey(verseNo) ? true : false;
    }


    public void clearData() {
        if (selectedVerseList != null && selectedVerseList.size() > 0) {
            selectedVerseList.clear();
        }
    }


    // Function to sort map by Key
    public List<Verses> getSortedVerseList() {
        sortedVerseList = new ArrayList<>();
        SortedSet<String> keys = new TreeSet<>(selectedVerseList.keySet());
        for (String key : keys) {
            Verses value = selectedVerseList.get(key);
            sortedVerseList.add(value);
        }
        return sortedVerseList;
    }


    public String getBookmarkAndVerse(String verse_id) {
        String book_no = verse_id.substring(0, 2);
        String chapter_no = verse_id.substring(2, 5);
        String verse_no = verse_id.substring(5, 8);
        String detail = bookName[Integer.parseInt(book_no) - 1].trim() + " " + Integer.parseInt(chapter_no) + " : " + Integer.parseInt(verse_no);
        return detail;
    }


    public static final String[] bookName = new String[]{
            "தொடக்க நூல் ", "விடுதலைப் பயணம்", "லேவியர்  ", "எண்ணிக்கை   ", "இணைச் சட்டம்  ", "யோசுவா  ", "நீதித் தலைவர்கள்   ",
            "ரூத்து  ", "1 சாமுவேல்  ", "2 சாமுவேல் ", "1 அரசர்கள் ", "2 அரசர்கள்  ", "1 குறிப்பேடு  ", "2 குறிப்பேடு  ", "எஸ்ரா ",
            "நெகேமியா  ", "எஸ்தர்  ", "யோபு ", "திருப்பாடல்கள்  ", "நீதிமொழிகள் ", "சபை உரையாளர் ", "இனிமைமிகு பாடல்   ", "எசாயா   ", "எரேமியா  ",
            "புலம்பல் ", "எசேக்கியேல் ", "தானியேல்  ", "ஒசேயா ", "யோவேல் ", "ஆமோஸ் ", "ஒபதியா ", "யோனா  ", "மீக்கா ", "நாகூம்  ",
            "அபக்கூக்கு  ", "செப்பனியா ", "ஆகாய் ", "செக்கரியா  ", "மலாக்கி ", "தோபித்து ", "யூதித்து ", "எஸ்தர்  ", "சாலமோனின் ஞானம்  ",
            "சீராக்  ", "பாரூக்கு ", "தானியேல் (இ)", "1 மக்கபேயர்  ", "2 மக்கபேயர்  ",
            "மத்தேயு", "மாற்கு", "லூக்கா ", " யோவான்    ", " திருத்தூதர் பணிகள்", "உரோமையர்     ",
            " 1 கொரிந்தியர்", "2 கொரிந்தியர்", "கலாத்தியர்  ", " எபேசியர் ", "பிலிப்பியர்  ", "கொலோசையர் ",
            "1 தெசலோனிக்கர்   ", " 2 தெசலோனிக்கர்", "1 திமொத்தேயு", "2 திமொத்தேயு  ", " தீத்து ", "பிலமோன்  ",
            " எபிரேயர் ", "யாக்கோபு   ", " 1 பேதுரு    ", " 2 பேதுரு ", "1 யோவான்",
            "2 யோவான்", "3 யோவான்", "யூதா ", "திருவெளிப்பாடு"
    };

    public static String getTodayDate() {
        Date date = new Date();
        String stringDate = DateFormat.getDateInstance().format(date);
        return stringDate;
    }


    public static String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String date = sdf.format(cal.getTime());
        return date;
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    public String getVerse(String key) {
        mVerse = new HashMap<>();
        mVerse.put("01012020", "51002021");
        mVerse.put("02012020", "52001023");
        mVerse.put("03012020", "52001029");
        mVerse.put("04012020", "52001042");
        mVerse.put("05012020", "49002002");
        mVerse.put("06012020", "49004017");
        mVerse.put("07012020", "50006041");
        mVerse.put("08012020", "50006050");
        mVerse.put("09012020", "51004019");
        mVerse.put("10012020", "51005013");
        mVerse.put("11012020", "52003027");
        mVerse.put("12012020", "49003016");
        mVerse.put("13012020", "50001017");
        mVerse.put("14012020", "50001027");
        mVerse.put("15012020", "50001038");
        mVerse.put("16012020", "50001041");
        mVerse.put("17012020", "50002009");
        mVerse.put("18012020", "50002017");
        mVerse.put("19012020", "52001029");
        mVerse.put("20012020", "50002019");
        mVerse.put("21012020", "50002028");
        mVerse.put("22012020", "50003003");
        mVerse.put("23012020", "50003011");
        mVerse.put("24012020", "50003014");
        mVerse.put("25012020", "50016017");
        mVerse.put("26012020", "49004016");
        mVerse.put("27012020", "50003024");
        mVerse.put("28012020", "50003034");
        mVerse.put("29012020", "50004020");
        mVerse.put("30012020", "50004025");
        mVerse.put("31012020", "50004031");

        mVerse.put("01022020", "50004040");
        mVerse.put("02022020", "51002032");
        mVerse.put("03022020", "49005019");
        mVerse.put("04022020", "52012024");
        mVerse.put("05022020", "49006004");
        mVerse.put("06022020", "49028019");
        mVerse.put("07022020", "50006020");
        mVerse.put("08022020", "50006031");
        mVerse.put("09022020", "49005016");
        mVerse.put("10022020", "50006056");
        mVerse.put("11022020", "52002005");
        mVerse.put("12022020", "50007015");
        mVerse.put("13022020", "50007028");
        mVerse.put("14022020", "50007037");
        mVerse.put("15022020", "50008007");
        mVerse.put("16022020", "49005018");
        mVerse.put("17022020", "50008012");
        mVerse.put("18022020", "50008018");
        mVerse.put("19022020", "50008024");
        mVerse.put("20022020", "50008029");
        mVerse.put("21022020", "50008036");
        mVerse.put("22022020", "49016018");
        mVerse.put("23022020", "49005041");
        mVerse.put("24022020", "50009029");
        mVerse.put("25022020", "50009035");
        mVerse.put("26022020", "49006003");
        mVerse.put("27022020", "51009024");
        mVerse.put("28022020", "49009015");
        mVerse.put("29022020", "51005032");

        mVerse.put("01032020", "49004004");
        mVerse.put("02032020", "49025040");
        mVerse.put("03032020", "49006008");
        mVerse.put("04032020", "51011032");
        mVerse.put("05032020", "49007007");
        mVerse.put("06032020", "49005025");
        mVerse.put("07032020", "49005047");
        mVerse.put("08032020", "49017005");
        mVerse.put("09032020", "51006038");
        mVerse.put("10032020", "49023012");
        mVerse.put("11032020", "49020027");
        mVerse.put("12032020", "51016031");
        mVerse.put("13032020", "49021042");
        mVerse.put("14032020", "51015021");
        mVerse.put("15032020", "52004014");
        mVerse.put("16032020", "51004022");
        mVerse.put("17032020", "49018035");
        mVerse.put("18032020", "49005017");
        mVerse.put("19032020", "49001020");
        mVerse.put("20032020", "50012033");
        mVerse.put("21032020", "51018014");
        mVerse.put("22032020", "52009031");
        mVerse.put("23032020", "52004050");
        mVerse.put("24032020", "52005014");
        mVerse.put("25032020", "51001028");
        mVerse.put("26032020", "52005036");
        mVerse.put("27032020", "52007028");
        mVerse.put("28032020", "52007052");
        mVerse.put("29032020", "52011027");
        mVerse.put("30032020", "52008007");
        mVerse.put("31032020", "52008029");

        mVerse.put("01042020", "52008042");
        mVerse.put("02042020", "52008054");
        mVerse.put("03042020", "52010037");
        mVerse.put("04042020", "52011050");
        mVerse.put("05042020", "49026046");
        mVerse.put("06042020", "52012008");
        mVerse.put("07042020", "52013033");
        mVerse.put("08042020", "49026018");
        mVerse.put("09042020", "52013014");
        mVerse.put("10042020", "52019028");
        mVerse.put("11042020", "49028007");
        mVerse.put("12042020", "52020002");
        mVerse.put("13042020", "49028010");
        mVerse.put("14042020", "52020017");
        mVerse.put("15042020", "51024032");
        mVerse.put("16042020", "51024044");
        mVerse.put("17042020", "52021012");
        mVerse.put("18042019", "51016015");
        mVerse.put("19042020", "52020027");
        mVerse.put("20042020", "52003005");
        mVerse.put("21042020", "52003013");
        mVerse.put("22042020", "52003016");
        mVerse.put("23042020", "52003034");
        mVerse.put("24042020", "52006012");
        mVerse.put("25042020", "50016016");
        mVerse.put("26042020", "51024032");
        mVerse.put("27042020", "52006027");
        mVerse.put("28042020", "52006035");
        mVerse.put("29042020", "52006040");
        mVerse.put("30042020", "52006050");

        mVerse.put("01052020", "49013057");
        mVerse.put("02052020", "52006068");
        mVerse.put("03052020", "52010009");
        mVerse.put("04052020", "52010007");
        mVerse.put("05052020", "52010027");
        mVerse.put("06052020", "52012047");
        mVerse.put("07052020", "52013020");
        mVerse.put("08052020", "52014006");
        mVerse.put("09052020", "52014011");
        mVerse.put("10052020", "52014009");
        mVerse.put("11052020", "52014021");
        mVerse.put("12052020", "52014027");
        mVerse.put("13052020", "52015004");
        mVerse.put("14052020", "52015008");
        mVerse.put("15052020", "52015013");
        mVerse.put("16052020", "52015020");
        mVerse.put("17052020", "52014018");
        mVerse.put("18052020", "52015026");
        mVerse.put("19052020", "52016007");
        mVerse.put("20052020", "52016013");
        mVerse.put("21052020", "52016020");
        mVerse.put("22052020", "52016022");
        mVerse.put("23052020", "52016027");
        mVerse.put("24052020", "49028019");
        mVerse.put("25052020", "52016033");
        mVerse.put("26052020", "52017010");
        mVerse.put("27052020", "52017014");
        mVerse.put("28052020", "52017021");
        mVerse.put("29052020", "52021016");
        mVerse.put("30052020", "52021022");
        mVerse.put("31052020", "52020022");


        //June
        mVerse.put("01062020", "50012010");
        mVerse.put("02062020", "50012017");
        mVerse.put("03062020", "50012027");
        mVerse.put("04062020", "50012032");
        mVerse.put("05062020", "50012036");
        mVerse.put("06062020", "50012044");
        mVerse.put("07062020", "52003017");
        mVerse.put("08062020", "49005008");
        mVerse.put("09062020", "49005015");
        mVerse.put("10062020", "49005020");
        mVerse.put("11062020", "49010013");
        mVerse.put("12062020", "49005028");
        mVerse.put("13062020", "49005034");
        mVerse.put("14062020", "52006055");
        mVerse.put("15062020", "49005040");
        mVerse.put("16062020", "49005048");
        mVerse.put("17062020", "49006002");
        mVerse.put("18062020", "49006007");
        mVerse.put("19062020", "49011029");
        mVerse.put("20062020", "51002049");
        mVerse.put("21062020", "49010028");
        mVerse.put("22062020", "49007005");
        mVerse.put("23062020", "49007012");
        mVerse.put("24062020", "51001063");
        mVerse.put("25062020", "49007021");
        mVerse.put("26062020", "49008003");
        mVerse.put("27062020", "49008010");
        mVerse.put("28062020", "49010038");
        mVerse.put("29062020", "49016019");
        mVerse.put("30062020", "49008026");

        mVerse.put("01072020", "49008031");
        mVerse.put("02072020", "49009002");
        mVerse.put("03072020", "52020027");
        mVerse.put("04072020", "49009016");
        mVerse.put("05072020", "49011025");
        mVerse.put("06072020", "49009022");
        mVerse.put("07072020", "49009033");
        mVerse.put("08072020", "49010006");
        mVerse.put("09072020", "49010012");
        mVerse.put("10072020", "49010022");
        mVerse.put("11072020", "49010024");
        mVerse.put("12072020", "49013011");
        mVerse.put("13072020", "49010034");
        mVerse.put("14072020", "49011023");
        mVerse.put("15072020", "49011027");
        mVerse.put("16072020", "49011028");
        mVerse.put("17072020", "49012007");
        mVerse.put("18072020", "49012021");
        mVerse.put("19072020", "49013030");
        mVerse.put("20072020", "49012040");
        mVerse.put("21072020", "49012047");
        mVerse.put("22072020", "52020018");
        mVerse.put("23072020", "49013016");
        mVerse.put("24072020", "49013023");
        mVerse.put("25072020", "49020028");
        mVerse.put("26072020", "49013044");
        mVerse.put("27072020", "49013035");
        mVerse.put("28072020", "49013043");
        mVerse.put("29072020", "52020018");
        mVerse.put("30072020", "49013049");
        mVerse.put("31072020", "49013057");

        mVerse.put("01082020", "49014004");
        mVerse.put("02082020", "49014019");
        mVerse.put("03082020", "49014014");
        mVerse.put("04082020", "49014028");
        mVerse.put("05082020", "51011028");
        mVerse.put("06082020", "49017004");
        mVerse.put("07082020", "49016027");
        mVerse.put("08082020", "49017020");
        mVerse.put("09082020", "49014031");
        mVerse.put("10082020", "52012025");
        mVerse.put("11082020", "49018005");
        mVerse.put("12082020", "49018018");
        mVerse.put("13082020", "49018035");
        mVerse.put("14082020", "49019006");
        mVerse.put("15082020", "51001049");
        mVerse.put("16082020", "49015028");
        mVerse.put("17082020", "49019021");
        mVerse.put("18082020", "49019029");
        mVerse.put("19082020", "49020014");
        mVerse.put("20082020", "49022014");
        mVerse.put("21082020", "49022037");
        mVerse.put("22082020", "51001038");
        mVerse.put("23082020", "49016016");
        mVerse.put("24082020", "52001049");
        mVerse.put("25082020", "49023024");
        mVerse.put("26082020", "49023028");
        mVerse.put("27082020", "49024042");
        mVerse.put("28082020", "49025009");
        mVerse.put("29082020", "49025023");
        mVerse.put("30082020", "49016025");
        mVerse.put("31082020", "51004018");

        mVerse.put("01092020", "51004035");
        mVerse.put("02092020", "51004043");
        mVerse.put("03092020", "51005010");
        mVerse.put("04092020", "51005035");
        mVerse.put("05092020", "51006005");
        mVerse.put("06092020", "49018015");
        mVerse.put("07092020", "51006009");
        mVerse.put("08092020", "49001016");
        mVerse.put("09092020", "51006026");
        mVerse.put("10092020", "51006028");
        mVerse.put("11092020", "51006041");
        mVerse.put("12092020", "51006045");
        mVerse.put("13092020", "51018033");
        mVerse.put("14092020", "52003014");
        mVerse.put("15092020", "51007016");
        mVerse.put("16092020", "51007035");
        mVerse.put("17092020", "51007050");
        mVerse.put("18092020", "51008003");
        mVerse.put("19092020", "51008015");
        mVerse.put("20092020", "49020015");
        mVerse.put("21092020", "49009013");
        mVerse.put("22092020", "51008021");
        mVerse.put("23092020", "51009004");
        mVerse.put("24092020", "51009009");
        mVerse.put("25092020", "51009020");
        mVerse.put("26092020", "51009044");
        mVerse.put("27092020", "49021032");
        mVerse.put("28092020", "51009048");
        mVerse.put("29092020", "52001051");
        mVerse.put("30092020", "51009062");

        mVerse.put("01102020", "51010003");
        mVerse.put("02102020", "49018003");
        mVerse.put("03102020", "51010022");
        mVerse.put("04102020", "49021042");
        mVerse.put("05102020", "51010037");
        mVerse.put("06102020", "51010042");
        mVerse.put("07102020", "51011004");
        mVerse.put("08102020", "51011009");
        mVerse.put("09102020", "51011023");
        mVerse.put("10102020", "51011028");
        mVerse.put("11102020", "49022009");
        mVerse.put("12102020", "51011029");
        mVerse.put("13102020", "51011041");
        mVerse.put("14102020", "51011046");
        mVerse.put("15102020", "51011050");
        mVerse.put("16102020", "51012007");
        mVerse.put("17102020", "51012012");
        mVerse.put("18102020", "49022021");
        mVerse.put("19102020", "51012020");
        mVerse.put("20102020", "51012037");
        mVerse.put("21102020", "51012048");
        mVerse.put("22102020", "51012050");
        mVerse.put("23102020", "51012058");
        mVerse.put("24102020", "51013008");
        mVerse.put("25102020", "49022039");
        mVerse.put("26102020", "51013016");
        mVerse.put("27102020", "51013019");
        mVerse.put("28102020", "51006019");
        mVerse.put("29102020", "51013035");
        mVerse.put("30102020", "51014005");
        mVerse.put("31102020", "51014011");

        mVerse.put("01112020", "49005011");
        mVerse.put("02112020", "49011028");
        mVerse.put("03112020", "51014024");
        mVerse.put("04112020", "51014033");
        mVerse.put("05112020", "51015007");
        mVerse.put("06112020", "51016008");
        mVerse.put("07112020", "51016010");
        mVerse.put("08112020", "49025013");
        mVerse.put("09112020", "52002017");
        mVerse.put("10112020", "51017010");
        mVerse.put("11112020", "51017018");
        mVerse.put("12112020", "51017024");
        mVerse.put("13112020", "51017033");
        mVerse.put("14112020", "51018007");
        mVerse.put("15112020", "49025029");
        mVerse.put("16112020", "51018042");
        mVerse.put("17112020", "51019009");
        mVerse.put("18112020", "49014027");
        mVerse.put("19112020", "51019042");
        mVerse.put("20112020", "51019048");
        mVerse.put("21112020", "49012050");
        mVerse.put("22112020", "49025040");
        mVerse.put("23112020", "51021004");
        mVerse.put("24112020", "51021006");
        mVerse.put("25112020", "51021014");
        mVerse.put("26112020", "51021027");
        mVerse.put("27112020", "51021033");
        mVerse.put("28112020", "51021036");
        mVerse.put("29112020", "50013033");
        mVerse.put("30112020", "49004018");

        mVerse.put("01122020", "51010024");
        mVerse.put("02122020", "49015031");
        mVerse.put("03122020", "50016016");
        mVerse.put("04122020", "49009029");
        mVerse.put("05122020", "49009036");
        mVerse.put("06122020", "50001004");
        mVerse.put("07122020", "51005024");
        mVerse.put("08122020", "51001028");
        mVerse.put("09122020", "49011029");
        mVerse.put("10122020", "49011012");
        mVerse.put("11122020", "49011019");
        mVerse.put("12122020", "49017012");
        mVerse.put("13122020", "52001007");
        mVerse.put("14122020", "49021024");
        mVerse.put("15122020", "49021032");
        mVerse.put("16122020", "51007023");
        mVerse.put("17122020", "49001016");
        mVerse.put("18122020", "49001020");
        mVerse.put("19122020", "51001017");
        mVerse.put("20122020", "51001031");
        mVerse.put("21122020", "51001042");
        mVerse.put("22122020", "51001049");
        mVerse.put("23122020", "51001063");
        mVerse.put("24122020", "51001071");
        mVerse.put("25122020", "52001018");
        mVerse.put("26122020", "49010020");
        mVerse.put("27122020", "51002022");
        mVerse.put("28122020", "49002017");
        mVerse.put("29122020", "51002029");
        mVerse.put("30122020", "51002035");
        mVerse.put("31122020", "52001010");


        return mVerse.get(key);
    }



    public List<String> getMenuListItem() {
        List<String> menuListItem = new ArrayList<>();
        menuListItem.add("திருவிவிலியம்");
        menuListItem.add("வானொலி");
        menuListItem.add("செபமாலை");
        /*menuListItem.add("சிலுவைப் பாதை");*/
        menuListItem.add("செப வேண்டுதல்");
        menuListItem.add("திருவழிபாட்டு நாட்குறிப்பு");
        menuListItem.add("உங்கள் கருத்து");
        menuListItem.add("நன்கொடை");
        menuListItem.add("தொடர்புக்கு");
        return menuListItem;
    }

    public String getFormattedDate(String inputDate) {

        DateFormat theDateFormat = new SimpleDateFormat("ddMMyyyy");
        Date date = null;

        try {
            date = theDateFormat.parse(inputDate);
        } catch (ParseException parseException) {
        } catch(Exception exception) {
        }

        theDateFormat = new SimpleDateFormat("MMM dd, yyyy");

        return theDateFormat.format(date);
    }



}
