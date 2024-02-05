# Bicing IA

<font size="4">Primera práctica de la asignatura de IA en la FIB. </font>

<font size="4">Nota obtenida: 11 </font>

## Cómo ejecutar el programa

1. Desde una terminal, ir a la carpeta de la practica.

2. Anadir las librerias necesarias escribiendo el siguiente comando a la terminal:
  ```sh
  export CLASSPATH=".:./lib/Bicing.jar:./lib/AIMA.jar"
  ```

3. Compilar el programa con el comando:
  ```sh
  javac Main.java
  ```

4. Ejecutar el programa proporcionando los parámetros adecuados:
  ```sh
  java Main <algorithmMode> <initializationMode> <demandMode> <heuristicType> <E> <F> <B> <seed> [<iterations> <step> <k> <lambda>]
  ```

  * algorithmMode (algoritmo utilizado): "hc" (Hill Climbing) o "sa" (Simulated Annealing)
  * initializationMode: "easy", "medium" o "hard"
  * demandMode (tipo de demanda): "equi" o "rush"
  * heuristicType: 1 (sin coste de transporte) o 2 (con coste de trasporte)
  * E (num de estaciones): <entero positivo>
  * F (num de furgonetas): <entero positivo>
  * B (num de bicis): <entero positivo>
  * seed: <entero positivo>

  Los parametros opcionales se deben proporcionar si algorithmMode == "sa":
  * iterations: <entero positivo>
  * step: <entero positivo>
  * k: <entero positivo>
  * lambda: <double positivo>

5. Un ejemplo de comando para ejecutar con los parámetros por defecto del enunciado:
  ```sh
  java Main hc hard equi 1 25 5 1250 1234
  ```
  ```sh
  java Main sa hard equi 2 25 5 1250 1234 100000 20 125 0.0001
  ```

## Hecho por

Jordi Muñoz - [@jordimunozflorensa](https://github.com/jordimunozflorensa)

Jianing Xu - [@jianingxu1](https://github.com/jianingxu1)

Yiqi Zheng - [@yiqizhengshan](https://github.com/yiqizhengshan)

<br>

Link del proyecto: [https://github.com/yiqizhengshan/Bicing-IA](https://github.com/yiqizhengshan/Bicing-IA)

## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

[Java-url]: https://dev.java/
[Java.com]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
