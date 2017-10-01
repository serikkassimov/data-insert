package kz.worldclass.finances.data.entity;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by serik on 13.08.17.
 */
@Entity
@Table(name = "uni_datas", schema = "worlddata", catalog = "")
public class EntityDatas {
    private int id;
    private Date date;
    private int filial;
    private int dim1;
    private int dimv1;
    private int dim2;
    private int dimv2;
    private int dim3;
    private int dimv3;
    private int dim4;
    private int dimv4;
    private int dim5;
    private int dimv5;
    private double value;
    private String note;
    private java.util.Date updateTime;
    private String user;

    @Id
    @Column(name = "id")
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Basic
    @Column(name = "filial")
    public int getFilial() {
        return filial;
    }

    public void setFilial(int filial) {
        this.filial = filial;
    }

    @Basic
    @Column(name = "dim1")
    public int getDim1() {
        return dim1;
    }

    public void setDim1(int indicator) {
        this.dim1 = indicator;
    }

    @Basic
    @Column(name = "dim3")
    public int getDim3() {
        return dim3;
    }

    public void setDim3(int indicator) {
        this.dim3 = indicator;
    }

    @Basic
    @Column(name = "dim2")
    public int getDim2() {
        return dim2;
    }

    public void setDim2(int indicator) {
        this.dim2 = indicator;
    }

    @Basic
    @Column(name = "dim5")
    public int getDim5() {
        return dim5;
    }

    public void setDim5(int indicator) {
        this.dim5 = indicator;
    }

    @Basic
    @Column(name = "dim4")
    public int getDim4() {
        return dim4;
    }

    public void setDim4(int indicator) {
        this.dim4 = indicator;
    }

    @Basic
    @Column(name = "dimv1")
    public int getDimv1() {
        return dimv1;
    }

    public void setDimv1(int indicator) {
        this.dimv1 = indicator;
    }

    @Basic
    @Column(name = "dimv2")
    public int getDimv2() {
        return dimv2;
    }

    public void setDimv2(int indicator) {
        this.dimv2 = indicator;
    }

    @Basic
    @Column(name = "dimv3")
    public int getDimv3() {
        return dimv3;
    }

    public void setDimv3(int indicator) {
        this.dimv3 = indicator;
    }

    @Basic
    @Column(name = "dimv4")
    public int getDimv4() {
        return dimv4;
    }

    public void setDimv4(int indicator) {
        this.dimv4 = indicator;
    }

    @Basic
    @Column(name = "dimv5")
    public int getDimv5() {
        return dimv5;
    }

    public void setDimv5(int indicator) {
        this.dimv5 = indicator;
    }

    @Basic
    @Column(name = "value")
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Basic
    @Column(name = "note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Basic
    @Column(name = "update_time", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    public java.util.Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(java.util.Date updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityDatas that = (EntityDatas) o;

        if (id != that.id) return false;
        if (filial != that.filial) return false;
        if (dim1 != that.dim1) return false;
        if (dim2 != that.dim2) return false;
        if (dim3 != that.dim3) return false;
        if (dim4 != that.dim4) return false;
        if (dim5 != that.dim5) return false;
        if (dimv1 != that.dimv1) return false;
        if (dimv2 != that.dimv2) return false;
        if (dimv3 != that.dimv3) return false;
        if (dimv4 != that.dimv4) return false;
        if (dimv5 != that.dimv5) return false;
        if (value != that.value) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;
        if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + filial;
        result = 31 * result + dim1;
        result = 31 * result + dim2;
        result = 31 * result + dim3;
        result = 31 * result + dim4;
        result = 31 * result + dim5;
        result = 31 * result + dimv1;
        result = 31 * result + dimv2;
        result = 31 * result + dimv3;
        result = 31 * result + dimv4;
        result = 31 * result + dimv5;
        result = (int) (31 * result + Math.round(value))        ;
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EntityDatas{" +
                "id=" + id +
                ", date=" + date +
                ", filial=" + filial +
                ", dim1=" + dim1 +
                ", dim2=" + dim2 +
                ", dim3=" + dim3 +
                ", dim4=" + dim4 +
                ", dim5=" + dim5 +
                ", dimv1=" + dimv1 +
                ", dimv2=" + dimv2 +
                ", dimv3=" + dimv3 +
                ", dimv4=" + dimv4 +
                ", dimv5=" + dimv5 +
                ", value=" + value +
                ", note='" + note + '\'' +
                ", updateTime=" + updateTime +
                ", user='" + user + '\'' +
                '}';
    }
}
