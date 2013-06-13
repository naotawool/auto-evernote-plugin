package com.evernote.jenkins.plugin;

import org.apache.commons.lang.StringUtils;

public interface Autable {

    public Guid guid();

    public String type();

    public String name();

    public static class NullAutable implements Autable {

        private static NullAutable INSTANCE = new NullAutable();

        @Override
        public Guid guid() {
            return new Guid(StringUtils.EMPTY);
        }

        public static NullAutable getInstance() {
            return INSTANCE;
        }

        @Override
        public String type() {
            return StringUtils.EMPTY;
        }

        @Override
        public String name() {
            return StringUtils.EMPTY;
        }
    }
}
