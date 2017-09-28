package services;

import com.google.gson.Gson;
import entity.EntityDimensionElement;
import entity.EntityIndicators;
import entity.EntityIndicators;
import entity.EntityIndicators;
import entityes.Indicator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by serik on 11.08.17.
 */
@Service
public class IndicatorSevice {
    @Autowired
    SessionFactory sessionFactory;

    public String getAllFilials(){
        List<EntityIndicators> indicatorList = loadIndicators();
        Gson gson = new Gson();
        return gson.toJson(indicatorList);
    }

    private List<EntityIndicators> loadIndicators() {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityIndicators.class);
        List<EntityIndicators>  indicators = criteria.list();
        session.close();
        return indicators;
    }

    public EntityIndicators addNewIndicator(Map param) {
        int id = (int) param.get("id");
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityIndicators.class);
        criteria.add(Restrictions.eq("id", id));
        EntityIndicators filial  = (EntityIndicators) criteria.uniqueResult();
        if (filial==null) {
            filial = new EntityIndicators();
        }
        filial.setName(param.get("name").toString());
        filial.setCode("temp");
        filial.setUser("admin");
        session.saveOrUpdate(filial);
        session.flush();
        session.close();
        System.out.println(filial.getId());
        filial.setCode(""+filial.getId());
        session = sessionFactory.openSession();
        session.saveOrUpdate(filial);
        session.flush();
        session.close();
        return filial;
    }

    public EntityIndicators deleteIndicator(Map param) {
        int id = (int) param.get("id");
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityIndicators.class);
        criteria.add(Restrictions.eq("id", id));
        EntityIndicators filial  = (EntityIndicators) criteria.uniqueResult();
        if (filial!=null) {
            session.delete(filial);
        }
        session.flush();
        session.close();
        return filial;
    }

    public String getElementsGson(int sp_id){
        List elements = getElements(sp_id);
        Gson gson = new Gson();
        return gson.toJson(elements);
    }

    public List<EntityDimensionElement> getElements(int sp_id) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityDimensionElement.class);
        criteria.add(Restrictions.eq("spId", sp_id));
        List<EntityDimensionElement> elements = criteria.list();
        session.close();
        return elements;
    }
}
