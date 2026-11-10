import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * HEURÍSTICA CONSTRUTIVA 2 — Greedy por Valor Absoluto
 * =====================================================================
 *
 * CATEGORIA: Heurística Construtiva
 *   Constrói uma solução de raiz, adicionando itens um a um até não ser
 *   possível adicionar mais nenhum sem violar a restrição de capacidade.
 *
 * IDEIA CENTRAL:
 *   Priorizar os itens de maior valor absoluto, independentemente do peso
 *   que ocupam. A intuição é simples: "coloca primeiro o que vale mais".
 *
 * PASSOS DO ALGORITMO:
 *   1. Ordenar todos os itens por valor absoluto DECRESCENTE
 *   2. Percorrer os itens por essa ordem:
 *        - Se o item couber na capacidade restante → inserir
 *        - Se não couber → saltar
 *   3. Parar quando todos os itens forem analisados
 *
 * COMPLEXIDADE:
 *   Temporal: O(n log n) — dominada pela ordenação
 *   Espacial: O(n) — cópia da lista de itens
 *
 * DISTINÇÃO FACE A [C1] GreedyByDensity (importante para a defesa):
 *
 *   C1 ordena por valor/peso → considera a EFICIÊNCIA do item
 *   C2 ordena por valor      → considera apenas o BENEFÍCIO absoluto
 *
 *   Exemplo onde C2 falha:
 *     Item A: valor=100, peso=40 → C2 escolhe primeiro
 *     Item B: valor=70,  peso=10 → C2 escolhe a seguir (se couber)
 *     Item C: valor=60,  peso=10 → pode não caber se A+B já esgotaram a cap.
 *     Com capacidade=50: C2 escolhe A(40)+B(10) = valor 170
 *                        C1 escolhe B(10)+C(10)+... → pode obter mais valor
 *
 *   C2 tende a desperdiçar capacidade com itens pesados de alto valor,
 *   deixando de fora itens leves com boa relação valor/peso.
 *   Ter ambos permite comparar experimentalmente o impacto do critério
 *   de ordenação na qualidade da solução construtiva.
 *
 * LIMITAÇÕES:
 *   - Ignora completamente o peso na fase de ordenação
 *   - Geralmente produz soluções de qualidade inferior ao C1
 *   - Útil como baseline de comparação e ponto de partida alternativo
 */

public class GreedyByValue {
    public static List<KnapsackItem> solve(List<KnapsackItem> items, long capacity) {
        List<KnapsackItem> selected = new ArrayList<>();
        List<KnapsackItem> sortedItems = new ArrayList<>(items);
        
        // Ordenar por valor decrescente
        sortedItems.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

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