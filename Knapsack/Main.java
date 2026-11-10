import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Classe principal para testar e avaliar diferentes heurísticas 
 * aplicadas ao Problema da Mochila (Knapsack Problem - KP).
 */
public class Main {

    // Define o número de vezes que o algoritmo PL2 (Hill Climbing) vai reiniciar
    private static final int HCRR_RESTARTS = 30;

    public static void main(String[] args) {

        // ==========================================================
        // 1. MAPA DE SOLUÇÕES ÓTIMAS (SO)
        // ==========================================================
        // Dicionário que mapeia o nome de cada instância para a sua Solução Ótima (conhecida previamente).
        // Utiliza-se o sufixo 'L' porque os valores ultrapassam o limite do tipo inteiro (int).
        Map<String, Long> solucoesOtimas = new HashMap<>();
        solucoesOtimas.put("n_1000_1", 9999946233L);
        solucoesOtimas.put("n_1000_2", 9999964987L);
        solucoesOtimas.put("n_1000_3", 9999229281L);
        solucoesOtimas.put("n_1000_4", 9999239905L);
        solucoesOtimas.put("n_1000_5", 9999251796L);
        solucoesOtimas.put("n_1000_6", 9996100344L);
        solucoesOtimas.put("n_1000_7", 9996105266L);
        solucoesOtimas.put("n_1000_8", 9996111502L);
        solucoesOtimas.put("n_1000_9", 9980488131L);
        solucoesOtimas.put("n_1000_10", 9980507700L);

        // ==========================================================
        // 2. LEITURA DA PASTA DE INSTÂNCIAS
        // ==========================================================
        File folder = new File("instancias");
        File[] listOfFiles = folder.listFiles();

        // Validação de segurança: verifica se a pasta existe e contém ficheiros
        if (listOfFiles == null || listOfFiles.length == 0) {
            System.out.println("ERRO: A pasta 'instancias' nao foi encontrada ou esta vazia!");
            return;
        }

        // Preparar as variáveis (StringBuilders) para guardar as linhas de cada tabela em memória
        StringBuilder tabela1 = new StringBuilder();
        StringBuilder tabela2 = new StringBuilder();

        // ==========================================================
        // 3. PROCESSAMENTO DE CADA INSTÂNCIA
        // ==========================================================
        for (File file : listOfFiles) {
            if (file.isFile()) {
                // Limpa o nome do ficheiro removendo a extensão ".txt" para bater certo com o Mapa
                String nomeLimpo = file.getName().replace(".txt", ""); 
                
                // Vai buscar a SO. Se não existir no mapa, devolve -1
                long so = solucoesOtimas.getOrDefault(nomeLimpo, -1L);
                
                // Lê os dados do ficheiro (itens e capacidade)
                InstanciaMochila inst = lerFicheiro(file);
                if (inst.items.isEmpty()) continue; // Ignora ficheiros mal formatados ou vazios

                // ----------------------------------------------------------
                // AVALIAÇÃO 1: [C1] Greedy By Density + [PL1] Local Search
                // ----------------------------------------------------------
                long startTime = System.nanoTime(); // Inicia o cronómetro

                // 1. Executa o construtivo primeiro (o tempo ainda não conta)
                List<KnapsackItem> solC1 = GreedyByDensity.solve(inst.items, inst.capacity);
                long custoC1 = getTotalValue(solC1);

                // 2. Inicia o cronómetro APENAS para a pesquisa local
                long startTimePL1 = System.nanoTime();
                List<KnapsackItem> solPL1 = LocalSearch.solve(inst.items, solC1, inst.capacity, "Densidade");
                long tempoPL1 = System.nanoTime() - startTimePL1; // Para o cronómetro

                long sePL1 = getTotalValue(solPL1);
                
                // Guarda a linha formatada para a Tabela 1
                tabela1.append(String.format("%-15s | %-15s | %-15s | %-15s | %-10s | %-15d%n", 
                        nomeLimpo, (so == -1 ? "N/A" : so), custoC1, sePL1, calcularDesvio(so, sePL1), tempoPL1));

                // ----------------------------------------------------------
                // AVALIAÇÃO 2: [C2] Greedy By Value + [PL2] Random Restart
                // ----------------------------------------------------------
                startTime = System.nanoTime(); // Reinicia o cronómetro

                // 1. Executa o construtivo (apenas para registar o valor inicial na tabela)
                List<KnapsackItem> solC2 = GreedyByValue.solve(inst.items, inst.capacity);
                long custoC2 = getTotalValue(solC2);

                // 2. Inicia o cronómetro APENAS para o Hill Climbing
                long startTimePL2 = System.nanoTime();
                List<KnapsackItem> solPL2 = HillClimbingRandomRestart.solve(inst.items, inst.capacity, HCRR_RESTARTS);
                long tempoPL2 = System.nanoTime() - startTimePL2; // Para o cronómetro

                long sePL2 = getTotalValue(solPL2);
                // Guarda a linha formatada para a Tabela 2
                tabela2.append(String.format("%-15s | %-15s | %-15s | %-15s | %-10s | %-15d%n", 
                        nomeLimpo, (so == -1 ? "N/A" : so), custoC2, sePL2, calcularDesvio(so, sePL2), tempoPL2));
            }
        }

        // ==========================================================
        // 4. IMPRESSÃO DOS RESULTADOS FINAIS
        // ==========================================================
        String cabecalho = "=======================================================================================================\n" +
                           String.format("%-15s | %-15s | %-15s | %-15s | %-10s | %-15s%n", "Instancia", "SO", "Sol. Inicial", "SE (Pesquisa)", "% Desvio", "Tempo (ns)") +
                           "=======================================================================================================";

        // Imprime Tabela 1
        System.out.println("\n\n>>> TABELA 1: [C1] Greedy By Density + [PL1] Local Search (First-Improvement) <<<");
        System.out.println(cabecalho);
        System.out.print(tabela1.toString());
        System.out.println("-------------------------------------------------------------------------------------------------------");

        // Imprime Tabela 2
        System.out.println("\n\n>>> TABELA 2: [C2] Greedy By Value + [PL2] Hill Climbing Random Restart <<<");
        System.out.println(cabecalho);
        System.out.print(tabela2.toString());
        System.out.println("-------------------------------------------------------------------------------------------------------\n");
    }

    /**
     * Calcula a percentagem de desvio da solução encontrada em relação à solução ótima.
     * Fórmula: ((SO - SE) / SO) * 100
     */
    private static String calcularDesvio(long so, long se) {
        if (so <= 0) return "N/A";
        double desvio = ((double)(so - se) / so) * 100.0;
        return String.format(java.util.Locale.US, "%.4f%%", desvio); 
    }

    /**
     * Método auxiliar que calcula o valor total (soma) de uma lista de itens da mochila.
     */
    private static long getTotalValue(List<KnapsackItem> items) {
        long total = 0;
        for (KnapsackItem item : items) total += item.getValue();
        return total;
    }

    /**
     * Faz o parse (leitura) de um ficheiro de texto convertendo as linhas em objetos KnapsackItem.
     * Ignora a primeira linha (quantidade total) e define uma capacidade fixa de 10 mil milhões.
     */
    private static InstanciaMochila lerFicheiro(File ficheiro) {
        List<KnapsackItem> items = new ArrayList<>();
        long capacity = 10000000000L; // Capacidade injetada manualmente de acordo com a especificação da instância
        
        try (Scanner scanner = new Scanner(ficheiro)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Descarta a primeira linha (ex: "1000")
                
                // Lê linha a linha até o final do ficheiro
                while (scanner.hasNextLine()) {
                    String linha = scanner.nextLine().trim();
                    if (linha.isEmpty()) continue;
                    
                    // Separa a linha em elementos usando espaços em branco como delimitadores
                    String[] partes = linha.split("\\s+");
                    
                    // Assegura que há pelo menos ID, Valor e Peso na linha
                    if (partes.length >= 3) {
                        int id = Integer.parseInt(partes[0]);
                        long valor = Long.parseLong(partes[1]);
                        long peso = Long.parseLong(partes[2]);
                        
                        items.add(new KnapsackItem(id, valor, peso));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler o ficheiro: " + e.getMessage());
        }
        
        return new InstanciaMochila(items, capacity);
    }

    /**
     * Classe auxiliar estática para retornar os itens lidos e a capacidade de uma só vez
     * a partir do método lerFicheiro.
     */
    static class InstanciaMochila {
        List<KnapsackItem> items;
        long capacity;
        
        public InstanciaMochila(List<KnapsackItem> items, long capacity) {
            this.items = items;
            this.capacity = capacity;
        }
    }
}