package keaton.moneybank.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by keaton on 06.05.2014.
 * Хелпер для всякого
 */
public class Tools {
    static String dateFormat = "%1$s, %2$s";
    static String TODAY = "сегодня";
    static String YESTERDAY = "вчера";
    /**
     * Склонение по числам для русского языка
     *
     * http://www.gnu.org/software/gettext/manual/html_mono/gettext.html#Plural-forms
     *
     * @param n int
     * @param form1 String $n заканчивается на 1, но не на 11
     * @param form2 String $n заканчивается на 2, 3 или 4, но не 12, 13 или 14
     * @param form3 String $n заканчивается на 5-20
     * @return String
     */


    public static String nget_text(int n, String form1, String form2, String form3) {
        String result = "";
        String[] w = {form1,form2,form3};
        int index = (n%10==1 && n%100!=11) ? 0 :
                ((n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20)) ? 1 : 2);
        return w[index];
    }

    public static String getDateFromString(String dateStr) {
        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
            return getDate(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getDate(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        Calendar cal2 = Calendar.getInstance();

        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                return String.format(dateFormat, TODAY,
                        new SimpleDateFormat("HH:mm").format(date));
            } else if (cal1.get(Calendar.DAY_OF_YEAR) + 1 == cal2.get(Calendar.DAY_OF_YEAR)) {
                return String.format(dateFormat, YESTERDAY,
                        new SimpleDateFormat("HH:mm").format(date));
            }
        }
        return new SimpleDateFormat("dd MMM yyyy, HH:mm").format(date);
    }



    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static String numberFormat(Long i) {

        NumberFormat decimal_formatter = NumberFormat.getInstance(Locale.FRENCH);
        return decimal_formatter.format(i);
    }

    /**
     * Отправлят почту
     * @param addresses
     */
    public static void composeEmail(String[] addresses, String subject, Activity ctx) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        try {
            ctx.startActivity(Intent.createChooser(intent, "Написать письмо..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx,
                    "Кажется у Вас не установлен Email клиент.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Открывает страницу в браузере
     * @param url
     * @param  ctx
     */
    public static void openWebPage(String url, Context ctx)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        ctx.startActivity(intent);
    }

    public static Boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




}
