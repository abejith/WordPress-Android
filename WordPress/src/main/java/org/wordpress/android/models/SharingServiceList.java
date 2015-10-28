package org.wordpress.android.models;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

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

    /*
    "services": {
		"facebook": {
			"label": "Facebook",
			"description": "Publish your posts to your Facebook wall or page.",
			"noticon": "noticon-facebook-alt",
			"icon": "http://i.wordpress.com/wp-content/admin-plugins/publicize/assets/publicize-fb-2x.png",
			"screenshots": [],
			"connect": "https://public-api.wordpress.com/connect/?action=request&kr_nonce=eb5ccc2500&nonce=a4f35f026e&for=connect&service=facebook&blog=5836086&kr_blog_nonce=7160750c45&magic=keyring"
		},
		...
     */
    public static SharingServiceList fromJson(JSONObject json) {
        SharingServiceList serviceList = new SharingServiceList();
        if (json == null) return serviceList;

        JSONObject jsonServiceList = json.optJSONObject("services");
        if (jsonServiceList == null) return serviceList;

        Iterator<String> it = jsonServiceList.keys();
        while (it.hasNext()) {
            String serviceName = it.next();
            JSONObject jsonService = jsonServiceList.optJSONObject(serviceName);

            SharingService service = new SharingService();
            service.setName(serviceName);
            service.setLabel(jsonService.optString("label"));
            service.setDescription(jsonService.optString("description"));
            service.setNoticon(jsonService.optString("noticon"));
            service.setIconUrl(jsonService.optString("icon"));
            service.setConnectUrl(jsonService.optString("connect"));
            serviceList.add(service);
        }

        return serviceList;
    }
}
