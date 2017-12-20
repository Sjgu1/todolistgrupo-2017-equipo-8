# Documentación práctica 4
## Historias de usuario

### [SGT-4 Etiquetado de tareas](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-4-Etiquetado-de-tareas)

#### Descripción

Como usuario quiero poder `etiquetar` mis tareas y las tareas de los tableros en los que participo.

#### COS - Condiciones de Satisfacción

* Las etiquetas dispondrán de un `color` asignado y un `texto` opcional, se debe poder realizar el CRUD de las mismas.
* En la modificación de una tarea, debo poder establecer `etiquetas` a modo de clasificación.
* Las `etiquetas` disponibles deben pertenecer al usuario si estamos en "Mis tareas" o al tablero concreto en el que estemos realizando la asignación.
* Se deben poder crear tantas `etiquetas` como se necesiten.
* En el tablero cualquier usuario (administrador o participante) debe poder mantener las etiquetas y asignar-desasignarlas en las diferentes tareas.

### [SGT-5 Descripción y selector de fecha en tareas](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-5-Descripci%C3%B3n-y-selector-de-fecha-en-tareas)
#### Descripción
Las tareas deben de tener una `descripción`, a parte de el `título` y la `fecha límite`, para poder tener más información sobre las tareas.
Además, el selector de `fecha límite` ha de ser un calendario desplegable, para facilitar el uso de este campo.
#### COS - Condiciones de Satisfacción

**Descripción**
Las tareas deben contar con un nuevo campo que se podrá editar tras la creación de la tarea:

* `Descripción` : Texto informativo sobre la tarea.

**Calendario**
El campo `fecha límite` será un calendario desplegable en el que:

* Al pulsar sobre él se abrirá y al pulsar una fecha aparecerá reflejada en el campo correspondiente.
* No se puede seleccionar una fecha anterior a la actual.
* Tendrá un formato de `dd-MM-yyyy`

### [SGT-6 Comentarios en tareas](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-6-Comentarios-en-tareas)
#### Descripción
Las tareas deben permitir ser agregar comentarios por parte de los usuarios para poder informar sobre el estado de la tarea, o permitir un debate sobre la misma. Si el usuario ha escrito el comentario, lo puede editar o eliminar.
#### COS - Condiciones de Satisfacción

**Comentarios en tareas**
Las tareas pueden tener comentarios hechos por los usuarios. En cada comentario aparecerá la siguiente información:

* `Usuario` que ha realizado el comentario.
* `Fecha de creación` del comentario, generada automáticamente.
* `Comentario` tipo texto.

Los comentarios escritos por un usuario, al visualizarlos deben mostrar y permitir las siguientes opciones:

* Eliminar el comentario en la tarea si eres el usuario que lo ha escrito.

### [SGT-7 Reactivación, finalización y borrado de tareas](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-7-Reactivaci%C3%B3n,-finalizaci%C3%B3n-y-borrado-de-tareas)

#### Descripción

* Las tareas ya no pueden ser eliminadas desde las tareas pendientes, únicamente se pueden finalizar con el fin de evitar borrados accidentales.
* Las tareas finalizadas pueden volver a pendientes, para poder reutilizarlas, o modificar sus datos.
* Las tareas finalizadas pueden ser eliminadas previo mensaje de confirmación.

#### COS - Condiciones de Satisfacción

**Tareas**
Las tareas pendientes pueden ser:

* `Consultadas` para ver su información, comentarios y lo habitual de las tareas.
* `Finalizadas` pasando a la pestaña correspondiente.

Las tareas finalizadas pueden ser:
* `Reactivadas` pasando de nuevo a estar pendiente a través de un icono en la tarea.
* `Eliminadas` dejando de estar accesibles a través de un icono en la tarea. Para eliminarse:
    * Se ha de pulsar el icono correspondiente desde el que se mostrará un mensaje de confirmación.
    * Se ha de confirmar la eliminación de la tarea, pasando a no estar disponible.

### [SGT-8 Filtrado de tareas](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-8-Filtrado-de-tareas)

#### Descripción

Como usuario quiero poder filtrar las tareas por las `etiquetas` asignadas a las mismas y por el texto que pueda disponer en el título o descripción, así como por el `usuario` responsable de la misma con el objetivo de poder gestionarlas mejor.

#### COS - Condiciones de Satisfacción

* Debe existir una forma sencilla de filtrar tareas por `etiquetas` tanto en "Mis tareas" como en la visualización de un tablero concreto.
* Debe existir una forma sencilla de filtrar tareas por texto contenido en el `título` o `descripción` de las tareas, tanto en "Mis tareas" como en la visualización de un tablero concreto.
* Debe existir una forma sencilla de filtrar tareas por usuario responsable de las tareas, tanto en "Mis tareas" como en la visualización de un tablero concreto.
* Se deben poder combinar todos los filtros.

### [SGT-9 Asignar tareas a tableros](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-9-Agregar-tareas-a-tableros)

#### Descripción
Permitir que el usuario tenga la posibilidad de poder crear tareas dentro de cada tablero.

#### COS - Condiciones de satisfacción

- El usuario dentro de cada tablero podrá crear tareas que solo estarán disponibles dentro de él.
- Dentro de cada tablero el usuario, ya sea administrador o participante, podrá añadir una o varias tareas haciendo click sobre el icono `+`.
- El apartado de `misTareas` seguirá mostrando las tareas de propias de cada usuario que no tienen relación con ningún tablero.

### [SGT-10 Asignar usuarios a tareas](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-10-Asignar-usuarios-a-tareas)

#### Descripción
Añadir la funcionalidad de que un usuario pueda ponerse como responsable de la tarea.

#### COS - Condiciones de satisfacción

- Un usuario podrá asignarse como responsable a una tarea independientemente del usuario que la haya creado.
- El usuario solo podrá asignarse a aquellas tareas que estén dentro de un tablero en el que participa o es administrador, ya que las tareas que están dentro del apartado `misTareas` son las tareas personales de cada usuario.
- Dentro de cada tarea habrá una nueva acción que permita asignarse como responsable de dicha tarea.

## Funcionalidades implementadas.
### Etiquetado de tareas

#### USUARIO.
> Las tareas pueden tener una o más etiquetas asignadas. Se pueden crear etiquetas en `Mis tareas` o `Mis tableros`. Las etiquetas de cada uno de estos apartados estarán disponibles para las tareas que pertenezcan a este apartado (una etiqueta creada en un tablero solo estará disponible para las tareas del tablero). Se pueden agregar etiquetas desde el apartado de modificación de etiquetas.

> Las etiquetas pueden ser modificadas en cualquier momento pero dicho cambio afectará a su relación con las tareas, si a una tarea le cambiamos el color, dicho cambio será repercutido en todas las tareas en las que esté asignada esa etiqueta. Se mostrarán las etiquetas de las tareas a la que acompañan tanto en las lista de tareas, como en el panel de modificar tarea. Al eliminar una etiqueta, desaparecerá de todos las tareas a los que estaba asignada.

> La asignación de etiquetas a una tarea, se realiza desde la modificación de la tarea, para las existentes existirá una "X" dentro de la etiqueta asignada para poder borrarla de la tarea y dispondremos de una selección múltiple con la que poder seleccionar varias etiquetas para añadirlas de una sola vez a la tarea.

#### DESARROLLADOR.
> Se crea un nuevo modelo de etiquetas que para mayor flexibilidad puede funcionar por el mísmo, no depende de ninguna otra entidad. Hay una posible relación uno a muchos con usuarios y tableros además de una muchos a muchos con tareas. La idea es que una etiqueta solo puede estar en un usuario o en un tablero determinado, pero que se pueda relacionar con muchas tareas (de ese usuario o tablero).

> Además del modelo, repository, servicio y controlador de la entidad etiquetas, se han tenido que realizar métodos de servicio y controlador en usuarios, tableros y tareas para gestionar toda esa relación. Por último, se han modificado las vistas de `Mis tareas` y `Mis tableros` para poder disponer de las operaciones de CRUD de etiquetas en un menú opciones y se puede ver una miniatura de las etiquetas asignadas a una tarea en el listado de tareas disponibles (en el caso de tableros, esta opción estaría en el `detalle tablero`), así como de `Modificación tareas` desde donde se pueden ver las etiquetas asignadas y se pueden desasignar desde ahí con un click, así como poder asignar de una sola vez varias etiquetas a la tarea).

> Para realizar más atractiva la asignación de etiquetas a una tarea, se ha jugado con el componente Modal, con el que se puede ver una determinada funcionalidad en primer plano, dejando en segundo plano la página de la que proviene. La asignación de etiquetas a la tarea se hace mediante AJAX en un bucle que va asignando las etiquetas una a una.

### Descripción y selector de fecha en tareas
#### USUARIO.
> Se puede agregar una descripción a las tareas, de esta manera se puede ampliar la información referente a la tarea, siendo limitada con el título y la fecha. Esta información está disponible en los detalles de la tarea.

> Además, las fechas ahora se seleccionan con un calendario desplegable, facilitando el uso de este campo.

#### DESARROLLADOR.
> Las tareas tienes un nuevo atributo del tipo `String`. En la parte de las vistas hay un `textarea` para facilitar la entrada de texto.

> Se hace uso del calendario de `Jquery`, por lo que ha sido agregado al proyecto. Se hace uso de javascript para no permitir fechas anteriores al día actual.
### Comentarios en tareas
#### USUARIO.
> Las tareas que pertenecen a los tableros tiene la opción de dejar comentarios por parte de los usuarios. Estos comentarios tiene el fin de dar información, o crear opinión sobre el estado de una tarea. En ellos, aparece reflejado el usuario y el momento en el que se creó el comentario. Se pueden eliminar los comentarios siempre y cuando el usuario que lo elimina sea el mismo que lo escribió.
> Al eliminar una tarea, los comentarios se borrarán también.


#### DESARROLLADOR.
> Los comentarios son un nuevo modelo de la aplicación, con todas sus clases y métodos correspondientes. La posibilidad de agregar comentarios en las tareas de los `tableros` solo se diferencia de `mis tareas` a nivel visual, no son distintos tipos de tareas.

> Eliminar una tarea hace que se eliminen todos los comentarios que tenga la tarea antes de proceder a eliminar la misma.  

### Reactivación, finalización y borrado de tareas
#### USUARIO.
> Las tareas ahora no se pueden borrar si no se han finalizado. De esta forma se evitan borrados accidentales. Del mismo modo, las tareas finalizadas se pueden reactivar, en caso de querer reutilizarla. Al eliminar una tarea, se nos mostrará un mensaje de confirmación antes de borrarla.

#### DESARROLLADOR.
> Los cambios son a nivel visual, además de incluir el método que reactive la tarea.

### Filtrado de tareas
#### USUARIO.
> Se dispone de una opción por la que poder realizar filtrado de las tareas de un usuario o de un tablero, según las etiquetas que tienen asignadas. Esa opción de filtrado, se encuentra en el apartado opciones tanto de la página en la que veo `Mis Tareas` como en el `detalle de un Tablero`, en ambos casos se situa justo debajo del apartado de creación de etiquetas.

>Para usarlo, solo se deben seleccionar `etiquetas` que se quieren filtrar y pulsar el botón `Filtrar`, en cualquier momento, se puede volver a la vista principal antes de filtrado, pulsando el botón `Cancelar filtrado`.

#### DESARROLLADOR.
> Se ha realizado un método en el servicio de tareas que es capaz de devolver un conjunto de tareas filtradas a partir de un conjunto de etiquetas, en este método nos apoyamos desde el controlador de usuarios como en el del tablero para poder llamar en ambos casos a una nueva vista que carga el resto de funcionalidades (`listaTareasFiltradas` y `detalleTableroFiltrado` respectivamente), solo que con las tareas filtradas según las etiquetas que le llegan.

> Ha sido un reto conseguir realizar esta operación, intentamos realizarlo pasando un array de elementos desde el formulario de la vista, pero no se conseguía, tras ello probamos a realizarlo con una llamada AJAX, pero hay limitaciones al respecto, aunque se llamaba a todo, no se llegaba a refrescar la pantalla. Finalmente, hemos usado un campo hidden en el que hemos ido acumulando las etiquetas seleccionadas, y haciendo split de ese campo sí hemos podido realizar la operación.

### Asignar tareas a tableros
#### USUARIO.
> Los tableros pueden tener tareas, con todas sus funcionalidades y algunas extra (comentarios). Están disponibles a modos de listas en el tablero correspondiente. Se pueden crear, borrar, modificar, eliminar, consultar, comentar, finalizar y reactivar.

#### DESARROLLADOR.
> A nivel técnico implica diferenciar las tareas que pertenecen a un tablero y las que son personales del usuario, es decir, las que aparecen en el apartado `misTareas`. Cuando se crea una tarea dentro de un tablero, primero se crea la tarea con sus parámetros y luego se le asigna al tablero.

### Asignar usuarios a tareas
#### USUARIO.
> Las tareas pueden tener un responsable. Se puede asignar solo un responsable por tarea o dejarla sin responsable. El usuario puede elegir en el desplegable el responsable que quiere asignar a una tarea.

#### DESARROLLADOR.
> Cualquier usuario que sea administrador o participante de un tablero, puede asignar responsables a tareas del tablero. En el despegable para asignar responsable aparecen el administrador y los participantes del tablero, y si hay alguien asignado, la opción para borrar el responsable. No es necesario borrar el responsable para cambia a otro, se puede elegir directamente otro responsable y se asigna el nuevo automáticamente.

## Informe sobre la metodología

### Evolución del tablero.

A lo largo del sprint se han tenido dos reuniones a modo de reuniones diarias. En ellas se ha comprobado el estado del tablero, viendo lo que se ha desarrollado hasta la fecha y comparándolo con cómo debería ir según la planificación. Esto se verá más adelante con los documentos de las reuniones, pero esta ha sido la evolución de los tableros a lo largo del desarrollo:

#### Primera semana de desarrollo
En la primera semana vemos que hay una tarea terminada. Esta tarea es de tamaño pequeño, por lo que su evolución ha sido la deseada. El resto de tareas tienen un tamaño mediano, así que para este momento están en desarrollo como se puede apreciar.
![enter image description here](https://i.imgur.com/Gy2CwxT.png)

Este sería el tablero de github correspondiente.
![enter image description here](https://i.imgur.com/2eknafA.png)

Se puede ver como las tareas etiquetadas como SGT-5 están completamente terminadas, y el resto de historias de usuario que aparecen en la columna `En marcha` de Trello, tienen sus correspondientes issues en alguna de las 5 primeras columnas de GitHub.

#### Tercera semana de desarrollo.

Para esta tercera semana se esperaban terminadas todas las historias de usuario que se encontraban en `En marcha` en Trello. En la reunion de sprint se verá que esta semana hubo algún problema, pero a pesar de ello el tablero evolucionó de la siguiente manera.
![enter image description here](https://i.imgur.com/CjaaNli.png)

Con su correspondiente tablero de GitHub actualizado, con los nuevos issues pendientes que pertenecen a las historias de usuario en marcha.
![enter image description here](https://i.imgur.com/CjaaNli.png)


En el siguiente gráfico se puede ver en puntos historia lo que estaba planeado tener para cada semana de desarrollo y lo que hemos conseguido completar. Como hay diferencia, queda claro que hay algún problema en la planificación, por lo que esto será analizado en la reunión de restrospectiva y ya está reflejado en las reuniones diarias.  
![enter image description here](https://i.imgur.com/7IFb8Oa.png)

Por otro lado, se ha calculado una gráfica Burndown a partir de los pull request realizados que básicamente lo que indican es la finalización de una issue. No es una gráfica exacta, pues no se están teniendo en cuenta los commits entre pull requests y asignando el mismo peso a todas las issues de la misma historia de usuario, pero nos da una idea general de cómo ha ido la evolución del trabajo.
![enter image description here](https://i.imgur.com/22YlpoC.png)

Además, hay que tener en cuenta que dicho gráfico está realizado a partir de los puntos asignados en primera instancia a las historias de usuario, parte de la desviación, también se debe a que algunas historias tenían un peso superior al que realmente se asignó inicialmente. Aún así se ve como no hemos podido finalizar todo lo planificado, se profundizará en este aspecto en la retrospectiva.

## Reuniones Scrum
#### Planificación del sprint
En la primera reunión en la que se planificó el sprint se eligieron las tareas anteriormente mencionadas. A estas se les asignó unos puntos en según criterio del grupo. Estos son:

| HU / Tamaño |  S (1 punto) | M (2 puntos) | L (4 puntos) |
| ----------- | ------------ | ------------ | ------------ |
|[HU-4](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-4-Etiquetado-de-tareas) | ------------ | ------X----- | ------------ |
|[HU-5](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-5-Descripci%C3%B3n-y-selector-de-fecha-en-tareas) | -----X------ | ------------ | ------------ |
|[HU-6](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-6-Comentarios-en-tareas) | ------------ | ------X----- | ------------ |
|[HU-7](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-7-Reactivaci%C3%B3n,-finalizaci%C3%B3n-y-borrado-de-tareas) | -----X------ | ------------ | ------------ |
|[HU-8](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-8-Filtrado-de-tareas) | ------------ | ------X----- | ------------ |
|[HU-9](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-9-Agregar-tareas-a-tableros) | ------------ | ------X----- | ------------ |
|[HU-10](https://github.com/mads-ua/todolistgrupo-2017-equipo-8/wiki/SGT-10-Asignar-usuarios-a-tareas) | ------------ | ------X----- | ------------ |

Siendo cada uno de los puntos una semana de trabajo.

#### Scrum diario.
Estas dos reuniones desarrolladas en la semana 1 y 3 fueron documentadas, en ellas quedan reflejados los problemas encontrados, las tareas realizadas y lo que se espera para la siguiente semana de desarollo.
#### Primera Reunión (29/11/2017)
> WEEKLY MEETING 29/11/2017

> Sergio: He terminado la historia de usuario 5 estimada para realizar en una semana (Descripción y selector fechas tareas), inicialmente sin problemas, realizadas pruebas en stage.

> Detecta que tenemos constructores no necesarios y se propone issue de refactorización tanto de código como de test. André y Adel están de acuerdo en dicha issue.

> Sergio tiene 2 historias disponibles, una para poder desactivar tareas y poder recuperarlas y otra para poder realizar comentarios sobre las tareas. Pensamos que aporta más valor la historia de comentarios, por lo que se pondrá con ella.

> André: está con la historia de usuario 9 agregar tareas a tableros, ya tiene realizado hasta el método de servicio (realizar la relación entre tableros y tareas,…) ha tenido problemas a la hora de pasar los tests por restricciones de clave ajena que estaban relacionadas con la carga de datos en test. Al final lo ha resuelto haciendo setters y manteniendo la relación.

> Comenta que le falta implementar la parte de la vista y durante esta semana va a realizar la misma.

> Adel: he realizado el modelo de etiquetas su crud hasta el service, y la relación con usuarios, tableros y tareas. Durante esta semana va a seguir con el service de las relaciones y las vistas.

#### Segunda Reunión (13/12/2017)
 > WEEKLY MEETING 13/12/2017

> Sergio: comenta que ha realizado el issue propuesto en la anterior reunión para la refactorización de los constructores y los tests implicados. Además, en la historia de usuario de añadir comentarios a tareas (historia de usuario 6) de tamaño estimado mediano y previsiblemente finalizado para el día de hoy, queda pendiente un issue para completar la historia de usuario, parte de ese retraso se debe a que ha realizado la issue no planificado inicialmente.

> En cuanto a inconvenientes encontrados desde la última reunión, Sergio nos indica que las relaciones con la creación del nuevo modelo han sido más complejas de lo esperado, aún con ello, la historia se finalizará muy cerca del tiempo estimado.

> Para esta semana, Sergio terminará la historia en la que se encuentra actualmente y se encargará de realizar la historia de tamaño pequeño con la que no se podrán eliminar tareas de forma directa y serán recuperables (historia de usuario 7).

> André: comenta que ha terminado la historia de usuario 9, de agregar tareas a tableros en su tiempo (restaba la parte de la vista y el controlador) y tiene al 50% la siguiente historia de usuario asignada (la 10), con lo que inicialmente va según lo previsto.

> Durante la finalización de la historia de usuario 9, surgieron inconvenientes respecto a la actualización de la clave ajena en una relación uno a muchos, tras varios errores e investigaciones, llegó a la conclusión de que la actualización de la clave ajena no es automática, tras realizarlo de forma explícita, todo funcionó de forma correcta. Este problema afectó también a Adel, que basaba parte de su comportamiento en el recurso corregido, finalmente lo resolvió haciendo un merge de master una vez resuelto el problema por André.

> En esta semana que resta para acabar el sprint, André finalizará la historia que tienen a medias (parte de vista y controlador de la historia de usuario 10).

> Adel: comenta que ha sido más complejo de lo esperado manejar las relaciones entre el nuevo modelo etiquetas de la historia de usuario 4 y que realmente ha habido un problema de estimación del tamaño de dicha historia, se calculó que sería una historia mediana, pero realmente es grande, eso afectará a la historia de usuario mediana que le queda pendiente.

> Para esta semana, aunque una historia grande ocuparía 4 semanas, estima que esta en este momento al 80% de la finalización de la historia 4 y aunque no todo, podrá hacer parte de la historia de filtrado.  (historia 8)


#### Retrospectiva(20/12/2017)


>  **Aspectos que han ido bien:**

>  El control que hemos tenido del trabajo usando Github, tanto a nivel de código como de tareas realizadas, en proceso y por realizar ha sido bastante positivo.
El grupo ha funcionado muy bien, nos hemos entendido perfectamente y el reparto del trabajo ha sido equilibrado. Hemos aprovechado la tecnología móvil para comunicarnos de forma más ágil y el tablero de Kanban también ha ayudado.
La metodología nos ha obligado a trabajar de forma más o menos continua, teniendo en cuenta, que cada uno de los integrantes tiene otras obligaciones y no se trata de un trabajo con horario concreto.

>  **Aspectos a mejorar:**

>  Hemos visto que el tener que esperar en los Pull Request a que confirmen ambos integrantes provocaba cuellos de botella, ya que, no coincidimos normalmente en horario y ha provocado que un recurso tuviera que estar parado mientras tanto. Sería un punto a mejorar, flexibilizando dicho aspecto.
>  Ha habido un problema de estimación de algunas historias que ha provocado retrasos que se pueden comprobar en la gráfica burndown, esos problemas de estimación, han provocado que no finalicemos todo lo pactado con el cliente. Debemos aprender de las desviaciones que estamos teniendo para estimar mejor en futuros sprints.

>  **Para el futuro:**

>  Completar la funcionalidad de filtrado.
>  Realizar un dashboard con datos significativos y navegables respecto a tableros y tareas.
>  Realizar columnas para trabajo por estados y rediseñar el aspecto de las tareas que podrían etiquetas.
