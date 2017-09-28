package services;

import com.google.gson.Gson;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by serik on 11.08.17.
 */
@Service
public class UtilService {
    @Autowired
    SessionFactory sessionFactory;


    public String getMonths() {
        JSONArray res = new JSONArray();
        addJsonObject(res, 1, "Январь");
        addJsonObject(res, 2, "Февраль");
        addJsonObject(res, 3, "Март");
        addJsonObject(res, 4, "Апрель");
        addJsonObject(res, 5, "Май");
        addJsonObject(res, 6, "Июнь");
        addJsonObject(res, 7, "Июль");
        addJsonObject(res, 8, "Август");
        addJsonObject(res, 9, "Сентябрь");
        addJsonObject(res, 10, "Октябрь");
        addJsonObject(res, 11, "Ноябрь");
        addJsonObject(res, 12, "Декабрь");
        return res.toString();
    }

    private void addJsonObject(JSONArray res, int value, String label) {
        JSONObject object = new JSONObject();
        object.put("value", value);
        object.put("label", label);
        res.put(object);
    }
}
