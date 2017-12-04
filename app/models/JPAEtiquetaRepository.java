package models;

import javax.inject.Inject;
import play.db.jpa.JPAApi;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;

public class JPAEtiquetaRepository implements EtiquetaRepository{
  JPAApi jpaApi;

  @Inject
  public JPAEtiquetaRepository(JPAApi api){
    this.jpaApi=api;
  }

  public Etiqueta add(Etiqueta etiqueta){
    return jpaApi.withTransaction(entityManager -> {
      entityManager.persist(etiqueta);
      entityManager.flush();
      entityManager.refresh(etiqueta);
      return etiqueta;
    });
  }

  public Etiqueta update(Etiqueta etiqueta){
    return jpaApi.withTransaction(entityManager -> {
      Etiqueta etiquetaBD=entityManager.find(Etiqueta.class,etiqueta.getId());
      etiquetaBD.setNombre(etiqueta.getNombre());
      etiquetaBD.setColor(etiqueta.getColor());
      return etiquetaBD;
    });
  }

  public void delete(Long idEtiqueta){
    jpaApi.withTransaction(() -> {
      EntityManager entityManager=jpaApi.em();
      Etiqueta etiquetaBD=entityManager.getReference(Etiqueta.class,idEtiqueta);
      entityManager.remove(etiquetaBD);
    });
  }

  public Etiqueta findById(Long idEtiqueta){
    return jpaApi.withTransaction(entityManager -> {
      return entityManager.find(Etiqueta.class,idEtiqueta);
    });
  }
  
}
