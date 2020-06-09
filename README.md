# Concurrent Sorted Set Specification

In many systems there is a need to keep track of a sorted structure. This data structure comes out-of-the-box in the popular open-source system Redis. However, Redis is explicitly designed to be single-threaded in order to keep the code and system simple. Given the amount of data and access patterns for retrieving it later, it's needed faster version that is able to support the same sorted set abstraction.

The goal of this task is to implemented a networked sorted set that handles positive integers. The program should listen on a Unix domain socket at "./socket", accept new connections from multiple clients, and process commands that are sent over these connections via the binary protocol described below. The program should be running, even after all connections have been disconnected, until it is terminated. Each <...> represents a four byte unsigned integer in network byte order* sent or received on the socket. All client -> server and server -> client commands are prefixed with the number of fields in the command (detailed below). All set ids, keys, and scores will be positive.

The server should implement the following commands:

Add Score: Adds member <key> to <set>, with score <score>. If <set> doesn't exist, it's created. If <key> is already in <set>, its score is updated. Should run in time O(log(size(<set>))).

    Client: <4> <1> <set> <key> <score>
    Server: <0>

Remove Key: Removes <key> from <set> if <set> exists and <key> is in <set>. Should run in time O(log(size(<set>))).

    Client: <3> <2> <set> <key>
    Server: <0>

Get Size: Returns the size of set <set>, or 0 if <set> doesn't exist. Should run in time O(1).

    Client: <2> <3> <set>
    Server: <1> <size>

Get key-value: Returns the score of key <key> in <set>, and 0 if either the set does not exist or does not contain <key>. Should run in time O(1).

    Client: <3> <4> <set> <key>
    Server: <1> <score>

Get Range: Returns all elements in sets <set1> ... <setM> with scores in the range [<lower>, <upper>]. Elements should be returned sorted by non-decreasing order of key. If two keys match, the elements with matching keys should be sorted by non-decreasing order of value. This is the most important operation, you should achieve the best asymptotic complexity you can.

    Client: <N> <5> <set1> ... <setM> <0> <lower> <upper>
    Server: <K> [<key> <score>] (repeat for each element of the set returned, where K is the total number of integers returned)

DISCONNECT:

    Client: <1> <6>
    Server: No response, then disconnect the client
   
# Solution

## Data Structure Design

Data structure design was driven by the requested asymptotic complexities. Given the need to achieve O(log n) for add and remove operations, O(1) for size and get, and as fast as possible in the range queries, it was determined to use a combination of two complementing data structures. To achieve O(1) in get, we need a direct (or semi-direct) addressing structure and after considering the cardinality (in the range of 0 to 2^31) it requires the use of a hash function to transform the keys to a discret range. For this reason as first data structure was selected a hash map.

With the goal to provide the best possible version for GETRANGE, it was decided to use a search tree, where the key of each node will be the score value, and contains a list for the keys containing that value. The implementation details of the structure has to guarantee that insert and remove operations in O(log n), and execution time logaritmic for the worst case scenarios. The structures capable to guarantee those properties are balanced trees, where it's enforced that the heith (h) is logaritmic with respect to the number of nodes. Some examples of those strucutures are red-black trees (h=2log(n+1)), AVL, 2-3 trees, B-tries, k-neigbours trees and others.

In this context is important to mention, it was also studied the use of van Emde Boas trees. This data structure is capable to perform all the basic operatios in O(log log u), given the keys are in the range from 0 to u. In the problem at hand where the range of the keys is big, this approach produce an use of memory close to 8GB to store. Considering the lack of specific information about the statistics related with the distribution of the elements, we assume the number of elements to store is going to be in practice considerably smaller than the potential universe of keys.

After studing the state of the art in research regarding dynamic range search, several sudies were found. Between them we need to highligth the work "On Dynamic Range Reporting in One Dimension" giving a perspective on the status of the field. In this work the authors propose a data structure to guarantee O(log log n) for searchs, at the same time they keep logaritmic times for updates and their space requirements are O(n), being n the number of elements to store. However, some of the implementation details are not clear in the work. For these reasons to provide the solution at this problem it was chosen to use as base for the implementation the theory behind red-black trees.

Considering the selected data structure has aproximately a heigth of h=log n, it's possible to develop an algorithm that finds the elements in a given range in O(log n) time. It's important to highligt in any case the resulting algorithm is going to be output dependant. For this reason it will depend on the number of elements in the requested range. With this caracteristics two variants were identified, both with the target asymptotic complexity. In the first version the tree structure is traversed without additional information (only using the score as key and the list of positions). In this version we start from the root and in each step we include the current element if it's score is in range, doing the recursive search in both children. If the value is out of range, the search is only performed in the child with chances of containing elements.

For the second version an optimization is applied to improve the performance by avoiding unnecesary comparisons and there reducing some of the constant coeficients in the polinomial execution time. In this case the node content is extended, it's included additional information about the minimun and maximun elements found as children of that node. The traverse is started similar to the first version, the root analysis is performed and if is in range, the children are only analysed if the search range is found in the node or it's decendans, using the aforemetioned informatio. To include the additional information in the nodes, the modify operations (insert and delete) are going to adjust the tree with children range. In both cases this can be achieved in O(log n), hence the asymptotic complexity of these operations remain the same. 

## Class design

Para representar un conjunto, se crearon las clases SortedSetState y SortedSetHandler que permiten almacenar el estado y realizar las operaciones básicas sobre el conjunto, respectivamente. En su implementación está estructura para almacenar y realizar las operaciones mantiene la tabla hash y el árbol de búsqueda, de forma paralela. De forma que las operaciones de actualización se realizan sobre ambas, el tamaño y la obtención sobre la tabla de dispersión y la búsqueda de rango sobre el árbol de búsqueda.

Para el manejo de todos los conjuntos se creó la clase SortedSetManager que realiza las operaciones sobre los eventos. Las responsabilidades de esta clase son transformar los datos, seleccionar el o los conjuntos sobre los que se realizará la operación y crear un nuevo conjunto cuando sea necesario. Para el caso de la búsqueda por rango se identifican los conjuntos objetivo y se unifican los resultados para ser entregados al cliente.

## Data strcucture network access

Para el acceso a través de la red se utilizó el soporte básico brindado por los sockets de Java. Para el servidor se creó un lanzador que se encuentra en “com.sortedset.init.launcher”, que se encarga de levantar un hilo que escucha y maneja las peticiones de los clientes.

El hilo que escucha las peticiones de los clientes se encuentra en la clase “com.sortedset.networking.ConnectionListener”  que recibe las conexiones entrantes. Para cada nueva conexión se crea un hilo que es iniciado para su manejo. Cada hilo iniciado es almacenado en una lista para poder detenerlo o realizar cualquier otra operación con el mismo. La lista de hilos es chequeada al pasar ciertos intervalos de tiempo para eliminar aquellas peticiones que hayan terminado. Luego de iniciado el hilo que maneja la petición el control regresa al hilo principal, por lo que se está en condición de procesar tantas comunicaciones concurrentes como se desee.

Los hilos que individualmente manejan las peticiones se encuentran representados por la clase “com.sortedset.networking.ConnectionHandler” que comienza la comunicación recibiendo el comando a ejecutar y en función de este recibe los parámetros y ejecuta la acción asociada sobre la estructura de datos. En caso que el comando lo requiera se envía la respuesta al cliente y se cierra la conexión.

El ConnectionListener crea para su trabajo una única instancia del SortedSetManager, y con esta realiza todas las operaciones solicitadas hasta que se detenga la aplicación. Las configuraciones generales, tales como puerto de comunicación y códigos de los comandos son establecidos en la clase “com.sortedset.config.CommonParam”.

## Concurrent operations in the Sorted Set

Para el diseño de la concurrencia se consideraron dos principios fundamentales:
    • Permitir el acceso concurrente en todos los casos donde sea posible.
    • Garantizar la consistencia de los datos entre operaciones.
En particular el segundo principio guía una de las principales decisiones de diseño de la aplicación en el tema de concurrencia. Debido a que algunas operaciones no retornan una respuesta al cliente puede ocurrir que se realice por un cliente una operación de inserción y a continuación una obtención del valor recién insertado. En este escenario puede darse el caso que la operación de obtención se ejecute antes de completarse la inserción, provocando una inconsistencia en los valores. Por este motivo, en el SortedSetManager se sincroniza el acceso a la tabla donde se encuentran almacenados los conjuntos para evitar que se realice de forma concurrente con el caso donde se está creando el conjunto que se solicita. Así se garantiza que si el conjunto está siendo creado por una operación precedente se pueda acceder al él. La sección sincronizada se limita en la mayor medida posible para garantizar el acceso concurrente a la estructura una vez que ha sido recuperada. Además dentro de cada conjunto se limita el acceso a las operaciones de lectura que arriban después de iniciada una escritura hasta que está no termine. En el caso de las lecturas no se bloquean entre sí.
La situación antes mencionada provoca que en el diseño se realicen bloqueos a los conjuntos (sólo en el momento que se recupera o se crea uno nuevo). Sin estos bloqueos un mismo usuario podría notar inconsistencias en los datos según el orden de las operaciones que ha ejecutado. Una posible solución a esta situación que permitiría eliminar los bloqueos consiste en adicionar una respuesta estándar de operación aceptada para las operaciones que no necesitan respuesta del servidor. Así el cliente sabría cuando su comando anterior fue ejecutado y pasaría a ejecutar el próximo.

## Backgroud updates for the Search Tree

Tomando en cuenta que a pesar que tres operaciones interactúan con el árbol de búsqueda, solo una lo hace como su fuente de información y que la operación de GETRANGE presumiblemente se realiza con menor frecuencia que el resto, se diseñó un proceso para la actualización en el segundo plano de la estructura de árbol. Con este enfoque se reduce el tiempo de procesamiento de las operaciones de ADD y REM, debido a que sólo el tiempo de inserción en la tabla de dispersión es que se encuentra esperando el cliente. La parte de la tarea que ejecuta la actualización en el árbol es introducida en una cola con una tarea independiente que se encarga de procesarlas. En el momento que se recibe una operación búsqueda de rango, se introduce también en la cola y se ejecuta después de procesadas las inserciones o eliminaciones que se produjeron antes. El uso de este enfoque no afecta los tiempos de la operación GETRANGE, ya que las operaciones de modificación se van realizando según la disponibilidad de procesamiento. De cualquier forma las operaciones sobre el árbol es obligatorio realizarlas de forma independiente.
Las clases que brindan el soporte a la característica antes mencionada se encuentran en el paquete “com.sortedset.sortedset.tree.background”. En la clase “com.sortedset.sortedset.tree.background.BackgroundProcess” se implementa un hilo que se procesa las operaciones que se almacenan en la cola. Este se mantiene detenido hasta que es notificado de la existencia de nuevas operaciones.

## Test Client

Para facilitar la realización de pruebas sobre la estructura se crearon varias clases que permiten ejecutar los comandos permitidos a un servidor. Estas clases se encuentran en el paquete “com.sortedset.client”. Estas clases se conectan al mismo puerto establecido en CommonParam y a la dirección que indique en “com.sortedset.client.ClientConfig”. En “com.sortedset.client.ClientSimulator” se incluyó un ejemplo básico que hace uso de las clases desarrolladas para probar el ejemplo incluido en el enunciado del ejercicio. Se incluyeron además un grupo de pruebas funcionales para probar el cumplimiento de los requisitos (con el objetivo de mantener la simplicidad no se usó ningún framework de pruebas en específico), que se encuentran en “com.sortedset.test”. 
