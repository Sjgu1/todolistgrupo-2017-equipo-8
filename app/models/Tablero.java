package models;

import models.Usuario;
import models.Etiqueta;

import javax.persistence.*;

import java.util.Set;
import java.util.HashSet;

@Entity
public class Tablero{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  Long id;
  private String nombre;
  @ManyToOne
  @JoinColumn(name="administradorId")
  private Usuario administrador;
  @ManyToMany(fetch=FetchType.EAGER)
  @JoinTable(name="Persona_Tablero")
  private Set<Usuario> participantes = new HashSet<Usuario>();
  //relación uno a muchos, un tablero puede tener muchas etiquetas
  @OneToMany(fetch=FetchType.EAGER)
  @JoinTable(name="Etiqueta_Tablero")
  private Set<Etiqueta> etiquetas = new HashSet<Etiqueta>();

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

  public Set<Usuario> getParticipantes() {
    return participantes;
  }

  public void setParticipantes(Set<Usuario> participantes) {
    this.participantes = participantes;
  }

  public Set<Etiqueta> getEtiquetas() {
    return etiquetas;
  }

  public void setEtiquetas(Set<Etiqueta> etiquetas) {
    this.etiquetas = etiquetas;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = prime + ((nombre == null) ? 0 : nombre.hashCode());
    result = result + ((administrador == null) ? 0 : administrador.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (getClass() != obj.getClass()) return false;
    Tablero other = (Tablero) obj;
    // Si tenemos los ID, comparamos por ID
    if (id != null && other.id != null)
      return ((long) id == (long) other.id);
    // sino comparamos por campos obligatorios
    else {
      if (nombre == null) {
        if (other.nombre != null) return false;
      } else if (!nombre.equals(other.nombre)) return false;
      if (administrador == null) {
        if (other.administrador != null) return false;
        else if (!administrador.equals(other.administrador)) return false;
      }
    }
    return true;
  }
}
