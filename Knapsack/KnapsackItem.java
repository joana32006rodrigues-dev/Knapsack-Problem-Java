/**
 * Representação de um item do Problema da Mochila (Knapsack Problem).
 *
 * PARA A DEFESA:
 *   Cada item tem três atributos:
 *     - id     : identificador único (para comparação sem ambiguidade)
 *     - value  : benefício obtido ao incluir o item na mochila (v_i)
 *     - weight : espaço que o item ocupa na mochila (w_i)
 *
 *   A densidade é calculada como value/weight e representa o "retorno por unidade
 *   de capacidade" — o critério central da heurística Greedy por Densidade.
 *
 *   A variável de decisão x_i ∈ {0,1} não está aqui explicitamente: um item
 *   está "selecionado" (x_i = 1) se e só se estiver na lista de itens escolhidos.
 */
public class KnapsackItem {

    private int  id;     // identificador único do item
    private long  value;  // valor (benefício) do item — v_i na formulação matemática
    private long  weight; // peso do item — w_i na formulação matemática

    /**
     * Cria um item com os atributos indicados.
     *
     * @param id     identificador único
     * @param value  valor/benefício do item (v_i)
     * @param weight peso do item (w_i)
     */
    public KnapsackItem(int id, long value, long  weight) {
        this.id     = id;
        this.value  = value;
        this.weight = weight;
    }

    public int getId()     { return id; }
    public long getValue()  { return value; }
    public long getWeight() { return weight; }

    /**
     * Calcula a densidade de valor: razão valor/peso.
     * Usada pelo GreedyByDensity para ordenar os itens.
     * Quanto maior a densidade, mais "eficiente" é o item.
     */
    public double getDensity() {
        return (double) value / weight;
    }

    @Override
    public String toString() {
        return String.format("Item{id=%d, value=%d, weight=%d} (Density: %.2f)",
                id, value, weight, getDensity());
    }
}
