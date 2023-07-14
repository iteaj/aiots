package com.iteaj.framework.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public interface AuthorizationService {

    default Collection<String> getRoles(Serializable userId) {
        return Collections.EMPTY_SET;
    }

    Collection<String> getPermissions(Serializable userId);
}
