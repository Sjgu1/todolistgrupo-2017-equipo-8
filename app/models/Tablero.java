package models;

import models.Usuario;

public class Tablero{
  private Usuario usuario;
  private String nombre;

  public Tablero(Usuario usuario,String nombre){
    this.usuario=usuario;
    this.nombre=nombre;
  }

  public Usuario getAdministrador(){
    return this.usuario;
  }

  public String getNombre(){
    return this.nombre;
  }

}
