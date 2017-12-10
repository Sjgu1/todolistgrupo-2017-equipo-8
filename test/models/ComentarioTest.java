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


  @Test
  public void testCrearComentario(){
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");
    Comentario comentario = new Comentario("Comentario 1", usuario, tarea);

    assertEquals("juangutierrez", comentario.getUsuario().getLogin());
    assertEquals("juangutierrez@gmail.com", comentario.getUsuario().getEmail());
    assertEquals("Práctica 1 de MADS", comentario.getTarea().getTitulo());
    assertEquals("Comentario 1", comentario.getComentario());
  }

  @Test
  public void testFechasCreacionModificacionIguales() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Renovar DNI");
    Comentario comentario = new Comentario("Comentario 1", usuario, tarea);
    assertEquals(comentario.getFechaCreacion(), comentario.getFechaModificacion());
  }

  @Test
  public void testFechasCreacionModificacionDiferentes() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Renovar DNI");
    Comentario comentario = new Comentario("Comentario 1", usuario, tarea);
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
    Comentario comentario = new Comentario("Comentario 1", usuario, tarea);
    String comentarioInicial = comentario.getComentario();
    comentario.setComentario("Nuevo Comentario");
    assertNotEquals(comentarioInicial, comentario.getComentario());
  }
  @Test
  public void testComentariosNull() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Renovar DNI");
    Comentario comentario = new Comentario(null, usuario, tarea);
    assertEquals("", comentario.getComentario());
    comentario.setComentario("Nuevo Comentario");
    assertEquals("Nuevo Comentario", comentario.getComentario());
    comentario.setComentario(null);
    assertEquals("", comentario.getComentario());

  }

}
