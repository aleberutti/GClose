package com.becafe.gclose.Model;

public class Usuario {

    String nombre, apellido, sexo, interes, fecha_nac, descripcion, trabajo, localidad, educacion;

    public Usuario() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String desc) {
        this.descripcion = desc;
    }

    public String getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(String trabajo) {
        this.trabajo = trabajo;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getEducacion() {
        return educacion;
    }

    public void setEducacion(String educac) {
        this.educacion = educac;
    }

    public Usuario(String nombre, String apellido, String sexo, String interes, String fecha_nac, String desc, String trabajo, String localidad, String educacion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.sexo = sexo;
        this.interes = interes;
        this.fecha_nac = fecha_nac;
        this.descripcion = desc;
        this.trabajo = trabajo;
        this.localidad = localidad;
        this.educacion = educacion;
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
