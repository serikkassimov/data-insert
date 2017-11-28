package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.worldclass.finances.dao.impl.DictOrgDao;
import kz.worldclass.finances.dao.impl.UserDao;
import kz.worldclass.finances.dao.impl.XIncomingDao;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.dto.entity.XIncomingDto;
import kz.worldclass.finances.data.dto.results.xincoming.GetDataResult;
import kz.worldclass.finances.data.dto.results.xincoming.SaveDataResult;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import kz.worldclass.finances.data.entity.XIncomingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class XIncomingService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private DictOrgDao dictOrgDao;
    @Autowired
    private XIncomingDao xIncomingDao;
    
    public GetDataResult getData(Date date, String login) {
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return new GetDataResult(GetDataResult.Type.USER_NOT_FOUND);
        
        DictOrgEntity userOrgEntity = userEntity.getOrg();
        if (userOrgEntity == null) return new GetDataResult(GetDataResult.Type.ORG_NOT_FOUND);
        
        List<XIncomingDto> xIncomingDtos = new ArrayList<>();
        for (XIncomingEntity xIncomingEntity: xIncomingDao.fetchByOrgAndDate(userOrgEntity, date)) xIncomingDtos.add(Dtos.less(xIncomingEntity));
        return new GetDataResult(xIncomingDtos.toArray(new XIncomingDto[xIncomingDtos.size()]));
    }

    public GetDataResult getData(Date begdate, Date enddate, long org) {
        DictOrgEntity userOrgEntity = dictOrgDao.get(org);
        if (userOrgEntity == null) return new GetDataResult(GetDataResult.Type.ORG_NOT_FOUND);

        List<XIncomingDto> xIncomingDtos = new ArrayList<>();
        for (XIncomingEntity xIncomingEntity: xIncomingDao.fetchByOrgAndBetweenDate(userOrgEntity, begdate, enddate)) xIncomingDtos.add(Dtos.less(xIncomingEntity));
        return new GetDataResult(xIncomingDtos.toArray(new XIncomingDto[xIncomingDtos.size()]));
    }
    
    public SaveDataResult saveData(Date date, List<XIncomingDto> dtos, String login) {
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return SaveDataResult.USER_NOT_FOUND;
        
        DictOrgEntity userOrgEntity = userEntity.getOrg();
        if (userOrgEntity == null) return SaveDataResult.ORG_NOT_FOUND;
        
        Map<Long, XIncomingEntity> deletedMap = new HashMap<>();
        for (XIncomingEntity xIncomingEntity: xIncomingDao.fetchByOrgAndDate(userOrgEntity, date)) deletedMap.put(xIncomingEntity.getId(), xIncomingEntity);
        
        for (XIncomingDto xIncomingDto: dtos) {
            XIncomingEntity xIncomingEntity;
            if (xIncomingDto.id == null) {
                xIncomingEntity = new XIncomingEntity();
                xIncomingEntity.setDate(date);
                xIncomingEntity.setOrg(userOrgEntity);
            } else {
                xIncomingEntity = deletedMap.remove(xIncomingDto.id);
                if (xIncomingEntity == null) return SaveDataResult.DATA_NOT_FOUND;
            }
            xIncomingEntity.setNote(xIncomingDto.note);
            xIncomingEntity.setOrderNumber(xIncomingDto.orderNumber);
            xIncomingEntity.setCash(xIncomingDto.cash);
            xIncomingEntity.setValue(xIncomingDto.value);
            xIncomingDao.saveOrUpdate(xIncomingEntity);
        }
        
        for (XIncomingEntity xIncomingEntity: deletedMap.values()) xIncomingDao.delete(xIncomingEntity);
        
        return SaveDataResult.SUCCESS;
    }
}