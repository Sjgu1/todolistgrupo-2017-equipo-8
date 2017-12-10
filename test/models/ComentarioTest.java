import org.junit.*;
import static org.junit.Assert.*;

import play.db.Database;
import play.db.Databases;
import play.db.jpa.*;

import java.lang.IllegalArgumentException;

import play.Logger;

import java.sql.*;

import java.util.concurrent.TimeUnit;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import models.Comentario;
import models.Usuario;
import models.Tarea;
import models.TareaRepository;

import models.ComentarioRepository;
import models.JPAComentarioRepository;
import models.ComentarioRepositoryException;

public class ComentarioTest {

  static Database db;
  static private Injector injector;

  //Se ejecuta sólo una vez, al principio de todos los tests
  @BeforeClass
  static public void initApplication(){
    GuiceApplicationBuilder guiceApplicationBuilder =
      new GuiceApplicationBuilder().in(Environment.simple());
    injector=guiceApplicationBuilder.injector();
    db=injector.instanceOf(Database.class);
    //Necesario para inicializar JPA
    injector.instanceOf(JPAApi.class);
  }

  @Before
  public void initData() throws Exception {
    JndiDatabaseTester databaseTester = new JndiDatabaseTester("DBTest");
    IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
    databaseTester.setDataSet(initialDataSet);
    databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
    databaseTester.onSetup();
  }

  private ComentarioRepository newComentarioRepository(){
    return injector.instanceOf(ComentarioRepository.class);
  }
  private TareaRepository newTareaRepository(){
    return injector.instanceOf(TareaRepository.class);
  }


  @Test
  public void testCrearComentario(){
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");
    Comentario comentario = new Comentario("Comentario 1", usuario.getLogin(), tarea);

    assertEquals("juangutierrez", comentario.getUsuario());
    assertEquals("Práctica 1 de MADS", comentario.getTarea().getTitulo());
    assertEquals("Comentario 1", comentario.getComentario());
  }

  @Test
  public void testFechasCreacionModificacionIguales() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Renovar DNI");
    Comentario comentario = new Comentario("Comentario 1", usuario.getLogin(), tarea);
    assertEquals(comentario.getFechaCreacion(), comentario.getFechaModificacion());
  }

  @Test
  public void testFechasCreacionModificacionDiferentes() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Renovar DNI");
    Comentario comentario = new Comentario("Comentario 1", usuario.getLogin(), tarea);
    try{
        Thread.sleep(1000);
    }catch(InterruptedException ex){
        Thread.currentThread().interrupt();
    }
    comentario.setComentario("Comentario cambiado");
    assertNotEquals(comentario.getFechaCreacion().toString(), comentario.getFechaModificacion().toString());
    assertEquals(comentario.getFechaCreacion().toString(), comentario.getFechaCreacion().toString());
  }

  @Test
  public void testModificarComentario() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Renovar DNI");
    Comentario comentario = new Comentario("Comentario 1", usuario.getLogin(), tarea);
    String comentarioInicial = comentario.getComentario();
    comentario.setComentario("Nuevo Comentario");
    assertNotEquals(comentarioInicial, comentario.getComentario());
  }
  @Test
  public void testComentariosNull() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Renovar DNI");
    Comentario comentario = new Comentario(null, usuario.getLogin(), tarea);
    assertEquals("", comentario.getComentario());
    comentario.setComentario("Nuevo Comentario");
    assertEquals("Nuevo Comentario", comentario.getComentario());
    comentario.setComentario(null);
    assertEquals("", comentario.getComentario());

  }

  @Test
  public void testCrearComentarioRepository(){
    String coment ="Comentario";
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    // Obtenemos datos del dataset
    TareaRepository tareaRepository=newTareaRepository();
    Tarea tarea = tareaRepository.findById(1000L);

    Comentario comentario=new Comentario(coment, usuario.getLogin(), tarea);
    ComentarioRepository comentarioRepository = newComentarioRepository();
    Comentario resul = comentarioRepository.add(comentario);
    assertEquals(coment,resul.getComentario());
  }

  @Test
  public void testModificarComentarioRepository(){
    String comentario1 = "Comentario 1";
    String comentario2 = "Comentario 1";
      Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");

    TareaRepository tareaRepository=newTareaRepository();
    Tarea tarea = tareaRepository.findById(1000L);

    Comentario comentario=new Comentario(comentario1, usuario.getLogin(), tarea);
    ComentarioRepository comentarioRepository = newComentarioRepository();
    comentario = comentarioRepository.add(comentario);
    assertEquals(comentario1,comentario.getComentario());
    comentario.setComentario(comentario2);
    comentario=comentarioRepository.update(comentario);
    assertEquals(comentario2,comentario.getComentario());
  }
  @Test
  public void testRecuperarComentarioPorIdRepository(){
    String coment ="Comentario";
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    // Obtenemos datos del dataset
    TareaRepository tareaRepository=newTareaRepository();
    Tarea tarea = tareaRepository.findById(1000L);

    Comentario comentario=new Comentario(coment, usuario.getLogin(), tarea);
    ComentarioRepository comentarioRepository = newComentarioRepository();
    Comentario resul = comentarioRepository.add(comentario);

    Comentario recuperar = comentarioRepository.findById(resul.getId());
    assertEquals(recuperar,resul);
  }

  @Test
  public void testRecuperarComentarioPorIdNoExisteRepository(){
    ComentarioRepository comentarioRepository=newComentarioRepository();
    Comentario comentario = comentarioRepository.findById(10000L);
    assertNull(comentario);
  }


  @Test
  public void testEliminarComentarioPorIdRepository(){
    String coment ="Comentario";
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    // Obtenemos datos del dataset
    TareaRepository tareaRepository=newTareaRepository();
    Tarea tarea = tareaRepository.findById(1000L);

    Comentario comentario=new Comentario(coment, usuario.getLogin(), tarea);
    ComentarioRepository comentarioRepository = newComentarioRepository();
    Comentario resul = comentarioRepository.add(comentario);
    Long id = resul.getId();

    comentarioRepository.delete(id);

    Comentario recuperar = comentarioRepository.findById(id);
    assertNull(recuperar);
  }
}
