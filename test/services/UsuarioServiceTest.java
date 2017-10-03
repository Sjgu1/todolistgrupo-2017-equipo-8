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
import models.UsuarioRepository;
import models.JPAUsuarioRepository;

import services.UsuarioService;
import services.UsuarioServiceException;


public class UsuarioServiceTest{
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

  //Test 5: crearNuevoUsuarioCorrectoTest
  @Test
  public void crearNuevoUsuarioCorrectoTest(){
    UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
    UsuarioService usuarioService=new UsuarioService(repository);
    Usuario usuario=usuarioService.creaUsuario("luciaruiz","lucia.ruiz@gmail.com","123456");
    assertNotNull(usuario.getLogin());
    assertEquals("luciaruiz",usuario.getLogin());
    assertEquals("lucia.ruiz@gmail.com",usuario.getEmail());
    assertEquals("123456",usuario.getPassword());
  }

  //Test 6: crearNuevoUsuarioLoginRepetidoLanzaExcepcion
  @Test(expected = UsuarioServiceException.class)
  public void crearNuevoUsuarioLoginRepetidoLanzaExcepcion(){
    UsuarioRepository repository = new JPAUsuarioRepository(jpaApi);
    UsuarioService usuarioService = new UsuarioService(repository);
    // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
    Usuario usuario = usuarioService.creaUsuario("juangutierrez", "juan.gutierrez@gmail.com", "123456");
  }

  //Test 7: findUsuarioPorLogin
  @Test
  public void findUsuarioPorLogin() {
    UsuarioRepository repository = new JPAUsuarioRepository(jpaApi);
    UsuarioService usuarioService = new UsuarioService(repository);
    // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
    Usuario usuario = usuarioService.findUsuarioPorLogin("juangutierrez");
    assertNotNull(usuario);
    assertEquals((Long) 1000L, usuario.getId());
  }

  //Test 8: loginUsuarioExistenteTest
   @Test
   public void loginUsuarioExistenteTest() {
      UsuarioRepository repository = new JPAUsuarioRepository(jpaApi);
      UsuarioService usuarioService = new UsuarioService(repository);
      // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
    Usuario usuario = usuarioService.login("juangutierrez", "123456789");
      assertEquals((Long) 1000L, usuario.getId());
   }

   //Test 9: loginUsuarioNoExistenteTest
   @Test
   public void loginUsuarioNoExistenteTest() {
      UsuarioRepository repository = new JPAUsuarioRepository(jpaApi);
      UsuarioService usuarioService = new UsuarioService(repository);
      // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
      Usuario usuario = usuarioService.login("juan", "123456789");
      assertNull(usuario);
   }

   //Test 10: findUsuarioPorId
   @Test
   public void findUsuarioPorId(){
     UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
     UsuarioService usuarioService=new UsuarioService(repository);
     //En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
     Usuario usuario=usuarioService.findUsuarioPorId(1000L);
     assertNotNull(usuario);
     assertEquals("juangutierrez",usuario.getLogin());
   }

   //Test #24: creaUsuarioEmailIncorrecto lanza excepción
   @Test(expected = UsuarioServiceException.class)
   public void creaUsuarioEmailIncorrecto(){
     UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
     UsuarioService usuarioService=new UsuarioService(repository);
     Usuario usuario=usuarioService.creaUsuario("eustaquio","eustaquio","123456");
   }

   //Test #25: modificaUsuarioDiferentesCasos
   @Test
   public void modificaUsuarioDiferentesCasos(){
     try{
       SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
       Date fecha=sdf.parse("01-12-1980");
       UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
       UsuarioService usuarioService=new UsuarioService(repository);
       Usuario usuario=usuarioService.creaUsuario("roberto","roberto@gmail.com","123456");
       assertEquals("roberto",usuario.getLogin());
       assertEquals("roberto@gmail.com",usuario.getEmail());
       assertEquals("123456",usuario.getPassword());
       assertNull(usuario.getNombre());
       assertNull(usuario.getApellidos());
       assertNull(usuario.getFechaNacimiento());
       usuario=usuarioService.modificaUsuario("roberto","nuevo@gmail.com","654321","robert","Macia","01-12-1980");
       assertEquals("roberto",usuario.getLogin());
       assertEquals("nuevo@gmail.com",usuario.getEmail());
       assertEquals("654321",usuario.getPassword());
       assertEquals("robert",usuario.getNombre());
       assertEquals("Macia",usuario.getApellidos());
       assertEquals(fecha,usuario.getFechaNacimiento());
       usuario=usuarioService.modificaUsuario("roberto","nuevo@gmail.com","654321",null,null,null);
       assertEquals("roberto",usuario.getLogin());
       assertEquals("nuevo@gmail.com",usuario.getEmail());
       assertEquals("654321",usuario.getPassword());
       assertEquals("robert",usuario.getNombre());
       assertEquals("Macia",usuario.getApellidos());
       assertEquals(fecha,usuario.getFechaNacimiento());
     } catch (Exception e) {
       e.printStackTrace();
     }
   }

   //Test #26: modificaContrasenaUsuario
   @Test
   public void modificaContrasenaUsuario(){
     UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
     UsuarioService usuarioService=new UsuarioService(repository);
     Usuario usuario=usuarioService.findUsuarioPorId(1000L);
     usuario=usuarioService.modificaPassword(usuario.getLogin(),"123456789","654321");
     assertEquals("654321",usuario.getPassword());
   }

    //Test #27: modificaContrasenaActualIncorrecta lanza excepción
    @Test(expected = UsuarioServiceException.class)
    public void modificaContrasenaActualIncorrecta(){
      UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
      UsuarioService usuarioService=new UsuarioService(repository);
      Usuario usuario=usuarioService.findUsuarioPorId(1000L);
      usuario=usuarioService.modificaPassword(usuario.getLogin(),"12345","654321");
    }

    //Test #28: modificaContrasenaNuevaIgual lanza excepción
    @Test(expected = UsuarioServiceException.class)
    public void modificaContrasenaNuevaIgual(){
      UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
      UsuarioService usuarioService=new UsuarioService(repository);
      Usuario usuario=usuarioService.findUsuarioPorId(1000L);
      usuario=usuarioService.modificaPassword(usuario.getLogin(),"123456789","123456789");
    }

    //Test #29: modificaContrasenaNuevaVacia lanza excepción
    @Test(expected = UsuarioServiceException.class)
    public void modificaContrasenaNuevaVacia(){
      UsuarioRepository repository=new JPAUsuarioRepository(jpaApi);
      UsuarioService usuarioService=new UsuarioService(repository);
      Usuario usuario=usuarioService.findUsuarioPorId(1000L);
      usuario=usuarioService.modificaPassword(usuario.getLogin(),"123456789","");
    }

}
