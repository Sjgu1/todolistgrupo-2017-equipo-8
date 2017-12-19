package models;

import javax.persistence.*;

import java.lang.IllegalArgumentException;

import java.util.Set;
import java.util.Locale;
import java.util.TimeZone;

import java.util.HashSet;

import play.Logger;
import play.data.format.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import models.Usuario;
import models.Tarea;

@Entity
public class Comentario{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  private Date fechaCreacion;
  private String comentario;
  private Date fechaModificacion;

  @ManyToOne
  @JoinColumn(name="tareaId")
  public Tarea tarea;

  public String usuario;

  public Comentario() {}

  public Comentario(String comentario, String usuario, Tarea tarea){
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date fecha = new Date();
    fecha.setHours(fecha.getHours() +1);
    this.fechaCreacion = fecha;
    this.fechaModificacion = fecha;
    if (comentario == null){
      this.comentario = "";
    }else{
      this.comentario = comentario;
    }
    this.usuario = usuario;
    this.tarea = tarea;

  }
  public Long getId(){
    return id;
  }

  public void setId(Long id){
    this.id=id;
  }

  public String getComentario(){
    if(comentario==null)
      return "";
    else
    return comentario;
  }

  public void setComentario(String comentario){
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date fecha = new Date();
    fecha.setHours(fecha.getHours() +1);
    this.fechaModificacion = fecha;
    this.comentario=comentario;
  }

  public Date getFechaCreacion(){
    return fechaCreacion;
  }

  public Date getFechaModificacion(){
    return fechaModificacion;
  }

  public Tarea getTarea(){
    return this.tarea;
  }

  public void setTarea(Tarea tarea){
    this.tarea=tarea;
  }

  public String getUsuario(){
    return this.usuario;
  }

  public void setUsuario(String usuario){
    this.usuario=usuario;
  }

  @Override
  public int hashCode(){
    final int prime=31;
    int result=prime+((fechaCreacion==null)?0:fechaCreacion.hashCode())+((comentario==null)?0:comentario.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (getClass() != obj.getClass()) return false;
    Comentario other = (Comentario) obj;
    // Si tenemos los ID, comparamos por ID
    if (id != null && other.id != null)
      return ((long)id == (long)other.id);
    // sino comparamos por campos obligatorios
    else {
       if (usuario == null) {
          if (other.usuario != null) return false;
       } else if (!usuario.equals(other.usuario)) return false;
       if (tarea == null) {
        if (other.tarea != null) return false;
        else if (!tarea.equals(other.tarea)) return false;
        }
        if (fechaCreacion == null) {
          if (other.fechaCreacion != null) return false;
          else if (!fechaCreacion.equals(other.fechaCreacion)) return false;
        }
        if (comentario == null) {
          if (other.comentario != null) return false;
          else if (!comentario.equals(other.comentario)) return false;
        }
      }
      return true;
   }
}
