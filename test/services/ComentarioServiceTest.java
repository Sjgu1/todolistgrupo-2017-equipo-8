import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import models.Comentario;
import models.Tarea;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import play.Logger;

import services.ComentarioService;
import services.ComentarioServiceException;

import services.TareaService;

public class ComentarioServiceTest {
  static private Injector injector;

  @BeforeClass
  static public void initApplication() {
    GuiceApplicationBuilder guiceApplicationBuilder =
      new GuiceApplicationBuilder().in(Environment.simple());
    injector=guiceApplicationBuilder.injector();
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

  private ComentarioService newComentarioService() {
    return injector.instanceOf(ComentarioService.class);
  }
  private TareaService newTareaService() {
    return injector.instanceOf(TareaService.class);
  }

  @Test
  public void nuevaComentario(){
    ComentarioService comentarioService = newComentarioService();
    TareaService tareaService=newTareaService();

    String user="juangutierrez";
    Long idTarea = 1000L;
    Comentario comentario = comentarioService.crearComentario("Comentario de servicio", user, idTarea);

    assertEquals("Comentario de servicio",comentario.getComentario());
    assertEquals(user,comentario.getUsuario());
    assertEquals(idTarea,comentario.getTarea().getId());
  }

  @Test(expected=ComentarioServiceException.class)
  public void nuevaComentarioConNombreNoExiste(){
    ComentarioService comentarioService = newComentarioService();
    TareaService tareaService=newTareaService();

    String user="juangutierrezzzz";
    Long idTarea = 1000L;
    Comentario comentario = comentarioService.crearComentario("Comentario de servicio", user, idTarea);
  }

  @Test(expected=ComentarioServiceException.class)
  public void nuevaComentarioConTareaNoExiste(){
    ComentarioService comentarioService = newComentarioService();
    TareaService tareaService=newTareaService();

    String user="juangutierrez";
    Long idTarea = 10000L;
    Comentario comentario = comentarioService.crearComentario("Comentario de servicio", user, idTarea);
  }
  @Test
  public void nuevaComentarioConNull(){
    ComentarioService comentarioService = newComentarioService();
    TareaService tareaService=newTareaService();

    String user="juangutierrez";
    Long idTarea = 1000L;
    Comentario comentario = comentarioService.crearComentario(null, user, idTarea);

    assertEquals("",comentario.getComentario());
    assertEquals(user,comentario.getUsuario());
    assertEquals(idTarea,comentario.getTarea().getId());
  }

}
