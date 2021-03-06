package services;

import javax.inject.*;
import java.util.regex.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import models.Comentario;
import models.ComentarioRepository;

import models.Tarea;
import models.TareaRepository;

import models.Usuario;
import models.UsuarioRepository;

public class ComentarioService{
  ComentarioRepository comentarioRepository;
  UsuarioRepository usuarioRepository;
  TareaRepository tareaRepository;

  @Inject
  public ComentarioService(ComentarioRepository comentarioRepository, UsuarioRepository usuarioRepository,TareaRepository tareaRepository){
    this.comentarioRepository=comentarioRepository;
    this.usuarioRepository=usuarioRepository;
    this.tareaRepository=tareaRepository;
  }

  public Comentario crearComentario(String comentario, String user, Long idTarea){
    Usuario usuario= usuarioRepository.findByLogin(user);
    Tarea tarea = tareaRepository.findById(idTarea);
    if(usuario==null){
      throw new ComentarioServiceException("El usuario no existe");
    }
    if(tarea==null){
      throw new ComentarioServiceException("La tarea no existe");
    }
    if(comentario == null){
      comentario = "";
    }

    Comentario comentarioClass =new Comentario(comentario, user, tarea);
    return comentarioRepository.add(comentarioClass);

  }

  public Comentario obtenerComentario(Long idComentario){
    return comentarioRepository.findById(idComentario);
  }

  public List<Comentario> allComentariosTarea(Long idTarea){
    Tarea tarea = tareaRepository.findById(idTarea);
    if(tarea==null){
      throw new ComentarioServiceException("Tarea no existente");
    }
    List<Comentario> comentarios=new ArrayList<Comentario>(tarea.getComentarios());

    Collections.sort(comentarios,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return comentarios;
  }

  public Comentario modificaComentario(String nuevoComentario,  String user, Long idComentario){
    Comentario comentario=comentarioRepository.findById(idComentario);

    if(comentario==null)
      throw new ComentarioServiceException("No existe comentario");
    if (!comentario.getUsuario().equals(user))
      throw new ComentarioServiceException("No pertenece el comentario al usuario indicado");

    comentario.setComentario(nuevoComentario);

    comentario=comentarioRepository.update(comentario);
    return comentario;
  }

  public void borraComentario(Long idComentario){
    Comentario comentario=comentarioRepository.findById(idComentario);
    if(comentario==null)
      throw new ComentarioServiceException("No existe el comentario");
    comentarioRepository.delete(idComentario);
  }
}
