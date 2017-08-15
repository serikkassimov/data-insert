package services;

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
        int filial = Integer.parseInt(data.get("filial").toString());
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(data.get("date").toString().replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<Object> datas = (ArrayList<Object>) data.get("datas");
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i) != null) {
                EntityDatas entityDatas = new EntityDatas();
                entityDatas.setFilial(filial);
                entityDatas.setDate(new java.sql.Date(date.getTime()));
                entityDatas.setIndicator(i);
                entityDatas.setUpdateTime(new Date());
                entityDatas.setUser("admin");
                double value;
                if (datas.get(i) instanceof Integer) {
                    value = ((Integer) datas.get(i)).doubleValue();
                } else {
                    value = ((Double) datas.get(i)).doubleValue();
                }
                entityDatas.setValue(value);
                saveEntityDatas(entityDatas);
                System.out.println(entityDatas);
            }
        }
    }

    private EntityDatas saveEntityDatas(EntityDatas entityDatas) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityDatas.class);
        criteria.add(Restrictions.eq("id", entityDatas.getId()));
        EntityDatas datas = (EntityDatas) criteria.uniqueResult();
        if (datas == null) {
            datas = new EntityDatas();
        }
        datas.setFilial(entityDatas.getFilial());
        datas.setIndicator(entityDatas.getIndicator());
        datas.setDate(entityDatas.getDate());
        datas.setValue(entityDatas.getValue());
        datas.setUser(entityDatas.getUser());
        datas.setNote(entityDatas.getNote());
        datas.setUpdateTime(new Date());
        session.saveOrUpdate(datas);
        session.flush();
        session.close();
        return datas;
    }

    private DataRow findRow(int filial, Date date) {
        for (DataRow dataRow : dataRows) {
            if ((dataRow.getFilial() == filial) & (dataRow.getDate().equals(date))) {
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
            data.put("filial", dataRow.getFilial());
            Object[] datas = dataRow.getDatas();
            for (int i = 0; i < datas.length; i++) {
                data.put("" + (i), datas[i]);
            }
            jsonArray.put(data);
        }
        return jsonArray.toString();
    }

    class Value {
        int ind_id;
        double value;
    }

    class Row {
        Date date;
        int filail;
        ArrayList<Value> values = new ArrayList<>();
    }

    private void addToRes(ArrayList<Row> rows, EntityDatas datas) {
        Row findRow = null;
        for (Row row : rows) {
            if ((row.date.equals(datas.getDate()) & (row.filail == datas.getFilial()))) {
                findRow = row;
                break;
            }
        }
        if (findRow == null) {
            findRow = new Row();
            rows.add(findRow);
        }
        findRow.date = datas.getDate();
        findRow.filail = datas.getFilial();
        Value val = new Value();
        val.ind_id = datas.getIndicator();
        val.value = datas.getValue();
        findRow.values.add(val);
    }

    public String getFilialData(int id) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityDatas.class);
        if (id!=99) {
            criteria.add(Restrictions.eq("filial", id));
        }
        ArrayList<EntityDatas> entityDatases = (ArrayList<EntityDatas>) criteria.list();
        session.close();
        ArrayList<Row> res = new ArrayList<>();
        for (EntityDatas datas : entityDatases) {
            addToRes(res, datas);
        }
        JSONArray resJson = new JSONArray();
        for (Row r : res) {
            JSONObject object = new JSONObject();
            object.put("date", r.date);
            object.put("filial", r.filail);
            for (Value value : r.values) {
                object.put(""+value.ind_id, value.value);
            }
            resJson.put(object);
        }
        return resJson.toString();
    }
}

