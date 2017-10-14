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

import java.util.Date;
import java.text.SimpleDateFormat;

import models.Usuario;
import models.Tarea;
import models.UsuarioRepository;
import models.TareaRepository;
import models.JPAUsuarioRepository;
import models.JPATareaRepository;

import services.UsuarioService;
import services.TareaService;
import services.UsuarioServiceException;
import services.TareaServiceException;


public class Practica2Test{
  static Database db;
  static JPAApi jpaApi;

  //Se ejecuta  sólo una vez, al princiio de todos los tests
  @BeforeClass
  static public void initDatabase(){
    //Inicializamos la BD en memoria y su nombre JNDI
    db=Databases.inMemoryWith("jndiName","DBTest");
    db.getConnection();
    //Se activa la compatibilidad MySQL en la BD H2
    db.withConnection(connection -> {
      connection.createStatement().execute("SET MODE MySQL");
    });
    //Activamos en JPA la unidad de persistencia "memoryPersistenceUnit"
    //declarada en META-INF/persistence.xml y obtenemos el objeto
    //JPAApi
    jpaApi=JPA.createFor("memoryPersistenceUnit");
  }

  @Before
  public void initData() throws Exception{
    JndiDatabaseTester databaseTester=new JndiDatabaseTester("DBTest");
    IDataSet initialDataSet=new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
    databaseTester.setDataSet(initialDataSet);
    databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
    databaseTester.onSetup();
  }

  private TareaService newTareaService() {
    UsuarioRepository usuarioRepository = new JPAUsuarioRepository(jpaApi);
    TareaRepository tareaRepository = new JPATareaRepository(jpaApi);
    return new TareaService(usuarioRepository, tareaRepository);
  }

  // Test #31: findUsuarioPorIdNoExisteNullTest
  @Test
  public void findUsuarioPorIdNoExisteNullTest(){
    UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
    UsuarioService usuarioService=new UsuarioService(repository);
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
