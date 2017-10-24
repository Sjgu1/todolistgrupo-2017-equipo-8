package models;

import javax.inject.Inject;
import play.db.jpa.JPAApi;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;

public class JPATableroRepository implements TableroRepository {
   JPAApi jpaApi;

   @Inject
   public JPATableroRepository(JPAApi api) {
      this.jpaApi = api;
   }

   public Tablero add(Tablero tablero) {
     return jpaApi.withTransaction(entityManager -> {
       entityManager.persist(tablero);
       entityManager.flush();
       entityManager.refresh(tablero);
       return tablero;
     });
  }

  public Tablero update(Tablero tablero) {
    return jpaApi.withTransaction(entityManager -> {
      Tablero actualizado = entityManager.merge(tablero);
      return actualizado;
    });
  }

  public Tablero findById(Long idTablero) {
    return jpaApi.withTransaction(entityManager -> {
      return entityManager.find(Tablero.class, idTablero);
    });
  }

  public List<Tablero> allTableros(){
    return jpaApi.withTransaction(entityManager -> {
      TypedQuery<Tablero> query= entityManager.createQuery(
        "select t from Tablero t)",Tablero.class);

        try{
          List<Tablero> tableros=query.getResultList();
          return tableros;
        } catch(NoResultException ex){
          return null;
        }
    });
  }

  public Tablero findByNombre(String nombre){
    return jpaApi.withTransaction(entityManager -> {
      TypedQuery<Tablero> query= entityManager.createQuery(
        "select t from Tablero t where t.nombre= :nombre",Tablero.class);

        try{
          Tablero tablero=query.setParameter("nombre",nombre).getSingleResult();
          return tablero;
        } catch(NoResultException ex){
          return null;
        }
    });
  }
}
