Para ejecutar el programa:
1. Desde una terminal, ir a la carpeta de la practica.

2. Anadir las librerias necesarias escribiendo el siguiente comando a la terminal:
  export CLASSPATH=".:./lib/Bicing.jar:./lib/AIMA.jar"

3. Compilar el programa con el comando:
  javac Main.java

4. Ejecutar el programa proporcionando los parámetros adecuados:
  java Main <algorithmMode> <initializationMode> <demandMode> <heuristicType> <E> <F> <B>
    <seed> [<iterations> <step> <k> <lambda>]

      Definicion de los parametros:
      - algorithmMode (algoritmo utilizado): "hc" (Hill Climbing) o "sa" (Simulated Annealing)
      - initializationMode: "easy", "medium" o "hard"
      - demandMode (tipo de demanda): "equi" o "rush"
      - heuristicType: 1 (sin coste de transporte) o 2 (con coste de trasporte)
      - E (num de estaciones): <entero positivo>
      - F (num de furgonetas): <entero positivo>
      - B (num de bicis): <entero positivo>
      - seed: <entero positivo>

      Los parametros opcionales se deben proporcionar si algorithmMode == "sa":
      - iterations: <entero positivo>
      - step: <entero positivo>
      - k: <entero positivo>
      - lambda: <double positivo>

5. Un ejemplo de comando para ejecutar con los parámetros por defecto del enunciado:
  java Main hc hard equi 1 25 5 1250 1234