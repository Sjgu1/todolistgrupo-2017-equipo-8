package models;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(JPAComentarioRepository.class)
public interface ComentarioRepository{
  Comentario add(Comentario comentario);
  Comentario update(Comentario comentario);
  void delete(Long idComentario);
  Comentario findById(Long idComentario);
}
