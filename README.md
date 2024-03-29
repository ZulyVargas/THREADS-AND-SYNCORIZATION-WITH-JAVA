
## Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW


#### Ejercicio – programación concurrente, condiciones de carrera y sincronización de hilos. EJERCICIO INDIVIDUAL O EN PAREJAS.

## Estudiantes

- Camilo Andrés Pichimata Cárdenas
- Zuly Valentina Vargas Ramírez

### Prerrequisitos para la ejecución de este laboratorio

* [Maven](https://maven.apache.org/) - Dependency Management
* [Java ](https://www.oracle.com/co/java/technologies/javase/javase-jdk8-downloads.html) -  Development Environment
* [Git](https://git-scm.com/) - Version Control System

##### Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?
    
    El consumo de CPU alcanza un 12% aproximadamente. La clase responsable es la clase Consumer ya que esta sigue ejecutando el ciclo aún cuando la lista esta vacía y no tiene nada que consumir.
    ![](img/cpu_before.jpg )
2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.
    
    Para corregir este consumo sincronizamos la lista que guarda los números de tal forma que el consumidor solo siga ejecutando el ciclo cuando la lista tenga elementos, de lo contrario se queda en estado de espera.
    Desde Producer al momento de realizar la inserción en la lista de un dato se notifica al Consumidor para que este se ejecute y salga del estado de espera.
    Estos ajustes disminuyen en consumo de CPU a un uso aproximado del 3%.
   ![](img/cpu_after.jpg )
	
    3.Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. 
    Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), 
    haga que dicho límite se respete. Revise el API de la colección usada como cola para ver 
    cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite 
    pequeño para el 'stock', no haya consumo alto de CPU ni errores.
	
    Para lograr esto se empleo el constructor LinkedBlockingQueue<>(int capacity); para definir el límite de la lista. Se dio un tiempo de espera del consumidor de 	2 segundos para que el consumidor consumiera más lento.


##### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).
- Lo anterior, garantizando que no se den condiciones de carrera.

	Para lograr esto se modificó el programa de tal forma que la lista donde se guardan las ocurrencias fuera concurrente, al igual que se modificaron los atributos 	que contabilizaban el número de oucrrencias encontradas y listas revisas de tal forma que fueran atómicos y garantizar que no existieran condiciones de carrea y 	que exista una busqueda más eficiente que la que se realizaba anteriormente.
	
##### Parte III. – Avance para el martes, antes de clase.

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:

	* Se tienen N jugadores inmortales.
	* Cada jugador conoce a los N-1 jugador restantes.
	* Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.
	* El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.

2. Revise el código e identifique cómo se implemento la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de tiempo). Para este caso, para N jugadores, cual debería ser este valor?.

	El valor debería ser el número de jugadores por la cantidad de salud inicial.
	 	
3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. Se cumple el invariante?.
 	
 	El invariante no se cumple ya que la suma total debería ser de *300*. (100 de nivel de salud * 3 immmortals) y es en este caso de *980*, además en cada una de las veces que se pulsa el botón el resultado cambia.
 	 
 	![](img/invariante.jpg )

4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.
	
	Para lograr que se cumpla el invariante se dejó como AtomicInteger la salud para que sea editada una vez al tiempo. Se implementó la opción resume, para esto se 	usa notifyAll() ara que los hilos sigan ejecutandose. En la opción de pause se hace wait() para los hilos y se procede a realizar la suma de la salud de cada 	uno.
	

5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). Se cumple o no el invariante?.
	Aún no se cumple el invariante.
	
6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:

	```java
	synchronized(locka){
		synchronized(lockb){
			…
		}
	}
	```
	Para evitar las condiciones de carrera se debe sincronizar el acceso a cada immortal.
	
7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.
	
	Implementando el bloque de sincronización, el programa se detiene. para comprobar lo sucedido se emplearon los comandos jps y jstack co el id del proceso dado:
	
![](img/jps1.jpg )

	Se puede observar que se produjo un deadlock:
	
![](img/jps2.jpg )

	
	
8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).

	En las lecturas se menciona que "si se puede garantizar que todos los hilos que necesitan bloqueos L y M al mismo tiempo siempre adquieren L y M en el mismo 	orden, 	no habrá bloqueo. " Por lo que para solucionar el deadlock presentado es posible indicar que se bloquee primero el immortal con id menor entre los dos.
		
![](img/sindb.jpg )
	
	De esta forma el programa no se detiene y se cumple el invariante (suma total del valor health igual a  300)
	
9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.


	- 100:
	
![](img/100threads.jpg )
	
Se cumple el invariante de 10000 para este caso (100*100).	

	- 1000:
	
![](img/1000threads.jpg )
	
Se cumple el invariante de 100000 para este caso (1000*100).

	-10000:
		
![](img/10000threads.jpg )
	
Se cumple el invariante de 1000000 para este caso (10000*100).

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:
	* Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.
	* Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.
	
	Para solucionar esto se debe agregar una verificación en la cual se suprima al hilo de la lista de inmortales si ya esta muerto.
	

11. Para finalizar, implemente la opción STOP.
	

<!--
### Criterios de evaluación

1. Parte I.
	* Funcional: La simulación de producción/consumidor se ejecuta eficientemente (sin esperas activas).

2. Parte II. (Retomando el laboratorio 1)
	* Se modificó el ejercicio anterior para que los hilos llevaran conjuntamente (compartido) el número de ocurrencias encontradas, y se finalizaran y retornaran el valor en cuanto dicho número de ocurrencias fuera el esperado.
	* Se garantiza que no se den condiciones de carrera modificando el acceso concurrente al valor compartido (número de ocurrencias).


2. Parte III.
	* Diseño:
		- Coordinación de hilos:
			* Para pausar la pelea, se debe lograr que el hilo principal induzca a los otros a que se suspendan a sí mismos. Se debe también tener en cuenta que sólo se debe mostrar la sumatoria de los puntos de vida cuando se asegure que todos los hilos han sido suspendidos.
			* Si para lo anterior se recorre a todo el conjunto de hilos para ver su estado, se evalúa como R, por ser muy ineficiente.
			* Si para lo anterior los hilos manipulan un contador concurrentemente, pero lo hacen sin tener en cuenta que el incremento de un contador no es una operación atómica -es decir, que puede causar una condición de carrera- , se evalúa como R. En este caso se debería sincronizar el acceso, o usar tipos atómicos como AtomicInteger).

		- Consistencia ante la concurrencia
			* Para garantizar la consistencia en la pelea entre dos inmortales, se debe sincronizar el acceso a cualquier otra pelea que involucre a uno, al otro, o a los dos simultáneamente:
			* En los bloques anidados de sincronización requeridos para lo anterior, se debe garantizar que si los mismos locks son usados en dos peleas simultánemante, éstos será usados en el mismo orden para evitar deadlocks.
			* En caso de sincronizar el acceso a la pelea con un LOCK común, se evaluará como M, pues esto hace secuencial todas las peleas.
			* La lista de inmortales debe reducirse en la medida que éstos mueran, pero esta operación debe realizarse SIN sincronización, sino haciendo uso de una colección concurrente (no bloqueante).

	

	* Funcionalidad:
		* Se cumple con el invariante al usar la aplicación con 10, 100 o 1000 hilos.
		* La aplicación puede reanudar y finalizar(stop) su ejecución.
		
		-->

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.
