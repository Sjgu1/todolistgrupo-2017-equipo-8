package models;

import javax.persistence.*;

import java.lang.IllegalArgumentException;

import java.util.Set;
import java.util.HashSet;

import java.awt.Color;

import play.Logger;
import play.data.format.*;

@Entity
public class Etiqueta{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  private String nombre;
  private String color;
  @ManyToOne
  @JoinColumn(name="tableroId")
  public Tablero tablero;
  @ManyToOne
  @JoinColumn(name="usuarioId")
  public Usuario usuario;
  @ManyToMany(mappedBy="etiquetas", fetch=FetchType.EAGER)
  private Set<Tarea> tareas = new HashSet<Tarea>();


  public Etiqueta() {}

  public Etiqueta(String color){
      if(compruebaColor(color)){
        this.color=color;
        this.nombre="";
      }
      else
        throw new IllegalArgumentException();
  }

  //Getters y setters necesarios para JPA

  public Long getId(){
    return id;
  }

  public void setId(Long id){
    this.id=id;
  }

  public String getNombre(){
    if(nombre.equals(null))
      return "";
    else
    return nombre;
  }

  public void setNombre(String nombre){
    this.nombre=nombre;
  }

  public String getColor(){
    return color;
  }

  public void setColor(String color){
    if(compruebaColor(color)){
      this.color=color;
    }
    else
      throw new IllegalArgumentException();
  }

  public Tablero getTablero(){
    return this.tablero;
  }

  public void setTablero(Tablero tablero){
    this.tablero=tablero;
  }

  public Usuario getUsuario(){
    return this.usuario;
  }

  public void setUsuario(Usuario usuario){
    this.usuario=usuario;
  }

  public Set<Tarea> getTareas(){
    return tareas;
  }

  public void setTareas(Set<Tarea> tareas){
    this.tareas=tareas;
  }

  public static Boolean compruebaColor(String color){
    Boolean valido=true;
      try{
      Color aux=Color.decode(color);
      return valido;
    }catch (IllegalArgumentException e){
      return false;
    }
  }

  public String toString(){
    return String.format("Etiqueta id: %s nombre: %s color:%s",
                    id,nombre,color);
  }

  @Override
  public int hashCode(){
    final int prime=31;
    int result=prime+((nombre==null)?0:nombre.hashCode())+((color==null)?0:color.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (getClass() != obj.getClass()) return false;
    Etiqueta other = (Etiqueta) obj;
    // Si tenemos los ID, comparamos por ID
    if (id != null && other.id != null)
      return ((long)id == (long)other.id);
    // sino comparamos por campos obligatorios
    else {
       if (nombre == null) {
          if (other.nombre != null) return false;
       } else if (!nombre.equals(other.nombre)) return false;
       if (color == null) {
        if (other.color != null) return false;
        else if (!color.equals(other.color)) return false;
        }
      }
      return true;
   }
}
