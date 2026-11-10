import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * HEURÍSTICA DE PESQUISA LOCAL 1 — First-Improvement
 * =====================================================================
 *
 * CATEGORIA: Heurística de Pesquisa Local / Melhoramento
 *   Parte de uma solução já construída (por um algoritmo construtivo)
 *   e tenta melhorá-la iterativamente através de pequenas modificações.
 *
 * IDEIA CENTRAL:
 *   As heurísticas construtivas tomam decisões locais irreversíveis e
 *   podem produzir soluções subótimas. A pesquisa local "repara" essas
 *   decisões explorando a vizinhança da solução actual.
 *
 * VIZINHANÇA UTILIZADA (dois tipos de movimentos):
 *   Movimento 1 — Inserção directa:
 *     Se um item FORA da mochila couber sem remover nenhum outro → inserir.
 *     Corrige casos em que o construtivo ignorou itens que cabem no espaço restante.
 *
 *   Movimento 2 — Swap 1-para-1:
 *     Remover um item que está DENTRO e inserir um que está FORA,
 *     se a troca aumentar o valor total e respeitar a capacidade.
 *     Permite substituir itens "maus" por itens "melhores".
 *
 * ESTRATÉGIA: FIRST-IMPROVEMENT
 *   Assim que é encontrado QUALQUER movimento que melhora a solução,
 *   esse movimento é aceite IMEDIATAMENTE e o algoritmo recomeça do início.
 *   Não avalia os movimentos restantes nessa iteração.
 *   → Mais rápido por iteração, converge com menos avaliações.
 *
 * PASSOS DO ALGORITMO:
 *   1. Partir da solução inicial fornecida (ex: resultado do GreedyByDensity)
 *   2. Tentar Movimento 1 (inserção): aceitar o PRIMEIRO item que caiba
 *   3. Se não houver inserção possível, tentar Movimento 2 (swap):
 *      aceitar o PRIMEIRO swap que melhore o valor
 *   4. Se algum movimento foi aceite → recomeçar do passo 2
 *   5. Se nenhum movimento melhorou → ÓTIMO LOCAL atingido → terminar
 *
 * CONDIÇÃO DE PARAGEM: ótimo local
 *   Nenhum dos movimentos da vizinhança melhora a solução actual.
 *   Atenção: ótimo local ≠ ótimo global (limitação de toda a pesquisa local).
 *
 * COMPLEXIDADE:
 *   Temporal: O(k × n²) onde k = número de iterações até ao ótimo local
 *   Espacial: O(n)
 *
 * DISTINÇÃO FACE A [PL2] HillClimbingRandomRestart:
 *   PL1 é DETERMINÍSTICO: dado o mesmo ponto de partida, produz sempre o
 *   mesmo resultado. Fica preso no primeiro ótimo local encontrado.
 *   PL2 é ESTOCÁSTICO: usa múltiplos pontos de partida aleatórios para
 *   explorar diferentes regiões do espaço de soluções.
 *
 * REFERÊNCIA: Aarts & Lenstra (2003) — Local Search in Combinatorial Optimization
 */
public class LocalSearch {

    /**
     * Executa a pesquisa local First-Improvement.
     *
     * @param allItems  todos os itens disponíveis (universo completo)
     * @param initial   solução inicial (produzida por um construtivo)
     * @param capacity  capacidade máxima da mochila
     * @param label     nome da solução inicial (para identificação no output)
     * @return melhor solução local encontrada a partir da solução inicial
     */
    public static List<KnapsackItem> solve(List<KnapsackItem> allItems,
                                           List<KnapsackItem> initial,
                                           long capacity,
                                           String label) {

        // Trabalhar sobre uma cópia para preservar a solução inicial original
        List<KnapsackItem> currentSolution = new ArrayList<KnapsackItem>(initial);
        boolean improved  = true;
        int     iteration = 0;

        // Ciclo principal: repete enquanto houver melhorias
        while (improved) {
            improved = false;
            iteration++;

            long currentWeight = getTotalWeight(currentSolution);
            long currentValue  = getTotalValue(currentSolution);
            List<KnapsackItem> outsideItems = getOutsideItems(allItems, currentSolution);

            // ------------------------------------------------------------------
            // Movimento 1: Inserção directa (First-Improvement)
            // Inserir o PRIMEIRO item de fora que caiba — não avalia os restantes.
            // ------------------------------------------------------------------
            for (int i = 0; i < outsideItems.size(); i++) {
                KnapsackItem candidate = outsideItems.get(i);
                if (currentWeight + candidate.getWeight() <= capacity) {
                    currentSolution.add(candidate);
                    currentWeight += candidate.getWeight();
                    currentValue  += candidate.getValue();
                    improved       = true;
                    outsideItems   = getOutsideItems(allItems, currentSolution);
                    i = -1; // reiniciar o ciclo (first-improvement)
                }
            }

            // ------------------------------------------------------------------
            // Movimento 2: Swap 1-para-1 (First-Improvement)
            // Aceitar o PRIMEIRO swap que aumente o valor total.
            // ------------------------------------------------------------------
            for (int o = 0; o < outsideItems.size(); o++) {
                KnapsackItem outItem = outsideItems.get(o); // candidato a entrar
                for (int s = 0; s < currentSolution.size(); s++) {
                    KnapsackItem inItem    = currentSolution.get(s); // candidato a sair
                    long          newWeight = currentWeight - inItem.getWeight() + outItem.getWeight();
                    long          newValue  = currentValue  - inItem.getValue()  + outItem.getValue();

                    // Critério de aceitação: peso válido E valor estritamente maior
                    if (newWeight <= capacity && newValue > currentValue) {
                        currentSolution.remove(s);
                        currentSolution.add(outItem);
                        currentWeight = newWeight;
                        currentValue  = newValue;
                        improved      = true;
                        outsideItems  = getOutsideItems(allItems, currentSolution);
                        o = -1; // reiniciar ciclo externo (first-improvement)
                        break;  // sair do ciclo interno
                    }
                }
                if (o == -1) break;
            }
            // Se improved == false → nenhum movimento melhorou → ótimo local → termina
        }

        System.out.println("  [PL1] First-Improvement (" + label + ")"
                + " | Iteracoes: " + iteration
                + " | Valor: " + getTotalValue(currentSolution)
                + " | Peso: "  + getTotalWeight(currentSolution));

        return currentSolution;
    }

    // =========================================================================
    // Métodos auxiliares (public static para reutilização no Main)
    // =========================================================================

    /** Soma dos valores de todos os itens da lista. */
    public static long getTotalValue(List<KnapsackItem> items) {
        long total = 0;
        for (int i = 0; i < items.size(); i++) total += items.get(i).getValue();
        return total;
    }

    /** Soma dos pesos de todos os itens da lista. */
    public static long getTotalWeight(List<KnapsackItem> items) {
        long total = 0;
        for (int i = 0; i < items.size(); i++) total += items.get(i).getWeight();
        return total;
    }

    /** Devolve os itens do universo que NÃO estão na solução actual (por ID). */
    private static List<KnapsackItem> getOutsideItems(List<KnapsackItem> allItems,
                                                       List<KnapsackItem> solution) {
        List<KnapsackItem> outside = new ArrayList<KnapsackItem>();
        for (int i = 0; i < allItems.size(); i++) {
            if (!containsById(solution, allItems.get(i))) outside.add(allItems.get(i));
        }
        return outside;
    }

    /** Verifica se um item (por ID) está contido numa lista. */
    private static boolean containsById(List<KnapsackItem> list, KnapsackItem target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == target.getId()) return true;
        }
        return false;
    }
}
