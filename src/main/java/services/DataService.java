package services;

import com.google.gson.Gson;
import entity.EntityDatas;
import entityes.DataRow;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by serik on 12.08.17.
 */
@Service
public class DataService {
    ArrayList<DataRow> dataRows = new ArrayList<>();
    @Autowired
    SessionFactory sessionFactory;
    
    public void addNewDataRow(Map data) {
        Date date = null;
        int id = Integer.parseInt(data.get("id").toString());
        int datas = Integer.parseInt(data.get("datas").toString());
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(data.get("date").toString().replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DataRow row = findRow(datas, date);
        if (row == null) {
            row = new DataRow();
            saveData(id, filial, data, )
            dataRows.add(row);
        }

    }


    private DataRow findRow(int datas, Date date) {
        for (DataRow dataRow : dataRows) {
            if ((dataRow.getdatas() == datas) & (dataRow.getDate().equals(date))) {
                return dataRow;
            }
        }
        return null;
    }

    public String getAllData() {
        JSONArray jsonArray = new JSONArray();
        for (DataRow dataRow : dataRows) {
            JSONObject data = new JSONObject();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            data.put("date", format.format(dataRow.getDate()));
            data.put("datas", dataRow.getdatas());
            Object[] datas = dataRow.getDatas();
            for (int i = 0; i < datas.length; i++) {
                data.put(""+(i), datas[i]);
            }
            jsonArray.put(data);
        }
        return jsonArray.toString();
    }

    public String getdatasData() {
        return getAllData();
    }
}

