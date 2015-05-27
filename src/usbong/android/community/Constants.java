package usbong.android.community;

public class Constants {
    private Constants() {

    }
    //HTTP constants
    public static final int REGISTRATION_TIMEOUT = 10000;
    public static final int WAIT_TIMEOUT = 30 * 1000;
    //public static final String HOSTNAME = "192.168.56.1";
    public static final String HOSTNAME = "shrimptalusan.hostei.com";
    public static final String FITS_LIST_SERVER = "http://" + HOSTNAME + "/usbong/getList.php";
    public static final String FITS_ITERATE_DOWNLOAD = "http://" + HOSTNAME + "/usbong/rate_download_count.php";    
    //JSON constants
    public static final String FILENAME = "FILENAME";
    public static final String FILEPATH = "FILEPATH";
    public static final String RATING = "RATING";
    public static final String UPLOADER = "UPLOADER";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String ICON = "ICON";
    public static final String YOUTUBELINK = "YOUTUBELINK";
    public static final String DATEUPLOADED = "DATEUPLOADED";
    public static final String DOWNLOADCOUNT = "DOWNLOADCOUNT";
    
    public static final String COLUMN = "COLUMN";
    public static final String ACTION = "ACTION";
    //API KEY
    public static final String YOUTUBE_API_KEY = "AIzaSyCQ-Awkvj5nq5j5_9GqCKwxDzEsxjVfEIc";
    
    //BUNDLE KEY
    public static final String BUNDLE = "FITS_BUNDLE";
    public static final String JSON_KEY = "FITS_JSON_KEY";
    public static final String UTREE_KEY = "UTREE_KEY";
}
