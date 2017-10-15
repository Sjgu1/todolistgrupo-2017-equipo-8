import org.junit.*;
import static org.junit.Assert.*;

import play.db.Database;
import play.db.Databases;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import play.db.jpa.JPAApi;

import java.util.Date;
import java.text.SimpleDateFormat;

import models.Usuario;
import models.Tarea;

import services.UsuarioService;
import services.TareaService;
import services.UsuarioServiceException;
import services.TareaServiceException;


public class Practica2Test{
  static private Injector injector;

  //Se ejecuta  sólo una vez, al princiio de todos los tests
  @BeforeClass
  static public void initDatabase(){
    GuiceApplicationBuilder guiceApplicationBuilder =
      new GuiceApplicationBuilder().in(Environment.simple());
    injector=guiceApplicationBuilder.injector();
    injector.instanceOf(JPAApi.class);
  }

  @Before
  public void initData() throws Exception{
    JndiDatabaseTester databaseTester=new JndiDatabaseTester("DBTest");
    IDataSet initialDataSet=new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
    databaseTester.setDataSet(initialDataSet);
    databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
    databaseTester.onSetup();
  }

  private UsuarioService newUsuarioService(){
    return injector.instanceOf(UsuarioService.class);
  }

  private TareaService newTareaService() {
    return injector.instanceOf(TareaService.class);
  }

  // Test #31: findUsuarioPorIdNoExisteNullTest
  @Test
  public void findUsuarioPorIdNoExisteNullTest(){
    UsuarioService usuarioService=newUsuarioService();
    Usuario usuario=usuarioService.findUsuarioPorId(5000L);
    assertNull(usuario);
  }

  // Test #32: borraTareanoExisteExcepcion
  @Test(expected=TareaServiceException.class)
  public void borraTareanoExisteExcepcion(){
    TareaService tareaService=newTareaService();
    long idTarea=5000L;
    tareaService.borraTarea(idTarea);
  }

  // Test #33: modificaTareanoExisteExcepcion
  @Test(expected=TareaServiceException.class)
  public void modificaTareanoExisteExcepcion(){
    TareaService tareaService=newTareaService();
    long idTarea=5000L;
    tareaService.modificaTarea(idTarea,"Descripción tarea");
  }

  // Test #34: exceptionSiUsuarioNoExisteNuevaTarea
  @Test(expected=TareaServiceException.class)
  public void nuevaTareaUsuarioNoExisteExcepcion(){
    TareaService tareaService=newTareaService();
    long idUsuario=5000L;
    tareaService.nuevaTarea(idUsuario,"Descripción tarea");
  }

}
