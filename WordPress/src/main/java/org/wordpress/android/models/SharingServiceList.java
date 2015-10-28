package org.wordpress.android.models;

import java.util.ArrayList;

public class SharingServiceList extends ArrayList<SharingService> {

    private int indexOfService(SharingService service) {
        if (service == null) return -1;

        for (int i = 0; i < this.size(); i++) {
            if (service.getLabel().equalsIgnoreCase(this.get(i).getLabel())) {
                return i;
            }
        }

        return -1;
    }

    public boolean isSameList(SharingServiceList otherList) {
        if (otherList == null || otherList.size() != this.size()) {
            return false;
        }

        for (SharingService otherService: otherList) {
            int i = this.indexOfService(otherService);
            if (i == -1) {
                return false;
            } else if (!otherService.isSameAs(this.get(i))) {
                return false;
            }
        }

        return true;
    }
}
