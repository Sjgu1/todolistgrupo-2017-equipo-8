package models;

import com.google.inject.ImplementedBy;

//Interfaz que define los métodos del EtiquetaRepository
//La anotación ImplementedBy hace que Play para resolver la
//inyección de dependencias escoja como objeto que implementa
//esta interfaz un objeto JPAEtiquetaRepository
@ImplementedBy(JPAEtiquetaRepository.class)
public interface EtiquetaRepository{
  Etiqueta add(Etiqueta etiqueta);
  Etiqueta update(Etiqueta etiqueta);
  void delete(Long idEtiqueta);
  Etiqueta findById(Long idEtiqueta);
}
