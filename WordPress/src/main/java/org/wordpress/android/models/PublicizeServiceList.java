package org.wordpress.android.models;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class PublicizeServiceList extends ArrayList<PublicizeService> {

    private int indexOfService(PublicizeService service) {
        if (service == null) return -1;

        for (int i = 0; i < this.size(); i++) {
            if (service.getLabel().equalsIgnoreCase(this.get(i).getLabel())) {
                return i;
            }
        }

        return -1;
    }

    public boolean isSameAs(PublicizeServiceList otherList) {
        if (otherList == null || otherList.size() != this.size()) {
            return false;
        }

        for (PublicizeService otherService: otherList) {
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
     * passed JSON is the response from /meta/publicize
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
    public static PublicizeServiceList fromJson(JSONObject json) {
        PublicizeServiceList serviceList = new PublicizeServiceList();
        if (json == null) return serviceList;

        JSONObject jsonServiceList = json.optJSONObject("services");
        if (jsonServiceList == null) return serviceList;

        Iterator<String> it = jsonServiceList.keys();
        while (it.hasNext()) {
            String serviceName = it.next();
            JSONObject jsonService = jsonServiceList.optJSONObject(serviceName);

            PublicizeService service = new PublicizeService();
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
