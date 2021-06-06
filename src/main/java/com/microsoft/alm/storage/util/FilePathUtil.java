package com.microsoft.alm.storage.util;

import com.microsoft.alm.helpers.Environment;

import java.io.File;

public class FilePathUtil {
    public static File determineParentFolder() {
        return findFirstValidFolder(
                Environment.SpecialFolder.LocalApplicationData,
                Environment.SpecialFolder.ApplicationData,
                Environment.SpecialFolder.UserProfile);
    }

    public static File findFirstValidFolder(final Environment.SpecialFolder... candidates) {
        for (final Environment.SpecialFolder candidate : candidates) {
            final String path = Environment.getFolderPath(candidate);
            if (path == null)
                continue;
            final File result = new File(path);
            if (result.isDirectory()) {
                return result;
            }
        }
        final String path = System.getenv("HOME");
        return new File(path);
    }

    public static String keyToPathName(String key) {
        return key.replaceAll("//", "").replaceAll(":", File.separator);
    }
}
