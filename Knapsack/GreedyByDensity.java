import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * HEURÍSTICA CONSTRUTIVA 1 — Greedy por Densidade de Valor
 * =====================================================================
 *
 * CATEGORIA: Heurística Construtiva
 *   Constrói uma solução de raiz, adicionando itens um a um até não ser
 *   possível adicionar mais nenhum sem violar a restrição de capacidade.
 *
 * IDEIA CENTRAL:
 *   Nem sempre o item mais valioso é o melhor a escolher — pode ser
 *   muito pesado e impedir a entrada de outros. O critério correcto é
 *   maximizar o valor obtido por unidade de capacidade usada:
 *
 *       densidade(i) = valor(i) / peso(i)
 *
 *   Ordenando os itens por densidade decrescente e inserindo gananciosamente,
 *   obtemos uma boa aproximação à solução ótima em tempo linear após ordenação.
 *
 * PASSOS DO ALGORITMO:
 *   1. Calcular a densidade de cada item: d_i = v_i / w_i
 *   2. Ordenar todos os itens por densidade DECRESCENTE
 *   3. Percorrer os itens por essa ordem:
 *        - Se o item couber na capacidade restante → inserir
 *        - Se não couber → saltar (sem tentar retirar outros itens)
 *   4. Parar quando todos os itens forem analisados
 *
 * COMPLEXIDADE:
 *   Temporal: O(n log n) — dominada pela ordenação
 *   Espacial: O(n) — cópia da lista de itens
 *
 * LIMITAÇÕES (importante para a defesa):
 *   - Não garante a solução ótima no caso 0-1 (ao contrário do caso fracionário)
 *   - Não volta atrás: se um item foi ignorado, nunca é reconsiderado
 *   - Pode deixar espaço vazio na mochila se nenhum item restante couber
 *
 * DISTINÇÃO FACE A [C2] GreedyByValue:
 *   C2 ignora o peso ao ordenar — considera apenas o valor absoluto.
 *   C1 considera a eficiência (valor por unidade de peso) — tende a produzir
 *   soluções de melhor qualidade, especialmente quando os pesos são variados.
 *
 * REFERÊNCIA: Dantzig (1957); Kellerer, Pferschy & Pisinger (2004)
 */

public class GreedyByDensity {
    public static List<KnapsackItem> solve(List<KnapsackItem> items, long capacity) {
        List<KnapsackItem> selected = new ArrayList<>();
        List<KnapsackItem> sortedItems = new ArrayList<>(items);
        
        // Ordenar por densidade decrescente
        sortedItems.sort((a, b) -> {
            double densityA = (double) a.getValue() / a.getWeight();
            double densityB = (double) b.getValue() / b.getWeight();
            return Double.compare(densityB, densityA); 
        });

        long currentWeight = 0;
        for (KnapsackItem item : sortedItems) {
            if (currentWeight + item.getWeight() <= capacity) {
                selected.add(item);
                currentWeight += item.getWeight();
            }
        }
        return selected;
    }
}