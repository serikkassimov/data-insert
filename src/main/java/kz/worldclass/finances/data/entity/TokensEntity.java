package kz.worldclass.finances.data.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by serik on 16.06.16.
 */
@Entity
@Table(name = "tokens", schema = "worlddata", catalog = "")
public class TokensEntity {
    private int id;
    private String username;
    private String series;
    private String tokenValue;
    private Date tokenDate;


    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "series")
    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }




    @Column(name = "token_date")
    @Temporal(TemporalType.DATE)
    public Date getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(Date tokenDate) {
        this.tokenDate = tokenDate;
    }

    @Basic
    @Column(name = "token_value")
    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokensEntity that = (TokensEntity) o;

        if (id != that.id) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (series != null ? !series.equals(that.series) : that.series != null) return false;
        if (tokenValue != null ? !tokenValue.equals(that.tokenValue) : that.tokenValue != null) return false;
        if (tokenDate != null ? !tokenDate.equals(that.tokenDate) : that.tokenDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (series != null ? series.hashCode() : 0);
        result = 31 * result + (tokenValue != null ? tokenValue.hashCode() : 0);
        result = 31 * result + (tokenDate != null ? tokenDate.hashCode() : 0);
        return result;
    }
}
