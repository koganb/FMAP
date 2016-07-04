package org.agreement_technologies.service.map_landmarks;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Message content used to calculate global identifiers for the landmarks
 * @author Alex
 */
public class MessageContentGlobalId implements Serializable {
        private ArrayList<GlobalIdInfo> literals;
        private int currentGlobalIndex;
        private static final long serialVersionUID = 6012531211040121809L;
        
        public MessageContentGlobalId(ArrayList<GlobalIdInfo> l) {
            literals = l;
        }
        
        public ArrayList<GlobalIdInfo> getLiterals() {
            return literals;
        }
        public int getCurrentGlobalIndex() {
            return currentGlobalIndex;
        }
        public void setCurrentGlobalIndex(int id) {
            currentGlobalIndex = id;
        }
}
