package com.becafe.gclose.Model;

import java.time.LocalDate;
import java.util.Date;

public class Usuario {

    String nombre, apellido, sexo, interes, fecha_nac;

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String sexo, String interes, String fecha_nac) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.sexo = sexo;
        this.interes = interes;
        this.fecha_nac = fecha_nac;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getInteres() {
        return interes;
    }

    public void setInteres(String interes) {
        this.interes = interes;
    }

    public String getFecha_nac() {
        return fecha_nac;
    }

    public void setFecha_nac(String fecha_nac) {
        this.fecha_nac = fecha_nac;
    }


}
