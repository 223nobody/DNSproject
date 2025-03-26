package com.example.net.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName dns
 */
@Data
public class Dns implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private String url;


    /**
     * 
     */
    private String dnscode;

    private static final long serialVersionUID = 1L;
    public Integer getId(){
        return this.id;
    }

    /**
     *
     */
    public String getUrl(){
        return this.url;
    }

    /**
     *
     */
    public String getDnscode(){
        return this.dnscode;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDnscode(String dnscode) {
        this.dnscode = dnscode;
    }


    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Dns other = (Dns) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl()))
            && (this.getDnscode() == null ? other.getDnscode() == null : this.getDnscode().equals(other.getDnscode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result + ((getDnscode() == null) ? 0 : getDnscode().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", url=").append(url);
        sb.append(", dnscode=").append(dnscode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}