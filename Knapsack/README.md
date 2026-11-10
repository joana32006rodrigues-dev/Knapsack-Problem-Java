=================================================================
TRABALHO PRÁTICO - PROBLEMA DA MOCHILA (KNAPSACK PROBLEM)
=================================================================

# DESCRIÇÃO DO PROJETO
Este projeto implementa e avalia diferentes algoritmos metaheurísticos
para a resolução do Knapsack Problem (Problema da Mochila).

Foram implementadas as seguintes abordagens:
1. Heurísticas Construtivas:
   - Greedy By Density (Ordenação por rácio Valor/Peso)
   - Greedy By Value (Ordenação por Valor Absoluto)

2. Heurísticas de Pesquisa Local/Melhoramento:
   - Local Search First-Improvement (Determinístico)
   - Hill Climbing with Random Restart (Estocástico)

# ESTRUTURA DE PASTAS E FICHEIROS
- /instancias/ : Esta pasta contém os ficheiros de texto com as instâncias do problema (ex: n_1000_1).
- Main.java : Classe principal que executa a bateria de testes e gera as tabelas.
- KnapsackItem.java : Classe que representa um item (ID, valor, peso).
- GreedyByDensity.java / GreedyByValue.java : Algoritmos construtivos.
- LocalSearch.java / HillClimbingRandomRestart.java : Algoritmos de pesquisa local.

# COMO COMPILAR E EXECUTAR O CÓDIGO
É necessário garantir que a pasta "instancias" se encontra na raiz do diretório de execução, ao mesmo nível dos ficheiros .java.

1. Abrir o terminal no diretório do projeto.
2. Compilar os ficheiros:
   javac Main.java
3. Executar o programa principal:
   java Main

# NOTAS DE EXECUÇÃO
- O programa executa os métodos sequencialmente para todos os ficheiros contidos na pasta "instancias".
- A capacidade da mochila está predefinida como 10.000.000.000 (10 mil milhões) no método de leitura.
- Os tempos de execução reportados nas tabelas medem ESTRITAMENTE o tempo da Pesquisa Local, excluindo o tempo de processamento das heurísticas construtivas.