package services;

import javax.inject.*;
import java.util.regex.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import play.Logger;

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
    Tablero tablero=new Tablero(usuario,titulo);
    return tableroRepository.add(tablero);
  }

}
