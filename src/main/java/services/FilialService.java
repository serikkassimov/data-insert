package services;

import com.google.gson.Gson;
import entity.EntityFilials;
import entityes.Filial;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by serik on 11.08.17.
 */
@Service
public class FilialService {
    @Autowired
    SessionFactory sessionFactory;

    public String getAllFilials() {
        List<EntityFilials> filialList = loadFilials();
        Gson gson = new Gson();
        return gson.toJson(filialList);
    }

    private List<EntityFilials> loadFilials() {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityFilials.class);
        List<EntityFilials>  filialsList = criteria.list();
        session.close();
        return filialsList;
    }

    public EntityFilials addNewFilial(Map param) {
        int id = (int) param.get("id");
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityFilials.class);
        criteria.add(Restrictions.eq("id", id));
        EntityFilials filial  = (EntityFilials) criteria.uniqueResult();
        if (filial==null) {
            filial = new EntityFilials();
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

    public EntityFilials deleteFilial(Map param) {
        int id = (int) param.get("id");
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(EntityFilials.class);
        criteria.add(Restrictions.eq("id", id));
        EntityFilials filial  = (EntityFilials) criteria.uniqueResult();
        if (filial!=null) {
            session.delete(filial);
        }
        session.flush();
        session.close();
        return filial;
    }
}
