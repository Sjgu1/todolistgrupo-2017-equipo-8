package services;

import javax.inject.*;
import java.util.regex.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import play.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import models.Usuario;
import models.UsuarioRepository;

import models.Tablero;
import models.TableroRepository;

public class TableroService{
  UsuarioRepository usuarioRepository;
  TableroRepository tableroRepository;

  @Inject
  public TableroService(UsuarioRepository usurepository,TableroRepository tabrepository){
    this.usuarioRepository=usurepository;
    this.tableroRepository=tabrepository;
  }

  public Tablero creaTablero(long idUsuario, String titulo){
    Usuario usuario= usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TableroServiceException("Usuario no existente");
    }
    Tablero tablero=new Tablero(usuario,titulo);
    return tableroRepository.add(tablero);
  }

  //Devuelve la lista de tableros administrados por un usuario, ordenadas por su id
  //(equivalente al orden de creación)
  public List<Tablero> allTablerosAdministradosUsuario(Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    List<Tablero> tableros=new ArrayList<Tablero>(usuario.getAdministrados());
    Collections.sort(tableros,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return tableros;
  }


}
