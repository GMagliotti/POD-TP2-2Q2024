# POD - TPE2 - Multas de Estacionamiento
Este proyecto consiste en una aplicación de consola para el procesamiento de multas de estacionamiento basado en datos reales de las ciudades de Nueva York y Chicago.
La aplicación permite cargar los datos de las multas de estacionamiento de ambas ciudades, realizar consultas sobre los datos cargados y exportar los resultados de las consultas a un archivo CSV.
Para esto se sigue el modelo de programación MapReduce junto con el framework de Hazelcast, donde se divide el procesamiento de los datos en dos etapas: Map y Reduce. En la etapa Map se procesan los datos de entrada y se generan pares clave-valor, y en la etapa Reduce se procesan los pares clave-valor generados por la etapa Map y se generan los resultados finales.

## Estructura del proyecto
- **api**: Contiene las clases que definen la interfaz de la aplicación.
- **client**: Contiene los clientes que interactúan con los servicios de la aplicación.
- **server**: Contiene los servicios de la aplicación. Permite instanciar nodos de Hazelcast y ejecutar los servicios de la aplicación.

# Compilación
Para compilar el proyecto se debe ejecutar el siguiente comando en el directorio raíz del proyecto:
```bash
mvn clean package
```

# Ejecución
Para ejecutar las distintas queryes del proyecto se deben seguir los siguientes pasos:
1. Compilar el proyecto.
2. **Descomprimir y ejecutar el servidor**: Dentro del directorio `server/target`, ejecutar el siguiente comando para descomprimir el archivo `tpe1-g9-server-2024.2Q-bin.tar.gz`:
   ```sh
    tar -xzf tpe2-g9-server-1.0-SNAPSHOT
   ```
   Luego, ingresar al directorio `tpe2-g9-server-1.0-SNAPSHOT` y otorgar permisos de ejecución al script `run-server.sh`:
   ```sh
    chmod +x run-server.sh
   ```
   Ejecutar el servidor: El mismo toma como dirección y puerto default `192.168.1.*:5701`, donde `*` es el número de nodo. Si no se especifica un puerto, se tomará el siguiente puerto disponible en orden ascendente desde el puerto 5701. Para ejecutar el servidor, correr el siguiente comando.
   ```sh
    sh run-server.sh Daddress=<dirección> Dport=<puerto>
   ```
    Donde `<dirección>` es la dirección IP del nodo y `<puerto>` es el puerto del nodo.
3. **Descompimir el cliente**: Dentro del directorio `client/target`, ejecutar el siguiente comando para descomprimir el archivo `tpe1-g9-client-2024.2Q-bin.tar.gz`:
   ```sh
    tar -xzf tpe2-g9-client-1.0-SNAPSHOT
   ```
   Luego, ingresar al directorio `tpe2-g9-client-1.0-SNAPSHOT` y otorgar permisos de ejecución a los scripts .sh del mismo:
   ```sh
    chmod +x *.sh
   ```
4. **Ejecutar el cliente**: El cliente cuenta con queries predefinidas que se pueden ejecutar.
   * **Query 1: Total de multas por infracción y agencia.** Para ejecutar la query 1, ejecutar el siguiente comando:
     ```sh
      sh query1.sh -Daddresses=<direcciones> -Dcity=<ciudad> -DinPath=<inPath> -DoutPath=<outPath>
     ```
        Donde: 
        *  `<direcciones>` es la dirección IP de los nodos separados por punto y coma. Por ejemplo: -Daddresses='192.168.1.2:5701;192.168.1.2:5702'.
        * `<ciudad>` es la ciudad de la cual se quieren obtener los datos. Puede ser 'CHI' o 'NYC'.
        * `<inPath>` es la ruta del directorio donde se encuentran los archivos de entrada.
        * `<outPath>` es la ruta del directorio donde se guardarán las salidas de las queryes.
    * **Query 2: Recaudación YTD por agencia.** Para ejecutar la query 2, ejecutar el siguiente comando:
        ```sh
        sh query2.sh -Daddresses=<direcciones> -Dcity=<ciudad> -DinPath=<inPath> -DoutPath=<outPath>
        ```
        Donde: 
        *  `<direcciones>` es la dirección IP de los nodos separados por punto y coma. Por ejemplo: -Daddresses='
        * `<ciudad>` es la ciudad de la cual se quieren obtener los datos. Puede ser 'CHI' o 'NYC'.
        * `<inPath>` es la ruta del directorio donde se encuentran los archivos de entrada.
        * `<outPath>` es la ruta del directorio donde se guardarán las salidas de las queryes.
    * **Query 3: Porcentaje de patentes reincidentes por barrio en el rango \[from, to].** Para ejecutar la query 3, ejecutar el siguiente comando:
        ```sh
        sh query3.sh -Daddresses=<direcciones> -Dcity=<ciudad> -DinPath=<inPath> -DoutPath=<outPath> -Dn=<n> -Dfrom=<from> -Dto=<to>
        ```
        Donde: 
        *  `<direcciones>` es la dirección IP de los nodos separados por punto y coma. Por ejemplo: -Daddresses='
        * `<ciudad>` es la ciudad de la cual se quieren obtener los datos. Puede ser 'CHI' o 'NYC'.
        * `<inPath>` es la ruta del directorio donde se encuentran los archivos de entrada.
        * `<outPath>` es la ruta del directorio donde se guardarán las salidas de las queryes.
        * `<n>` es el número a partir del cual se considera a una patente como patente reincidente.
        * `<from>` es la fecha de inicio del rango de fechas a considerar en formato dd/MM/yyyy.
        * `<to>` es la fecha de fin del rango de fechas a considerar en formato dd/MM/yyyy.
    * **Query 4: Top N infracciones con mayor diferencia entre máximos y mínimos montos para una agencia.** Para ejecutar la query 4, ejecutar el siguiente comando:
        ```sh
        sh query4.sh -Daddresses=<direcciones> -Dcity=<ciudad> -DinPath=<inPath> -DoutPath=<outPath> -Dn=<n> -Dagency=<agency>
        ```
        Donde: 
        *  `<direcciones>` es la dirección IP de los nodos separados por punto y coma. Por ejemplo: -Daddresses='
        * `<ciudad>` es la ciudad de la cual se quieren obtener los datos. Puede ser 'CHI' o 'NYC'.
        * `<inPath>` es la ruta del directorio donde se encuentran los archivos de entrada.
        * `<outPath>` es la ruta del directorio donde se guardarán las salidas de las queryes.
        * `<n>` es la cantidad de infracciones a mostrar.
        * `<agency>` es el nombre de la agencia de la cual se quieren obtener los datos.
