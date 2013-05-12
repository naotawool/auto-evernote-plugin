package com.evernote.jenkins.util;

import org.apache.commons.lang.BooleanUtils;

public class Util {

    /**
     * インスタンス化を抑制。
     */
    private Util() {
        // NOP
    }

    public static boolean not(boolean bool) {
        return BooleanUtils.negate(bool);
    }
}
