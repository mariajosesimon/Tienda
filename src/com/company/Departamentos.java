package com.company;


import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="Departamentos")
public class Departamentos implements Serializable {

    int id;
    int dept_no;
    String dnombre;
    String loc;


    public Departamentos(int id, int dept_no, String dnombre, String loc) {
        this.id = id;
        this.dept_no = dept_no;
        this.dnombre = dnombre;
        this.loc = loc;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDept_no() {
        return dept_no;
    }

    public void setDept_no(int dept_no) {
        this.dept_no = dept_no;
    }

    public String getDnombre() {
        return dnombre;
    }

    public void setDnombre(String dnombre) {
        this.dnombre = dnombre;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
