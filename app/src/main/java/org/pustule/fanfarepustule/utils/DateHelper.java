package org.pustule.fanfarepustule.utils;

import android.support.annotation.Nullable;

import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Paul Mougin on 19/05/2017.
 */

public final class DateHelper {

    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
    private static final SimpleDateFormat FULL_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSZ", Locale.FRANCE);

    private static final SimpleDateFormat RETURN_FULL_FORMAT = new SimpleDateFormat("EEEE dd MMMM, HH:mm", Locale.FRANCE);
    private static final SimpleDateFormat RETURN_FORMAT = new SimpleDateFormat("EEEE dd MMMM", Locale.FRANCE);


    @Nullable
    public static String getDateFormattedFromDateTime(DateTime dateTime) {
        try {
            if (dateTime.isDateOnly()) {
                return StringHelper.capitalizeFirstLetter(RETURN_FORMAT.format(DATE_ONLY_FORMAT.parse(dateTime.toString())));
            } else {
                return StringHelper.capitalizeFirstLetter(RETURN_FULL_FORMAT.format(FULL_FORMAT.parse(dateTime.toString())));
            }
        } catch (ParseException e) {
            return "Date inconnue";
        }
    }
}
