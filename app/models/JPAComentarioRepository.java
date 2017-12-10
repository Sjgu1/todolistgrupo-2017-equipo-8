package models;

import javax.inject.Inject;
import play.db.jpa.JPAApi;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;

public class JPAComentarioRepository implements ComentarioRepository{
  JPAApi jpaApi;

  @Inject
  public JPAComentarioRepository(JPAApi api){
    this.jpaApi=api;
  }

  public Comentario add(Comentario comentario){
    return jpaApi.withTransaction(entityManager -> {
      entityManager.persist(comentario);
      entityManager.flush();
      entityManager.refresh(comentario);
      return comentario;
    });
  }

  public Comentario update(Comentario comentario){
    return jpaApi.withTransaction(entityManager -> {
      Comentario comentarioBD=entityManager.find(Comentario.class,comentario.getId());
      comentarioBD.setComentario(comentario.getComentario());
      return comentarioBD;
    });
  }

  public void delete(Long idComentario){
    jpaApi.withTransaction(() -> {
      EntityManager entityManager=jpaApi.em();
      Comentario comentarioBD=entityManager.getReference(Comentario.class,idComentario);
      System.out.println(comentarioBD.getComentario());
      entityManager.remove(comentarioBD);
    });
  }

  public Comentario findById(Long idComentario){
    return jpaApi.withTransaction(entityManager -> {
      return entityManager.find(Comentario.class,idComentario);
    });
  }

}
