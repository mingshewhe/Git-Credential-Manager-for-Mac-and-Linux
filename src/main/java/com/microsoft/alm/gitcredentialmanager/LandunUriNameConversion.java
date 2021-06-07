// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.gitcredentialmanager;

import com.microsoft.alm.constant.Constants;
import com.microsoft.alm.helpers.StringHelper;
import com.microsoft.alm.secret.Secret;

import java.net.URI;

public class LandunUriNameConversion implements Secret.IUriNameConversion {

    private static final String SecretsPrefix = "devops";

    @Override
    public String convert(URI targetUri, String namespace) {
        final StringBuilder sb = new StringBuilder(SecretsPrefix);
        if (!StringHelper.isNullOrEmpty(System.getenv(Constants.BK_CI_PIPELINE_ID))) {
            sb.append(":").append(System.getenv(Constants.BK_CI_PIPELINE_ID));
        }
        if (!StringHelper.isNullOrEmpty(System.getenv(Constants.DEVOPS_VM_SEQ_ID))) {
            sb.append(":").append(System.getenv(Constants.DEVOPS_VM_SEQ_ID));
        }
        sb.append(":").append(namespace);
        return Secret.uriToName(targetUri, sb.toString());
    }
}
