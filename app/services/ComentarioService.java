package services;

import javax.inject.*;
import java.util.regex.*;

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


}
