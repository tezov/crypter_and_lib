/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.toolbox;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppContext;
import com.tezov.lib_java.debug.DebugException;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

final public class Clock{
private final static ZoneId ZONE_ID_DEFAULT = ZoneId.systemDefault();
private final static ZoneOffset ZONE_OFFSET_DEFAULT = ZoneOffset(ZoneIdLocal());

private Clock(){
}

public static ZoneId ZoneIdUtc(){
    return ZoneId.of("UTC");
}

public static ZoneId ZoneIdLocal(){
    return ZONE_ID_DEFAULT;
}

public static ZoneId ZoneId(String id){
    if(id == null){
        return null;
    } else {
        return ZoneId.of(id);
    }
}

public static TimeZone TimeZoneUtc(){
    return TimeZone(ZoneIdUtc().getId());
}

public static TimeZone TimeZoneLocal(){
    return TimeZone(ZoneIdLocal().getId());
}

public static TimeZone TimeZone(String id){
    if(id == null){
        return null;
    } else {
        return TimeZone.getTimeZone(ZoneId.of(id).getId());
    }
}

public static ZoneOffset ZoneOffsetUtc(){
    return ZoneOffset(ZoneIdUtc());
}

public static ZoneOffset ZoneOffsetLocal(){
    return ZONE_OFFSET_DEFAULT;
}

public static ZoneOffset ZoneOffset(ZoneId id){
    if(id == null){
        return null;
    } else {
        return OffsetDateTime.now(id).getOffset();
    }
}


public enum FormatDate{
    DAY_MONTH_YEAR, MONTH_YEAR, YEAR;
    public static FormatDate lastFormat = null;
    public static DateTimeFormatter lastFormatter = null;

    static{
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, AppContext.getResources().getLocale());
        String pattern = ((SimpleDateFormat)formatter).toPattern();
        String[] split = pattern.split("/");
        boolean done = false;
        if(split.length >= 1){
            if(split[0].toLowerCase().startsWith("m")){
                DAY_MONTH_YEAR.pattern = "MM/dd/yyyy";
                MONTH_YEAR.pattern = "MM/yyyy";
                done = true;
            } else {
                if(split[0].toLowerCase().startsWith("y")){
                    DAY_MONTH_YEAR.pattern = "yyyy/MM/dd";
                    MONTH_YEAR.pattern = "yyyy/MM";
                    done = true;
                }
            }
        }
        if(!done){
            DAY_MONTH_YEAR.pattern = "dd/MM/yyyy";
            MONTH_YEAR.pattern = "MM/yyyy";
        }
        YEAR.pattern = "yyyy";
    }

    String pattern;
    public static FormatDate findWithPattern(String pattern){
        FormatDate[] formats = values();
        for(FormatDate f: formats){
            if(f.pattern.equals(pattern)){
                return f;
            }
        }
        return null;
    }
    public String pattern(){
        return pattern;
    }
    public DateTimeFormatter formatter(){
        if((lastFormat == this) && (lastFormatter != null)){
            return lastFormatter;
        }
        lastFormat = this;
        DateTimeFormatterBuilder lastFormatterBuilder = new DateTimeFormatterBuilder().appendPattern(pattern);
        if(this.ordinal() > DAY_MONTH_YEAR.ordinal()){
            lastFormatterBuilder.parseDefaulting(ChronoField.DAY_OF_MONTH, 1);
        }
        if(this.ordinal() > MONTH_YEAR.ordinal()){
            lastFormatterBuilder.parseDefaulting(ChronoField.MONTH_OF_YEAR, 1);
        }
        lastFormatter = lastFormatterBuilder.toFormatter();
        return lastFormatter;
    }
}

public enum FormatTime{
    H24_FULL("HH:mm:ss"), H24_FULL_SSS("HH:mm:ss.SSS"), mm_ss_SSS("mm:ss.SSS");
    String pattern;
    FormatTime(String s){
        pattern = s;
    }
    public static FormatTime findWithPattern(String pattern){
        FormatTime[] formats = values();
        for(FormatTime f: formats){
            if(f.pattern.equals(pattern)){
                return f;
            }
        }
        return null;
    }
    public String pattern(){
        return pattern;
    }
}

public enum FormatDateAndTime{
    FULL, FULL_FILE_NAME;
    public static FormatDateAndTime lastFormat = null;
    public static DateTimeFormatter lastFormatter = null;

    static{
        FULL.pattern = FormatDate.DAY_MONTH_YEAR.pattern + " " + FormatTime.H24_FULL.pattern;
        FULL_FILE_NAME.pattern = FULL.pattern.replace("/", "").replace(":", "").replace(" ", "_");
    }

    String pattern;
    public static FormatDateAndTime findWithPattern(String pattern){
        FormatDateAndTime[] formats = values();
        for(FormatDateAndTime f: formats){
            if(f.pattern.equals(pattern)){
                return f;
            }
        }
        return null;
    }
    public String pattern(){
        return pattern;
    }
    public DateTimeFormatter formatter(){
        if((lastFormat == this) && (lastFormatter != null)){
            return lastFormatter;
        }
        lastFormat = this;
        DateTimeFormatterBuilder lastFormatterBuilder = new DateTimeFormatterBuilder().appendPattern(pattern);
        lastFormatter = lastFormatterBuilder.toFormatter();
        return lastFormatter;
    }
}

public static class NanoSecond{
    public static long now(){
        return System.nanoTime();
    }

}

public static class MicroSecond{
    public static long now(){
        return System.nanoTime() / 1000;
    }

}

public static class MilliSecond{
    public static long now(){
        return Instant.now().toEpochMilli();
    }

}

public static class DateAndTime{
    public static LocalDateTime now(){
        return LocalDateTime.now();
    }
    public static String now(FormatDateAndTime format){
        return Clock.DateAndTimeTo.string(now(), format);
    }

    public static LocalDateTime nowUTC(){
        return LocalDateTime.now(ZoneIdUtc());
    }
    public static String nowUTC(FormatDateAndTime format){
        return Clock.DateAndTimeTo.string(nowUTC(), format);
    }

}

public static class Date{
    public static LocalDate now(){
        return LocalDate.now();
    }
    public static String now(FormatDate format){
        return Clock.DateTo.string(now(), format);
    }

    public static LocalDate nowUTC(){
        return LocalDate.now(ZoneIdUtc());
    }
    public static String nowUTC(FormatDate format){
        return Clock.DateTo.string(nowUTC(), format);
    }

}

public static class Time{
    public static LocalTime now(){
        return LocalTime.now();
    }
    public static String now(FormatTime format){
        return Clock.TimeTo.string(now(), format);
    }

    public static LocalTime nowUTC(){
        return LocalTime.now(ZoneIdUtc());
    }
    public static String nowUTC(FormatTime format){
        return Clock.TimeTo.string(nowUTC(), format);
    }

}

public static class To{
    public static long with(long time, TimeUnit timeUnitSource, TimeUnit timeUnitTarget){
        return timeUnitTarget.convert(time, timeUnitSource);
    }

    public static String toString(long time, TimeUnit timeUnitSource, TimeUnit timeUnitTarget){
        return String.valueOf(with(time, timeUnitSource, timeUnitTarget));
    }

    public static class Elapsed{
        public static long since(long time, TimeUnit timeUnitSource, TimeUnit timeUnitTarget){
            long currentTime = Clock.MilliSecond.now();
            long diff = currentTime - TimeUnit.MILLISECONDS.convert(time, timeUnitSource);
            return timeUnitTarget.convert(diff, TimeUnit.MILLISECONDS);
        }

        public static String toString(long time, TimeUnit timeUnitSource, TimeUnit timeUnitTarget){
            return String.valueOf(since(time, timeUnitSource, timeUnitTarget));
        }

    }

}

public static class LongTo{
    public static class MilliSecond{
        private static final TimeUnit timeUnitTarget = TimeUnit.MILLISECONDS;

        public static long with(long time, TimeUnit timeUnitSource){
            return To.with(time, timeUnitSource, timeUnitTarget);
        }

        public static String toString(long time, TimeUnit timeUnitSource){
            return To.toString(time, timeUnitSource, timeUnitTarget);
        }

        public static class Elapsed{
            public static long since(long time, TimeUnit timeUnitSource){
                return To.Elapsed.since(time, timeUnitSource, timeUnitTarget);
            }

            public static String toString(long time, TimeUnit timeUnitSource){
                return To.Elapsed.toString(time, timeUnitSource, timeUnitTarget);
            }

        }

    }

    public static class Second{
        private static final TimeUnit timeUnitTarget = TimeUnit.SECONDS;

        public static long with(long time, TimeUnit timeUnitSource){
            return To.with(time, timeUnitSource, timeUnitTarget);
        }

        public static String toString(long time, TimeUnit timeUnitSource){
            return To.toString(time, timeUnitSource, timeUnitTarget);
        }

        public static class Elapsed{
            public static long since(long time, TimeUnit timeUnitSource){
                return To.Elapsed.since(time, timeUnitSource, timeUnitTarget);
            }

            public static String toString(long time, TimeUnit timeUnitSource){
                return To.Elapsed.toString(time, timeUnitSource, timeUnitTarget);
            }

        }

    }

    public static class Minute{
        private static final TimeUnit timeUnitTarget = TimeUnit.MINUTES;

        public static long with(long time, TimeUnit timeUnitSource){
            return To.with(time, timeUnitSource, timeUnitTarget);
        }

        public static String toString(long time, TimeUnit timeUnitSource){
            return To.toString(time, timeUnitSource, timeUnitTarget);
        }

        public static class Elapsed{
            public static long since(long time, TimeUnit timeUnitSource){
                return To.Elapsed.since(time, timeUnitSource, timeUnitTarget);
            }

            public static String toString(long time, TimeUnit timeUnitSource){
                return To.Elapsed.toString(time, timeUnitSource, timeUnitTarget);
            }

        }

    }

    public static class Hour{
        private static final TimeUnit timeUnitTarget = TimeUnit.HOURS;

        public static long with(long time, TimeUnit timeUnitSource){
            return To.with(time, timeUnitSource, timeUnitTarget);
        }

        public static String toString(long time, TimeUnit timeUnitSource){
            return To.toString(time, timeUnitSource, timeUnitTarget);
        }

        public static class Elapsed{
            public static long since(long time, TimeUnit timeUnitSource){
                return To.Elapsed.since(time, timeUnitSource, timeUnitTarget);
            }

            public static String toString(long time, TimeUnit timeUnitSource){
                return To.Elapsed.toString(time, timeUnitSource, timeUnitTarget);
            }

        }

    }

    public static class Day{
        private static final TimeUnit timeUnitTarget = TimeUnit.DAYS;

        public static long with(long time, TimeUnit timeUnitSource){
            return To.with(time, timeUnitSource, timeUnitTarget);
        }

        public static String toString(long time, TimeUnit timeUnitSource){
            return To.toString(time, timeUnitSource, timeUnitTarget);
        }

        public static class Elapsed{
            public static long since(long time, TimeUnit timeUnitSource){
                return To.Elapsed.since(time, timeUnitSource, timeUnitTarget);
            }

            public static String toString(long time, TimeUnit timeUnitSource){
                return To.Elapsed.toString(time, timeUnitSource, timeUnitTarget);
            }

        }

    }

    public static class DateAndTime{
        public static LocalDateTime with(long dateAndTime, TimeUnit timeUnitSource, ZoneId zoneId){
            Instant instant = Instant.ofEpochMilli(LongTo.MilliSecond.with(dateAndTime, timeUnitSource));
            return LocalDateTime.ofInstant(instant, zoneId);
        }

        public static LocalDateTime with(long dateAndTime, TimeUnit timeUnitSource){
            return with(dateAndTime, timeUnitSource, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDateTime localDateTime){
            return DateAndTimeTo.string(localDateTime);
        }

        public static String toString(long dateAndTime, TimeUnit timeUnitSource, ZoneId zoneId){
            return toString(with(dateAndTime, timeUnitSource, zoneId));
        }

        public static String toString(long dateAndTime, TimeUnit timeUnitSource){
            return toString(dateAndTime, timeUnitSource, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDateTime localDateTime, FormatDateAndTime format){
            return DateAndTimeTo.string(localDateTime, format);
        }

        public static String toString(long dateAndTime, TimeUnit timeUnitSource, ZoneId zoneId, FormatDateAndTime format){
            return toString(with(dateAndTime, timeUnitSource, zoneId), format);
        }

        public static String toString(long dateAndTime, TimeUnit timeUnitSource, FormatDateAndTime format){
            return toString(dateAndTime, timeUnitSource, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalDateTime since(long dateAndTime, TimeUnit timeUnitSource, ZoneId zoneId){
                return DateAndTime.with(LongTo.MilliSecond.with(dateAndTime, timeUnitSource), TimeUnit.MILLISECONDS, zoneId);
            }

            public static LocalDateTime since(long dateAndTime, TimeUnit timeUnitSource){
                return since(dateAndTime, timeUnitSource, ZONE_ID_DEFAULT);
            }

            public static String toString(long dateAndTime, TimeUnit timeUnitSource, ZoneId zoneId){
                return DateAndTimeTo.string(since(dateAndTime, timeUnitSource, zoneId));
            }

            public static String toString(long dateAndTime, TimeUnit timeUnitSource){
                return toString(dateAndTime, timeUnitSource, ZONE_ID_DEFAULT);
            }

            public static String toString(long dateAndTime, TimeUnit timeUnitSource, ZoneId zoneId, FormatDateAndTime format){
                return DateAndTimeTo.string(since(dateAndTime, timeUnitSource, zoneId), format);
            }

            public static String toString(long dateAndTime, TimeUnit timeUnitSource, FormatDateAndTime format){
                return toString(dateAndTime, timeUnitSource, ZONE_ID_DEFAULT, format);
            }

        }

    }

    public static class Date{
        public static LocalDate with(long date, TimeUnit timeUnitSource, ZoneId zoneId){
            return DateAndTime.with(date, timeUnitSource, zoneId).toLocalDate();
        }

        public static LocalDate with(long date, TimeUnit timeUnitSource){
            return with(date, timeUnitSource, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDate localDate){
            return DateTo.string(localDate);
        }

        public static String toString(long date, TimeUnit timeUnitSource, ZoneId zoneId){
            return toString(with(date, timeUnitSource, zoneId));
        }

        public static String toString(long date, TimeUnit timeUnitSource){
            return toString(date, timeUnitSource, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDate localDate, FormatDate format){
            return DateTo.string(localDate, format);
        }

        public static String toString(long date, TimeUnit timeUnitSource, ZoneId zoneId, FormatDate format){
            return toString(with(date, timeUnitSource, zoneId), format);
        }

        public static String toString(long date, TimeUnit timeUnitSource, FormatDate format){
            return toString(date, timeUnitSource, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalDate since(long date, TimeUnit timeUnitSource, ZoneId zoneId){
                return Date.with(LongTo.MilliSecond.with(date, timeUnitSource), TimeUnit.MILLISECONDS, zoneId);
            }

            public static LocalDate since(long date, TimeUnit timeUnitSource){
                return since(date, timeUnitSource, ZONE_ID_DEFAULT);
            }

            public static String toString(long date, TimeUnit timeUnitSource, ZoneId zoneId){
                return Date.toString(since(date, timeUnitSource, zoneId));
            }

            public static String toString(long date, TimeUnit timeUnitSource){
                return toString(date, timeUnitSource, ZONE_ID_DEFAULT);
            }

            public static String toString(long date, TimeUnit timeUnitSource, ZoneId zoneId, FormatDate format){
                return Date.toString(since(date, timeUnitSource, zoneId), format);
            }

            public static String toString(long date, TimeUnit timeUnitSource, FormatDate format){
                return toString(date, timeUnitSource, ZONE_ID_DEFAULT, format);
            }

        }

    }

    public static class Time{
        public static LocalTime with(long time, TimeUnit timeUnitSource, ZoneId zoneId){
            return DateAndTime.with(time, timeUnitSource, zoneId).toLocalTime();
        }

        public static LocalTime with(long time, TimeUnit timeUnitSource){
            return with(time, timeUnitSource, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalTime localTime){
            return TimeTo.string(localTime);
        }

        public static String toString(long time, TimeUnit timeUnitSource, ZoneId zoneId){
            return toString(with(time, timeUnitSource, zoneId));
        }

        public static String toString(long time, TimeUnit timeUnitSource){
            return toString(time, timeUnitSource, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalTime localTime, FormatTime format){
            return TimeTo.string(localTime, format);
        }

        public static String toString(long time, TimeUnit timeUnitSource, ZoneId zoneId, FormatTime format){
            return toString(with(time, timeUnitSource, zoneId), format);
        }

        public static String toString(long time, TimeUnit timeUnitSource, FormatTime format){
            return toString(time, timeUnitSource, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalTime since(long time, TimeUnit timeUnitSource, ZoneId zoneId){
                return Time.with(LongTo.MilliSecond.with(time, timeUnitSource), TimeUnit.MILLISECONDS, zoneId);
            }

            public static LocalTime since(long time, TimeUnit timeUnitSource){
                return since(time, timeUnitSource, ZONE_ID_DEFAULT);
            }

            public static String toString(long time, TimeUnit timeUnitSource, ZoneId zoneId){
                return Time.toString(since(time, timeUnitSource, zoneId));
            }

            public static String toString(long time, TimeUnit timeUnitSource){
                return toString(time, timeUnitSource, ZONE_ID_DEFAULT);
            }

            public static String toString(long time, TimeUnit timeUnitSource, ZoneId zoneId, FormatTime format){
                return Time.toString(since(time, timeUnitSource, zoneId), format);
            }

            public static String toString(long time, TimeUnit timeUnitSource, FormatTime format){
                return toString(time, timeUnitSource, ZONE_ID_DEFAULT, format);
            }

        }

    }

}

public static class MilliSecondTo{
    private static final TimeUnit timeUnitSource = TimeUnit.MILLISECONDS;

    public static class MilliSecond{
        public static String toString(long time){
            return LongTo.MilliSecond.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.MilliSecond.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.MilliSecond.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Second{
        public static long with(long time){
            return LongTo.Second.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Second.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Second.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Second.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Minute{
        public static long with(long time){
            return LongTo.Minute.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Minute.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Minute.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Minute.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Hour{
        public static long with(long time){
            return LongTo.Hour.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Hour.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Hour.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Hour.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Day{
        public static long with(long time){
            return LongTo.Day.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Day.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Day.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Day.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class DateAndTime{
        public static LocalDateTime with(long dateAndTime, ZoneId zoneId){
            return LongTo.DateAndTime.with(dateAndTime, timeUnitSource, zoneId);
        }

        public static LocalDateTime with(long dateAndTime){
            return with(dateAndTime, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDateTime localDateTime){
            return DateAndTimeTo.string(localDateTime);
        }

        public static String toString(long dateAndTime, ZoneId zoneId){
            return toString(with(dateAndTime, zoneId));
        }

        public static String toString(long dateAndTime){
            return toString(dateAndTime, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDateTime localDateTime, FormatDateAndTime format){
            return DateAndTimeTo.string(localDateTime, format);
        }

        public static String toString(long dateAndTime, ZoneId zoneId, FormatDateAndTime format){
            return toString(with(dateAndTime, zoneId), format);
        }

        public static String toString(long dateAndTime, FormatDateAndTime format){
            return toString(dateAndTime, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalDateTime since(long dateAndTime, ZoneId zoneId){
                return DateAndTime.with(LongTo.MilliSecond.Elapsed.since(dateAndTime, timeUnitSource), zoneId);
            }

            public static LocalDateTime since(long dateAndTime){
                return since(dateAndTime, ZONE_ID_DEFAULT);
            }

            public static String toString(long dateAndTime, ZoneId zoneId){
                return DateAndTimeTo.string(since(dateAndTime, zoneId));
            }

            public static String toString(long dateAndTime){
                return toString(dateAndTime, ZONE_ID_DEFAULT);
            }

            public static String toString(long dateAndTime, ZoneId zoneId, FormatDateAndTime format){
                return DateAndTimeTo.string(since(dateAndTime, zoneId), format);
            }

            public static String toString(long dateAndTime, FormatDateAndTime format){
                return toString(dateAndTime, ZONE_ID_DEFAULT, format);
            }

        }

    }

    public static class Date{
        public static LocalDate with(long date, ZoneId zoneId){
            return DateAndTime.with(date, zoneId).toLocalDate();
        }

        public static LocalDate with(long date){
            return with(date, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDate localDate){
            return DateTo.string(localDate);
        }

        public static String toString(long date, ZoneId zoneId){
            return toString(with(date, zoneId));
        }

        public static String toString(long date){
            return toString(date, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDate localDate, FormatDate format){
            return DateTo.string(localDate, format);
        }

        public static String toString(long date, ZoneId zoneId, FormatDate format){
            return toString(with(date, zoneId), format);
        }

        public static String toString(long date, FormatDate format){
            return toString(date, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalDate since(long date, ZoneId zoneId){
                return Date.with(LongTo.MilliSecond.Elapsed.since(date, timeUnitSource), zoneId);
            }

            public static LocalDate since(long date){
                return since(date, ZONE_ID_DEFAULT);
            }

            public static String toString(long date, ZoneId zoneId){
                return Date.toString(since(date, zoneId));
            }

            public static String toString(long date){
                return toString(date, ZONE_ID_DEFAULT);
            }

            public static String toString(long date, ZoneId zoneId, FormatDate format){
                return Date.toString(since(date, zoneId), format);
            }

            public static String toString(long date, FormatDate format){
                return toString(date, ZONE_ID_DEFAULT, format);
            }

        }

    }

    public static class Time{
        public static LocalTime with(long time, ZoneId zoneId){
            return DateAndTime.with(time, zoneId).toLocalTime();
        }

        public static LocalTime with(long time){
            return with(time, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalTime localTime){
            return TimeTo.string(localTime);
        }

        public static String toString(long time, ZoneId zoneId){
            return toString(with(time, zoneId));
        }

        public static String toString(long time){
            return toString(time, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalTime localTime, FormatTime format){
            return TimeTo.string(localTime, format);
        }

        public static String toString(long time, ZoneId zoneId, FormatTime format){
            return toString(with(time, zoneId), format);
        }

        public static String toString(long time, FormatTime format){
            return toString(time, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalTime since(long time, ZoneId zoneId){
                return Time.with(LongTo.MilliSecond.Elapsed.since(time, timeUnitSource), zoneId);
            }

            public static LocalTime since(long time){
                return since(time, ZONE_ID_DEFAULT);
            }

            public static String toString(long time, ZoneId zoneId){
                return Time.toString(since(time, zoneId));
            }

            public static String toString(long time){
                return toString(time, ZONE_ID_DEFAULT);
            }

            public static String toString(long time, ZoneId zoneId, FormatTime format){
                return Time.toString(since(time, zoneId), format);
            }

            public static String toString(long time, FormatTime format){
                return toString(time, ZONE_ID_DEFAULT, format);
            }

        }

    }

}

public static class SecondTo{
    private static final TimeUnit timeUnitSource = TimeUnit.SECONDS;

    public static class MilliSecond{
        public static long with(long time){
            return LongTo.MilliSecond.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.MilliSecond.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.MilliSecond.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.MilliSecond.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Second{
        public static String toString(long time){
            return LongTo.Second.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Second.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Second.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Minute{
        public static long with(long time){
            return LongTo.Minute.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Minute.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Minute.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Minute.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Hour{
        public static long with(long time){
            return LongTo.Hour.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Hour.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Hour.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Hour.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Day{
        public static long with(long time){
            return LongTo.Day.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Day.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Day.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Day.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class DateAndTime{
        public static LocalDateTime with(long dateAndTime, ZoneId zoneId){
            return LongTo.DateAndTime.with(dateAndTime, timeUnitSource, zoneId);
        }

        public static LocalDateTime with(long dateAndTime){
            return with(dateAndTime, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDateTime localDateTime){
            return DateAndTimeTo.string(localDateTime);
        }

        public static String toString(long dateAndTime, ZoneId zoneId){
            return toString(with(dateAndTime, zoneId));
        }

        public static String toString(long dateAndTime){
            return toString(dateAndTime, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDateTime localDateTime, FormatDateAndTime format){
            return DateAndTimeTo.string(localDateTime, format);
        }

        public static String toString(long dateAndTime, ZoneId zoneId, FormatDateAndTime format){
            return toString(with(dateAndTime, zoneId), format);
        }

        public static String toString(long dateAndTime, FormatDateAndTime format){
            return toString(dateAndTime, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalDateTime since(long dateAndTime, ZoneId zoneId){
                return DateAndTime.with(LongTo.MilliSecond.Elapsed.since(dateAndTime, timeUnitSource), zoneId);
            }

            public static LocalDateTime since(long dateAndTime){
                return since(dateAndTime, ZONE_ID_DEFAULT);
            }

            public static String toString(long dateAndTime, ZoneId zoneId){
                return DateAndTimeTo.string(since(dateAndTime, zoneId));
            }

            public static String toString(long dateAndTime){
                return toString(dateAndTime, ZONE_ID_DEFAULT);
            }

            public static String toString(long dateAndTime, ZoneId zoneId, FormatDateAndTime format){
                return DateAndTimeTo.string(since(dateAndTime, zoneId), format);
            }

            public static String toString(long dateAndTime, FormatDateAndTime format){
                return toString(dateAndTime, ZONE_ID_DEFAULT, format);
            }

        }

    }

    public static class Date{
        public static LocalDate with(long date, ZoneId zoneId){
            return DateAndTime.with(date, zoneId).toLocalDate();
        }

        public static LocalDate with(long date){
            return with(date, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDate localDate){
            return DateTo.string(localDate);
        }

        public static String toString(long date, ZoneId zoneId){
            return toString(with(date, zoneId));
        }

        public static String toString(long date){
            return toString(date, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDate localDate, FormatDate format){
            return DateTo.string(localDate, format);
        }

        public static String toString(long date, ZoneId zoneId, FormatDate format){
            return toString(with(date, zoneId), format);
        }

        public static String toString(long date, FormatDate format){
            return toString(date, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalDate since(long date, ZoneId zoneId){
                return Date.with(LongTo.MilliSecond.Elapsed.since(date, timeUnitSource), zoneId);
            }

            public static LocalDate since(long date){
                return since(date, ZONE_ID_DEFAULT);
            }

            public static String toString(long date, ZoneId zoneId){
                return Date.toString(since(date, zoneId));
            }

            public static String toString(long date){
                return toString(date, ZONE_ID_DEFAULT);
            }

            public static String toString(long date, ZoneId zoneId, FormatDate format){
                return Date.toString(since(date, zoneId), format);
            }

            public static String toString(long date, FormatDate format){
                return toString(date, ZONE_ID_DEFAULT, format);
            }

        }

    }

    public static class Time{
        public static LocalTime with(long time, ZoneId zoneId){
            return DateAndTime.with(time, zoneId).toLocalTime();
        }

        public static LocalTime with(long time){
            return with(time, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalTime localTime){
            return TimeTo.string(localTime);
        }

        public static String toString(long time, ZoneId zoneId){
            return toString(with(time, zoneId));
        }

        public static String toString(long time){
            return toString(time, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalTime localTime, FormatTime format){
            return TimeTo.string(localTime, format);
        }

        public static String toString(long time, ZoneId zoneId, FormatTime format){
            return toString(with(time, zoneId), format);
        }

        public static String toString(long time, FormatTime format){
            return toString(time, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalTime since(long time, ZoneId zoneId){
                return Time.with(LongTo.MilliSecond.Elapsed.since(time, timeUnitSource), zoneId);
            }

            public static LocalTime since(long time){
                return since(time, ZONE_ID_DEFAULT);
            }

            public static String toString(long time, ZoneId zoneId){
                return Time.toString(since(time, zoneId));
            }

            public static String toString(long time){
                return toString(time, ZONE_ID_DEFAULT);
            }

            public static String toString(long time, ZoneId zoneId, FormatTime format){
                return Time.toString(since(time, zoneId), format);
            }

            public static String toString(long time, FormatTime format){
                return toString(time, ZONE_ID_DEFAULT, format);
            }

        }

    }

}

public static class MinuteTo{
    private static final TimeUnit timeUnitSource = TimeUnit.MINUTES;

    public static class MilliSecond{
        public static long with(long time){
            return LongTo.MilliSecond.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.MilliSecond.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.MilliSecond.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.MilliSecond.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Second{
        public static String toString(long time){
            return LongTo.Second.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Second.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Second.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Minute{
        public static long with(long time){
            return LongTo.Minute.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Minute.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Minute.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Minute.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Hour{
        public static long with(long time){
            return LongTo.Hour.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Hour.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Hour.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Hour.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class Day{
        public static long with(long time){
            return LongTo.Day.with(time, timeUnitSource);
        }

        public static String toString(long time){
            return LongTo.Day.toString(time, timeUnitSource);
        }

        public static class Elapsed{
            public static long since(long time){
                return LongTo.Day.Elapsed.since(time, timeUnitSource);
            }

            public static String toString(long time){
                return LongTo.Day.Elapsed.toString(time, timeUnitSource);
            }

        }

    }

    public static class DateAndTime{
        public static LocalDateTime with(long dateAndTime, ZoneId zoneId){
            return LongTo.DateAndTime.with(dateAndTime, timeUnitSource, zoneId);
        }

        public static LocalDateTime with(long dateAndTime){
            return with(dateAndTime, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDateTime localDateTime){
            return DateAndTimeTo.string(localDateTime);
        }

        public static String toString(long dateAndTime, ZoneId zoneId){
            return toString(with(dateAndTime, zoneId));
        }

        public static String toString(long dateAndTime){
            return toString(dateAndTime, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDateTime localDateTime, FormatDateAndTime format){
            return DateAndTimeTo.string(localDateTime, format);
        }

        public static String toString(long dateAndTime, ZoneId zoneId, FormatDateAndTime format){
            return toString(with(dateAndTime, zoneId), format);
        }

        public static String toString(long dateAndTime, FormatDateAndTime format){
            return toString(dateAndTime, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalDateTime since(long dateAndTime, ZoneId zoneId){
                return DateAndTime.with(LongTo.MilliSecond.Elapsed.since(dateAndTime, timeUnitSource), zoneId);
            }

            public static LocalDateTime since(long dateAndTime){
                return since(dateAndTime, ZONE_ID_DEFAULT);
            }

            public static String toString(long dateAndTime, ZoneId zoneId){
                return DateAndTimeTo.string(since(dateAndTime, zoneId));
            }

            public static String toString(long dateAndTime){
                return toString(dateAndTime, ZONE_ID_DEFAULT);
            }

            public static String toString(long dateAndTime, ZoneId zoneId, FormatDateAndTime format){
                return DateAndTimeTo.string(since(dateAndTime, zoneId), format);
            }

            public static String toString(long dateAndTime, FormatDateAndTime format){
                return toString(dateAndTime, ZONE_ID_DEFAULT, format);
            }

        }

    }

    public static class Date{
        public static LocalDate with(long date, ZoneId zoneId){
            return DateAndTime.with(date, zoneId).toLocalDate();
        }

        public static LocalDate with(long date){
            return with(date, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDate localDate){
            return DateTo.string(localDate);
        }

        public static String toString(long date, ZoneId zoneId){
            return toString(with(date, zoneId));
        }

        public static String toString(long date){
            return toString(date, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalDate localDate, FormatDate format){
            return DateTo.string(localDate, format);
        }

        public static String toString(long date, ZoneId zoneId, FormatDate format){
            return toString(with(date, zoneId), format);
        }

        public static String toString(long date, FormatDate format){
            return toString(date, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalDate since(long date, ZoneId zoneId){
                return Date.with(LongTo.MilliSecond.Elapsed.since(date, timeUnitSource), zoneId);
            }

            public static LocalDate since(long date){
                return since(date, ZONE_ID_DEFAULT);
            }

            public static String toString(long date, ZoneId zoneId){
                return Date.toString(since(date, zoneId));
            }

            public static String toString(long date){
                return toString(date, ZONE_ID_DEFAULT);
            }

            public static String toString(long date, ZoneId zoneId, FormatDate format){
                return Date.toString(since(date, zoneId), format);
            }

            public static String toString(long date, FormatDate format){
                return toString(date, ZONE_ID_DEFAULT, format);
            }

        }

    }

    public static class Time{
        public static LocalTime with(long time, ZoneId zoneId){
            return DateAndTime.with(time, zoneId).toLocalTime();
        }

        public static LocalTime with(long time){
            return with(time, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalTime localTime){
            return TimeTo.string(localTime);
        }

        public static String toString(long time, ZoneId zoneId){
            return toString(with(time, zoneId));
        }

        public static String toString(long time){
            return toString(time, ZONE_ID_DEFAULT);
        }

        public static String toString(LocalTime localTime, FormatTime format){
            return TimeTo.string(localTime, format);
        }

        public static String toString(long time, ZoneId zoneId, FormatTime format){
            return toString(with(time, zoneId), format);
        }

        public static String toString(long time, FormatTime format){
            return toString(time, ZONE_ID_DEFAULT, format);
        }

        public static class Elapsed{
            public static LocalTime since(long time, ZoneId zoneId){
                return Time.with(LongTo.MilliSecond.Elapsed.since(time, timeUnitSource), zoneId);
            }

            public static LocalTime since(long time){
                return since(time, ZONE_ID_DEFAULT);
            }

            public static String toString(long time, ZoneId zoneId){
                return Time.toString(since(time, zoneId));
            }

            public static String toString(long time){
                return toString(time, ZONE_ID_DEFAULT);
            }

            public static String toString(long time, ZoneId zoneId, FormatTime format){
                return Time.toString(since(time, zoneId), format);
            }

            public static String toString(long time, FormatTime format){
                return toString(time, ZONE_ID_DEFAULT, format);
            }

        }

    }

}

public static class DateAndTimeTo{
    public static String string(LocalDateTime dateAndTime){
        return dateAndTime.format(FormatDateAndTime.FULL.formatter());
    }

    public static String string(LocalDateTime dateAndTime, FormatDateAndTime format){
        return dateAndTime.format(format.formatter());
    }

    public static class MilliSecond{
        public static long with(LocalDateTime dateAndTime, ZoneId zoneId){
            return dateAndTime.toInstant(ZoneOffset(zoneId)).toEpochMilli();
        }

        public static long with(LocalDateTime dateAndTime){
            return with(dateAndTime, ZONE_OFFSET_DEFAULT);
        }

        public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.MilliSecond.toString(with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static String toString(LocalDateTime dateAndTime){
            return toString(dateAndTime, ZONE_ID_DEFAULT);
        }

        public static class Elapsed{
            public static long since(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.MilliSecond.Elapsed.since(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static long since(LocalDateTime dateAndTime){
                return MilliSecondTo.MilliSecond.Elapsed.since(MilliSecond.with(dateAndTime));
            }

            public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.MilliSecond.Elapsed.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static String toString(LocalDateTime dateAndTime){
                return MilliSecondTo.MilliSecond.Elapsed.toString(MilliSecond.with(dateAndTime));
            }

        }

    }

    public static class Second{
        public static long with(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.Second.with(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static long with(LocalDateTime dateAndTime){
            return MilliSecondTo.Second.with(MilliSecond.with(dateAndTime));
        }

        public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.Second.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static String toString(LocalDateTime dateAndTime){
            return MilliSecondTo.Second.toString(MilliSecond.with(dateAndTime));
        }

        public static class Elapsed{
            public static long since(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.Second.Elapsed.since(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static long since(LocalDateTime dateAndTime){
                return MilliSecondTo.Second.Elapsed.since(MilliSecond.with(dateAndTime));
            }

            public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.Second.Elapsed.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static String toString(LocalDateTime dateAndTime){
                return MilliSecondTo.Second.Elapsed.toString(MilliSecond.with(dateAndTime));
            }

        }

    }

    public static class Minute{
        public static long with(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.Minute.with(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static long with(LocalDateTime dateAndTime){
            return MilliSecondTo.Minute.with(MilliSecond.with(dateAndTime));
        }

        public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.Minute.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static String toString(LocalDateTime dateAndTime){
            return MilliSecondTo.Minute.toString(MilliSecond.with(dateAndTime));
        }

        public static class Elapsed{
            public static long since(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.Minute.Elapsed.since(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static long since(LocalDateTime dateAndTime){
                return MilliSecondTo.Minute.Elapsed.since(MilliSecond.with(dateAndTime));
            }

            public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.Minute.Elapsed.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static String toString(LocalDateTime dateAndTime){
                return MilliSecondTo.Minute.Elapsed.toString(MilliSecond.with(dateAndTime));
            }

        }

    }

    public static class Hour{
        public static long with(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.Hour.with(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static long with(LocalDateTime dateAndTime){
            return MilliSecondTo.Hour.with(MilliSecond.with(dateAndTime));
        }

        public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.Hour.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static String toString(LocalDateTime dateAndTime){
            return MilliSecondTo.Hour.toString(MilliSecond.with(dateAndTime));
        }

        public static class Elapsed{
            public static long since(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.Hour.Elapsed.since(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static long since(LocalDateTime dateAndTime){
                return MilliSecondTo.Hour.Elapsed.since(MilliSecond.with(dateAndTime));
            }

            public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.Hour.Elapsed.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static String toString(LocalDateTime dateAndTime){
                return MilliSecondTo.Hour.Elapsed.toString(MilliSecond.with(dateAndTime));
            }

        }

    }

    public static class Day{
        public static long with(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.Day.with(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static long with(LocalDateTime dateAndTime){
            return MilliSecondTo.Day.with(MilliSecond.with(dateAndTime));
        }

        public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
            return MilliSecondTo.Day.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
        }

        public static String toString(LocalDateTime dateAndTime){
            return MilliSecondTo.Day.toString(MilliSecond.with(dateAndTime));
        }

        public static class Elapsed{
            public static long since(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.Day.Elapsed.since(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static long since(LocalDateTime dateAndTime){
                return MilliSecondTo.Day.Elapsed.since(MilliSecond.with(dateAndTime));
            }

            public static String toString(LocalDateTime dateAndTime, ZoneId zoneId){
                return MilliSecondTo.Day.Elapsed.toString(MilliSecond.with(dateAndTime, ZoneOffset(zoneId)));
            }

            public static String toString(LocalDateTime dateAndTime){
                return MilliSecondTo.Day.Elapsed.toString(MilliSecond.with(dateAndTime));
            }

        }

    }

    public static class Date{
        public static LocalDate with(LocalDateTime dateAndTime){
            return dateAndTime.toLocalDate();
        }

        public static String toString(LocalDateTime dateAndTime){
            return DateTo.string(with(dateAndTime));
        }

        public static String toString(LocalDateTime dateAndTime, FormatDate format){
            return DateTo.string(with(dateAndTime), format);
        }

    }

    public static class Time{
        public static LocalTime with(LocalDateTime dateAndTime){
            return dateAndTime.toLocalTime();
        }

        public static String toString(LocalDateTime dateAndTime){
            return TimeTo.string(with(dateAndTime));
        }

        public static String toString(LocalDateTime dateAndTime, FormatTime format){
            return TimeTo.string(with(dateAndTime), format);
        }

    }

}

public static class DateTo{
    public static String string(LocalDate date){
        return date.format(FormatDate.DAY_MONTH_YEAR.formatter());
    }

    public static String string(LocalDate date, FormatDate format){
        return date.format(format.formatter());
    }

    public static class MilliSecond{
        public static long with(LocalDate date, ZoneId zoneId){
            LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
            return localDateTime.toInstant(ZoneOffset(zoneId)).toEpochMilli();
        }

        public static long with(LocalDate date){
            return with(date, ZONE_OFFSET_DEFAULT);
        }

        public static String toString(LocalDate date, ZoneId zoneId){
            return MilliSecondTo.MilliSecond.toString(with(date, ZoneOffset(zoneId)));
        }

        public static String toString(LocalDate date){
            return toString(date, ZONE_ID_DEFAULT);
        }

        public static class Elapsed{
            public static long since(LocalDate date, ZoneId zoneId){
                return MilliSecondTo.MilliSecond.Elapsed.since(MilliSecond.with(date, ZoneOffset(zoneId)));
            }

            public static long since(LocalDate date){
                return MilliSecondTo.MilliSecond.Elapsed.since(MilliSecond.with(date));
            }

            public static String toString(LocalDate date, ZoneId zoneId){
                return MilliSecondTo.MilliSecond.Elapsed.toString(MilliSecond.with(date, ZoneOffset(zoneId)));
            }

            public static String toString(LocalDate date){
                return MilliSecondTo.MilliSecond.Elapsed.toString(MilliSecond.with(date));
            }

        }

    }

}

public static class TimeTo{
    public static String string(LocalTime time){
        return time.format(DateTimeFormatter.ofPattern(FormatTime.H24_FULL.pattern()));
    }

    public static String string(LocalTime time, FormatTime format){
        return time.format(DateTimeFormatter.ofPattern(format.pattern()));
    }

    public static class MilliSecond{
        public static long with(LocalTime time, ZoneId zoneId){
            LocalDateTime localTime = LocalDateTime.of(LocalDate.ofEpochDay(0), time);
            return localTime.toInstant(ZoneOffset(zoneId)).toEpochMilli();
        }

        public static long with(LocalTime time){
            return with(time, ZONE_OFFSET_DEFAULT);
        }

        public static String toString(LocalTime time, ZoneId zoneId){
            return MilliSecondTo.MilliSecond.toString(with(time, ZoneOffset(zoneId)));
        }

        public static String toString(LocalTime time){
            return toString(time, ZONE_ID_DEFAULT);
        }

        public static class Elapsed{
            public static long since(LocalTime time, ZoneId zoneId){
                return MilliSecondTo.MilliSecond.Elapsed.since(MilliSecond.with(time, ZoneOffset(zoneId)));
            }

            public static long since(LocalTime time){
                return MilliSecondTo.MilliSecond.Elapsed.since(MilliSecond.with(time));
            }

            public static String toString(LocalTime time, ZoneId zoneId){
                return MilliSecondTo.MilliSecond.Elapsed.toString(MilliSecond.with(time, ZoneOffset(zoneId)));
            }

            public static String toString(LocalTime time){
                return MilliSecondTo.MilliSecond.Elapsed.toString(MilliSecond.with(time));
            }

        }

    }

}

public static class StringTo{
    public static class DateAndTime{
        public static LocalDateTime with(String s){
            try{
                return LocalDateTime.parse(s, FormatDateAndTime.FULL.formatter());
            } catch(java.lang.Throwable e){

DebugException.start().logHidden(e).end();

            }
            return null;
        }

        public static LocalDateTime with(String s, FormatDateAndTime format){
            try{
                return LocalDateTime.parse(s, format.formatter());
            } catch(java.lang.Throwable e){

DebugException.start().logHidden(e).end();

            }
            return null;
        }

    }

    public static class Date{
        public static LocalDate with(String s){
            try{
                return LocalDate.parse(s, FormatDate.DAY_MONTH_YEAR.formatter());
            } catch(java.lang.Throwable e){

DebugException.start().logHidden(e).end();

            }
            return null;
        }

        public static LocalDate with(String s, FormatDate format){
            try{
                return LocalDate.parse(s, format.formatter());
            } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

            }
            return null;
        }

    }

    public static class Time{
        public static LocalTime with(String s){
            try{
                return LocalTime.parse(s, DateTimeFormatter.ofPattern(FormatTime.H24_FULL.pattern()));
            } catch(java.lang.Throwable e){

DebugException.start().logHidden(e).end();

            }
            return null;
        }

        public static LocalTime with(String s, FormatTime format){
            try{
                return LocalTime.parse(s, DateTimeFormatter.ofPattern(format.pattern()));
            } catch(java.lang.Throwable e){

DebugException.start().logHidden(e).end();

            }
            return null;
        }

    }

}

}
