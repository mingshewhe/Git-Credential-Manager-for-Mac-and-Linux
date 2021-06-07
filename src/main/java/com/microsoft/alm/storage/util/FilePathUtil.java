// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.storage.util;

import com.microsoft.alm.helpers.Environment;
import com.microsoft.alm.helpers.StringHelper;

import java.io.File;

public class FilePathUtil {
    public static File determineParentFolder() {
        return findFirstValidFolder(
                Environment.SpecialFolder.LocalApplicationData,
                Environment.SpecialFolder.ApplicationData,
                Environment.SpecialFolder.UserProfile);
    }

    private static File findFirstValidFolder(final Environment.SpecialFolder... candidates) {
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
        return StringHelper.join(File.separator, key.replaceAll("//", "").split(":"));
    }
}
