import org.junit.*;
import static org.junit.Assert.*;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import models.Tablero;

import services.TableroService;
import services.TableroServiceException;
import services.UsuarioService;

public class ServicioCrearListarTablerosTest{
  static private Injector injector;

  //Se ejecuta  sólo una vez, al princiio de todos los tests
  @BeforeClass
  static public void initApplication(){
    GuiceApplicationBuilder guiceApplicationBuilder =
      new GuiceApplicationBuilder().in(Environment.simple());
    injector=guiceApplicationBuilder.injector();
    //Instanciamos un JPAApi para que inicialice JPA
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

  private TableroService newTableroService(){
    return injector.instanceOf(TableroService.class);
  }

  //comprueba creación tablero correcta
  @Test
  public void crearNuevoTableroCorrectoTest(){
    long idUsuario=1000L;
    TableroService tableroService=newTableroService();
    Tablero tablero=tableroService.creaTablero(idUsuario,"tablero 1");
    assertNotNull(tablero.getId());
    assertEquals((long)idUsuario,(long)tablero.getAdministrador().getId());
    assertEquals("tablero 1",tablero.getNombre());
  }

  //comprueba excepción si usuario no existe para asignar como administrador
  @Test(expected=TableroServiceException.class)
  public void crearTableroUsuarioNoExisteCausaExcepcion(){
    long idUsuario=5000L;
    TableroService tableroService=newTableroService();
    Tablero tablero=tableroService.creaTablero(idUsuario,"tablero erróneo");
    }

  //comprueba listado tableros administrados por un usuario están ordenados
  //se da la peculiaridad en test que el tablero que inserto tiene un id inferior a los ya existentes
  @Test
  public void devuelveTablerosAdministradosUsuario(){
    long idUsuario=1000L;
    String tituloTablero="Tablero urgente";
    TableroService tableroService=newTableroService();
    List<Tablero> nombreTableros=tableroService.allTablerosAdministradosUsuario(idUsuario);
    assertEquals(2,nombreTableros.size());
    Tablero tablero=tableroService.creaTablero(idUsuario,tituloTablero);
    nombreTableros=tableroService.allTablerosAdministradosUsuario(idUsuario);
    assertEquals(3,nombreTableros.size());
    assertEquals(tituloTablero,nombreTableros.get(0).getNombre());
    assertEquals("Tablero test 1",nombreTableros.get(1).getNombre());
    assertEquals("Tablero test 2",nombreTableros.get(2).getNombre());
    }
}
