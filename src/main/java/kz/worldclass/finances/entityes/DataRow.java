package kz.worldclass.finances.entityes;

import java.util.Date;

/**
 * Created by serik on 12.08.17.
 */
public class DataRow {
    int filial;
    Date date;
    Object[] datas;

    public int getFilial() {
        return filial;
    }

    public void setFilial(int filial) {
        this.filial = filial;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Object[] getDatas() {
        return datas;
    }

    public void setDatas(Object[] datas) {
        this.datas = datas;
    }
}
