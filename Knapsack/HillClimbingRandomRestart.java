import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * =====================================================================
 * HEURÍSTICA DE PESQUISA LOCAL 2 — Hill Climbing com Reinicialização Aleatória
 *                                   (Hill Climbing with Random Restart — HCRR)
 * =====================================================================
 *
 * CATEGORIA: Heurística de Pesquisa Local / Melhoramento (com componente estocástica)
 *
 * IDEIA CENTRAL:
 *   O problema principal de qualquer pesquisa local é ficar presa em ótimos
 *   locais — soluções que não podem ser melhoradas por pequenas modificações,
 *   mas que estão longe do ótimo global.
 *   A solução? Não partir de um único ponto de partida. Repetir o Hill Climbing
 *   várias vezes, cada vez a partir de um ponto ALEATÓRIO diferente, e guardar
 *   o melhor resultado de todas as tentativas.
 *
 * DISTINÇÃO FUNDAMENTAL FACE A [PL1] LocalSearch — First-Improvement:
 *
 *   PL1 — DETERMINÍSTICO:
 *     - Parte SEMPRE da mesma solução greedy (C1)
 *     - Uma única execução → um único ótimo local
 *     - Execuções repetidas dão sempre o mesmo resultado
 *     - Limitado à região do espaço de soluções acessível a partir de C1
 *
 *   PL2 — ESTOCÁSTICO:
 *     - Parte de soluções ALEATÓRIAS (diferentes em cada reinicialização)
 *     - Múltiplas execuções → múltiplos ótimos locais → guarda o melhor
 *     - Explora regiões do espaço de soluções completamente diferentes
 *     - Reduz activamente a probabilidade de ficar preso num ótimo local mau
 *
 * PASSOS DO ALGORITMO:
 *   Para cada reinicialização r = 1, 2, ..., numRestarts:
 *     a) GERAR solução inicial ALEATÓRIA viável:
 *          - Embaralhar todos os itens numa ordem aleatória
 *          - Inserir itens por essa ordem até à capacidade
 *          → Cada reinicialização produz um ponto de partida diferente
 *     b) APLICAR Hill Climbing (First-Improvement) até ótimo local:
 *          - Tentar inserções e swaps 1-para-1
 *          - Aceitar o primeiro movimento que melhore a solução
 *          - Repetir até não haver mais melhorias
 *     c) Se o ótimo local desta reinicialização for MELHOR que o melhor
 *        global encontrado até agora → actualizar o melhor global
 *   Retornar o melhor de todos os ótimos locais encontrados.
 *
 * REPRODUTIBILIDADE:
 *   A semente do gerador aleatório é fixa (12345L), garantindo que duas
 *   execuções do programa sobre a mesma instância produzem sempre o mesmo
 *   resultado — requisito importante para análise experimental rigorosa.
 *
 * COMPLEXIDADE:
 *   Temporal: O(numRestarts × k × n²)
 *             onde k = iterações médias de HC por reinicialização
 *   Espacial: O(n)
 *
 * REFERÊNCIA: Concept well-established in combinatorial optimization literature.
 *             Ver: Michalewicz & Fogel (2004) — How to Solve It: Modern Heuristics
 */
public class HillClimbingRandomRestart {

    // Semente fixa → reprodutibilidade total entre execuções
    private static final Random rng = new Random(12345L);

    /**
     * Executa o Hill Climbing com Reinicialização Aleatória.
     *
     * @param allItems    lista completa de todos os itens disponíveis
     * @param capacity    capacidade máxima da mochila
     * @param numRestarts número de reinicializações aleatórias independentes
     * @return melhor solução encontrada entre todas as reinicializações
     */
    public static List<KnapsackItem> solve(List<KnapsackItem> allItems,
                                           long  capacity,
                                           int numRestarts) {

        List<KnapsackItem> bestOverall = new ArrayList<KnapsackItem>();
        long bestValue  = 0;
        int totalIter  = 0;

        for (int restart = 0; restart < numRestarts; restart++) {

            // -----------------------------------------------------------------
            // Passo a): Gerar solução inicial ALEATÓRIA viável
            // Embaralhar e inserir por ordem aleatória — completamente diferente
            // do greedy (que insere por ordem de densidade ou valor).
            // -----------------------------------------------------------------
            List<KnapsackItem> currentSolution = randomFeasibleSolution(allItems, capacity);

            // -----------------------------------------------------------------
            // Passo b): Hill Climbing (First-Improvement) desta solução aleatória
            // -----------------------------------------------------------------
            boolean improved  = true;
            int     iteration = 0;

            while (improved) {
                improved = false;
                iteration++;

                long currentWeight = getTotalWeight(currentSolution);
                long currentValue  = getTotalValue(currentSolution);
                List<KnapsackItem> outsideItems = getOutsideItems(allItems, currentSolution);

                // Tentativa de inserção directa (first-improvement)
                for (int i = 0; i < outsideItems.size(); i++) {
                    KnapsackItem candidate = outsideItems.get(i);
                    if (currentWeight + candidate.getWeight() <= capacity) {
                        currentSolution.add(candidate);
                        currentWeight += candidate.getWeight();
                        currentValue  += candidate.getValue();
                        improved       = true;
                        outsideItems   = getOutsideItems(allItems, currentSolution);
                        i = -1;
                    }
                }

                // Tentativa de swap 1-para-1 (first-improvement)
                outer:
                for (int o = 0; o < outsideItems.size(); o++) {
                    KnapsackItem outItem = outsideItems.get(o);
                    for (int s = 0; s < currentSolution.size(); s++) {
                        KnapsackItem inItem    = currentSolution.get(s);
                        long          newWeight = currentWeight - inItem.getWeight() + outItem.getWeight();
                        long          newValue  = currentValue  - inItem.getValue()  + outItem.getValue();
                        if (newWeight <= capacity && newValue > currentValue) {
                            currentSolution.remove(s);
                            currentSolution.add(outItem);
                            currentWeight = newWeight;
                            currentValue  = newValue;
                            improved      = true;
                            outsideItems  = getOutsideItems(allItems, currentSolution);
                            break outer;
                        }
                    }
                }
            }
            totalIter += iteration;

            // -----------------------------------------------------------------
            // Passo c): Actualizar o melhor global se esta reinicialização for melhor
            // -----------------------------------------------------------------
            long localValue = getTotalValue(currentSolution);
            if (localValue > bestValue) {
                bestValue   = localValue;
                bestOverall = new ArrayList<KnapsackItem>(currentSolution);
            }
        }

        System.out.println("  [PL2] HC Random Restart (" + numRestarts + " reinic.)"
                + " | Total Iteracoes: " + totalIter
                + " | Valor: " + bestValue
                + " | Peso: "  + getTotalWeight(bestOverall));

        return bestOverall;
    }

    /**
     * Gera uma solução inicial aleatória viável.
     * Embaralha os itens e insere por essa ordem até à capacidade máxima.
     * O resultado varia a cada chamada (o rng avança a cada shuffle).
     */
    private static List<KnapsackItem> randomFeasibleSolution(List<KnapsackItem> allItems,
                                                               long  capacity) {
        List<KnapsackItem> shuffled = new ArrayList<KnapsackItem>(allItems);
        Collections.shuffle(shuffled, rng); // ordem aleatória diferente a cada reinicialização

        List<KnapsackItem> solution = new ArrayList<KnapsackItem>();
        long currentWeight = 0;
        for (int i = 0; i < shuffled.size(); i++) {
            KnapsackItem item = shuffled.get(i);
            if (currentWeight + item.getWeight() <= capacity) {
                solution.add(item);
                currentWeight += item.getWeight();
            }
        }
        return solution;
    }

    // =========================================================================
    // Métodos auxiliares
    // =========================================================================

    public static long getTotalValue(List<KnapsackItem> items) {
        long total = 0;
        for (int i = 0; i < items.size(); i++) total += items.get(i).getValue();
        return total;
    }

    public static long getTotalWeight(List<KnapsackItem> items) {
        long total = 0;
        for (int i = 0; i < items.size(); i++) total += items.get(i).getWeight();
        return total;
    }

    private static List<KnapsackItem> getOutsideItems(List<KnapsackItem> allItems,
                                                       List<KnapsackItem> solution) {
        List<KnapsackItem> outside = new ArrayList<KnapsackItem>();
        for (int i = 0; i < allItems.size(); i++) {
            if (!containsById(solution, allItems.get(i))) outside.add(allItems.get(i));
        }
        return outside;
    }

    private static boolean containsById(List<KnapsackItem> list, KnapsackItem target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == target.getId()) return true;
        }
        return false;
    }
}
