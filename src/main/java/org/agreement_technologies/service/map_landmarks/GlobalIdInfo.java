package org.agreement_technologies.service.map_landmarks;

import java.io.Serializable;

/**
 *
 * @author Alex
 */
public class GlobalIdInfo implements Serializable {
    private String literal;
    private int globalId;
    private static final long serialVersionUID = 9212531211040121809L;
            
    GlobalIdInfo(String l, int id) {
        literal = l;
        globalId = id;
    }
            
    String getLiteral() {
        return literal;
    }
    public int getGlobalId() {
        return globalId;
    }
}
