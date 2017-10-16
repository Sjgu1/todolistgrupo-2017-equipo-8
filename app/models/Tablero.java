package models;

import models.Usuario;

import javax.persistence.*;

@Entity
public class Tablero{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  Long id;
  private String nombre;
  @ManyToOne
  @JoinColumn(name="administradorId")
  private Usuario administrador;

  public Tablero() {}
  public Tablero(Usuario administrador,String nombre){
    this.administrador=administrador;
    this.nombre=nombre;
  }

  public Long getId(){
    return this.id;
  }

  public void setId(Long id){
    this.id=id;
  }

  public String getNombre(){
    return this.nombre;
  }

  public void setNombre(String nombre){
    this.nombre=nombre;
  }

  public Usuario getAdministrador(){
    return this.administrador;
  }

  public void setAdministrador(Usuario administrador){
    this.administrador=administrador;
  }

}
