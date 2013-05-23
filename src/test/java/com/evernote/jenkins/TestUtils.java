package com.evernote.jenkins;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public final class TestUtils {

    /**
     * インスタンス化を抑制。
     */
    private TestUtils() {
        // NOP
    }

    public static Calendar createCalendar(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        return DateUtils.truncate(cal, Calendar.DAY_OF_MONTH);
    }

    public static Date createDate(int year, int month, int day) {
        return createCalendar(year, month, day).getTime();
    }

    /**
     * リフレクションを使用してフィールド値を取得する。
     *
     * @param target オブジェクト
     * @param name フィールド名
     * @return フィールド値
     * @see org.springframework.test.util.ReflectionTestUtils#getField(Object,
     *      String)
     */
    public static Object getField(Object target, String name) {
        Assert.notNull(target, "Target object must not be null");
        Field field = ReflectionUtils.findField(target.getClass(), name);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + name + "] on target ["
                    + target + "]");
        }

        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, target);
    }
}
