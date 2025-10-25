package Final;
import java.util.ArrayList;
import java.util.Arrays; 
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class PizzariaApp {

    // --- CLASSES DE MODELO ---

    public enum Sabor {
        MARGUERITA(30.00), PEPPERONI(35.00), QUATRO_QUEIJOS(37.00),
        CALABRESA(33.00), FRANGO_CATUPIRY(36.50), PORTUGUESA(33.40),
        MUSSARELA(28.00), ATUM(38.70), VEGETARIANA(34.30),
        ESPECIAL_CASA(42.20);

        private final double precoBase;

        Sabor(double precoBase) {
            this.precoBase = precoBase;
        }
        // Métodos de Sabor... (mantidos)
        public double getPrecoBase() { return precoBase; }
        public String getNomeFormatado() { return name().replace('_', ' ').toLowerCase(); }
        public static Sabor fromIndex(int index) {
            Sabor[] sabores = Sabor.values();
            if (index >= 0 && index < sabores.length) {
                return sabores[index];
            }
            throw new IllegalArgumentException("Índice de sabor inválido.");
        }
    }
    
    // NOVO: Enum para Bebidas
    public enum Bebida {
        COCA_COLA_2L(12.00), GUARANA_2L(10.50), FANTA_2L(10.00);

        private final double preco;

        Bebida(double preco) {
            this.preco = preco;
        }

        public double getPreco() { return preco; }
        
        public String getNomeFormatado() {
            return name().replace('_', ' ').toLowerCase();
        }
        
        public static Bebida fromIndex(int index) {
            Bebida[] bebidas = Bebida.values();
            if (index >= 0 && index < bebidas.length) {
                return bebidas[index];
            }
            throw new IllegalArgumentException("Índice de bebida inválido.");
        }
    }

    public static class Cliente {
        private final String nome;
        private final String endereco;
        private final String telefone;
        private final String email;

        public Cliente(String nome, String endereco, String telefone, String email){
            this.nome = nome;
            this.endereco = endereco;
            this.telefone = telefone;
            this.email = email;
        }

        public String getNome() { return nome; }
        public String getEndereco() { return endereco; }
        public String getTelefone() { return telefone; }
        public String getEmail() { return email; }
        
        @Override
        public String toString() {
            return String.format("%s | End: %s | Tel: %s", nome, endereco, telefone);
        }
    }

    public static class Pizza {
        private final List<Sabor> sabores;
        private final double preco;
        private final TamanhoPizza tamanho;

        public enum TamanhoPizza {
            BROTO(0.7), GRANDE(1.0), GIGA(1.3);
            
            private final double fatorPreco;

            TamanhoPizza(double fatorPreco) {
                this.fatorPreco = fatorPreco;
            }

            public double getFatorPreco() { return fatorPreco; }

            public static TamanhoPizza getByIndex(int index) {
                TamanhoPizza[] tamanhos = TamanhoPizza.values();
                if (index >= 0 && index < tamanhos.length) {
                    return tamanhos[index];
                }
                throw new IllegalArgumentException("Índice de tamanho inválido.");
            }
        }

        public Pizza(List<Sabor> sabores, TamanhoPizza tamanho){
            if (sabores == null || sabores.isEmpty()) {
                 throw new IllegalArgumentException("A pizza deve ter pelo menos um sabor.");
            }
            this.sabores = new ArrayList<>(sabores);
            this.tamanho = tamanho;
            this.preco = calcularPreco(sabores, tamanho);
        }
        
        private double calcularPreco(List<Sabor> sabores, TamanhoPizza tamanho) {
            double precoMaximo = sabores.stream()
                .mapToDouble(Sabor::getPrecoBase)
                .max().orElse(0.0);
            
            return Math.round((precoMaximo * tamanho.getFatorPreco()) * 100.0) / 100.0;
        }

        public List<Sabor> getSabores() { return Collections.unmodifiableList(sabores); }
        public double getPreco() { return preco; }
        public TamanhoPizza getTamanho() { return tamanho; }
        
        @Override
        public String toString() {
            String saboresStr = sabores.stream()
                .map(Sabor::getNomeFormatado)
                .collect(Collectors.joining(", "));
            
            return String.format("%s (R$ %.2f) - Sabores: [%s]", 
                                 tamanho.name(), preco, saboresStr);
        }
    }
    
    public static class Pedido {
        private final int id;
        private final Cliente cliente;
        private final List<Pizza> pizzas; 
        private final List<Bebida> bebidas; // NOVO: Lista de Bebidas
        private final double frete;
        private final double valorTotal;

        public Pedido(int id, Cliente cliente, List<Pizza> pizzas, List<Bebida> bebidas, double frete){
            this.id = id;
            this.cliente = cliente;
            this.pizzas = new LinkedList<>(pizzas); 
            this.bebidas = new LinkedList<>(bebidas); // Inicializa bebidas
            this.frete = frete;
            this.valorTotal = recalcularTotal();
        }
        
        private double somarPizzas(List<Pizza> pizzas) {
            return pizzas.stream().mapToDouble(Pizza::getPreco).sum();
        }
        
        // NOVO: Soma o valor das bebidas
        private double somarBebidas(List<Bebida> bebidas) {
            return bebidas.stream().mapToDouble(Bebida::getPreco).sum();
        }
        
        public double recalcularTotal() {
            double novoValorPizzas = somarPizzas(this.pizzas);
            double novoValorBebidas = somarBebidas(this.bebidas); // Inclui bebidas
            return Math.round((novoValorPizzas + novoValorBebidas + this.frete) * 100.0) / 100.0;
        }

        public int getId() { return id; }
        public Cliente getCliente() { return cliente; }
        public List<Pizza> getPizzas() { return pizzas; } 
        public List<Bebida> getBebidas() { return bebidas; } // Getter
        public double getValorTotal() { return valorTotal; }
        public double getFrete() { return frete; }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pedido other = (Pedido) obj;
            return id == other.id;
        }
        
        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }
    
    public static class PedidoNaoEncontradoException extends RuntimeException {
        public PedidoNaoEncontradoException(String message) {
            super(message);
        }
    }

    // --- LÓGICA DE NEGÓCIO (PizzariaService) ---
    public static class PizzariaService {
        private final List<Cliente> listaClientes;
        private final List<Pedido> listaPedidos;
        private int proximoIdPedido;

        private static final double CUSTO_BASE_KM = 1.80; 
        private static final double FATOR_PESO_PIZZA = 0.60;
        private static final double FATOR_PESO_BEBIDA = 0.20; // NOVO: Fator de peso para bebidas

        public PizzariaService() {
            this.listaClientes = new LinkedList<>();
            this.listaPedidos = new LinkedList<>();
            this.proximoIdPedido = 1;
        }
        
        public List<Cliente> getListaClientes() {
            return Collections.unmodifiableList(listaClientes);
        }

        public List<Pedido> getListaPedidos() {
            return Collections.unmodifiableList(listaPedidos);
        }

        public void adicionarCliente(Cliente cliente) {
            listaClientes.add(cliente);
        }
        
        // NOVO: Busca cliente por nome
        public Optional<Cliente> buscarClientePorNome(String nomeBusca) {
            String nomeNormalizado = nomeBusca.trim().toLowerCase();
            return listaClientes.stream()
                    .filter(c -> c.getNome().toLowerCase().contains(nomeNormalizado))
                    .findFirst();
        }

        // Frete agora considera Pizzas e Bebidas
        public double calcularFrete(double distanciaKm, int numPizzas, int numBebidas) {
            if (distanciaKm < 0 || (numPizzas + numBebidas) <= 0) return 0.0;
            double custoDistancia = distanciaKm * CUSTO_BASE_KM;
            double custoPeso = (numPizzas * FATOR_PESO_PIZZA) + (numBebidas * FATOR_PESO_BEBIDA);
            return Math.round((custoDistancia + custoPeso) * 100.0) / 100.0;
        }

        // Criar Pedido agora recebe a lista de Bebidas
        public Pedido criarPedido(Cliente cliente, List<Pizza> pizzas, List<Bebida> bebidas, double distanciaKm) {
            if (pizzas.isEmpty() && bebidas.isEmpty()) throw new IllegalArgumentException("O pedido está vazio.");
            
            double frete = calcularFrete(distanciaKm, pizzas.size(), bebidas.size());
            Pedido novoPedido = new Pedido(proximoIdPedido++, cliente, pizzas, bebidas, frete);
            listaPedidos.add(novoPedido);
            return novoPedido;
        }

        public Pedido buscarPedido(int id) {
            return listaPedidos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido ID " + id + " não encontrado."));
        }
        
        // Atualiza a lista de pedidos, criando um novo objeto Pedido para refletir as alterações
        private Pedido atualizarPedidoNaLista(Pedido pedidoAntigo) {
            // Re-calcula o frete, caso o número de itens tenha mudado
            double novoFrete = calcularFrete(pedidoAntigo.getFrete() / 
                                              (pedidoAntigo.getPizzas().size() * FATOR_PESO_PIZZA + pedidoAntigo.getBebidas().size() * FATOR_PESO_BEBIDA + 0.0001) * CUSTO_BASE_KM / CUSTO_BASE_KM,
                                              pedidoAntigo.getPizzas().size(),
                                              pedidoAntigo.getBebidas().size());
                                              
            Pedido pedidoAtualizado = new Pedido(
                pedidoAntigo.getId(),
                pedidoAntigo.getCliente(),
                pedidoAntigo.getPizzas(), 
                pedidoAntigo.getBebidas(),
                novoFrete // Usa o frete recalculado
            );
            
            int index = listaPedidos.indexOf(pedidoAntigo);
            if (index != -1) {
                listaPedidos.set(index, pedidoAtualizado);
                return pedidoAtualizado;
            }
            throw new PedidoNaoEncontradoException("Falha ao atualizar o pedido na lista.");
        }

        public Pedido adicionarPizzaAoPedido(Pedido pedido, Pizza novaPizza) {
            pedido.getPizzas().add(novaPizza);
            return atualizarPedidoNaLista(pedido);
        }
        
        public Pedido adicionarBebidaAoPedido(Pedido pedido, Bebida novaBebida) {
            pedido.getBebidas().add(novaBebida);
            return atualizarPedidoNaLista(pedido);
        }

        public Pedido removerPizzaDoPedido(Pedido pedido, int indicePizza) {
            if (indicePizza < 0 || indicePizza >= pedido.getPizzas().size()) {
                throw new IndexOutOfBoundsException("Índice de pizza inválido.");
            }
            pedido.getPizzas().remove(indicePizza);
            
            if (pedido.getPizzas().isEmpty() && pedido.getBebidas().isEmpty()) {
                listaPedidos.remove(pedido);
                throw new PedidoNaoEncontradoException("O pedido ficou vazio e foi cancelado.");
            }

            return atualizarPedidoNaLista(pedido);
        }
        
        public Pedido removerBebidaDoPedido(Pedido pedido, int indiceBebida) {
            if (indiceBebida < 0 || indiceBebida >= pedido.getBebidas().size()) {
                throw new IndexOutOfBoundsException("Índice de bebida inválido.");
            }
            pedido.getBebidas().remove(indiceBebida);
            
            if (pedido.getPizzas().isEmpty() && pedido.getBebidas().isEmpty()) {
                listaPedidos.remove(pedido);
                throw new PedidoNaoEncontradoException("O pedido ficou vazio e foi cancelado.");
            }

            return atualizarPedidoNaLista(pedido);
        }
        
        public Pedido alterarSaborPizza(Pedido pedido, int indicePizza, List<Sabor> novosSabores) {
            if (indicePizza < 0 || indicePizza >= pedido.getPizzas().size()) {
                throw new IndexOutOfBoundsException("Índice de pizza inválido.");
            }
            
            Pizza pizzaAntiga = pedido.getPizzas().get(indicePizza);
            Pizza novaPizza = new Pizza(novosSabores, pizzaAntiga.getTamanho());
            
            pedido.getPizzas().set(indicePizza, novaPizza);

            return atualizarPedidoNaLista(pedido);
        }
        
        public void gerarRelatorio() {
            if (listaPedidos.isEmpty()) {
                System.out.println("Nenhuma venda registrada para gerar o relatório.");
                return;
            }

            // (Lógica de Relatório mantida, com ênfase em Pizzas e Sabores)
            double faturamentoTotal = listaPedidos.stream().mapToDouble(Pedido::getValorTotal).sum();
            
            System.out.println("--- RELATÓRIO DE VENDAS COMPLETO ---");
            System.out.println("FATURAMENTO TOTAL: R$ " + String.format("%.2f", faturamentoTotal));
            System.out.println("------------------------------------");

            Map<Sabor, Integer> contagemSabores = new HashMap<>();
            Map<Bebida, Integer> contagemBebidas = new HashMap<>(); // NOVO: Contagem de bebidas
            GrafoSabor grafo = new GrafoSabor();
            
            for (Pedido pedido : listaPedidos) {
                for (Pizza pizza : pedido.getPizzas()) {
                    grafo.adicionarSabores(pizza.getSabores());
                    for (Sabor s : pizza.getSabores()) {
                        contagemSabores.put(s, contagemSabores.getOrDefault(s, 0) + 1);
                    }
                }
                // NOVO: Contagem de bebidas
                for (Bebida b : pedido.getBebidas()) {
                    contagemBebidas.put(b, contagemBebidas.getOrDefault(b, 0) + 1);
                }
            }

            // TOP 5 Sabores (mantido)
            Map<Sabor, Integer> topSabores = contagemSabores.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(5)
                    .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));
            
            System.out.println("\nTOP 5 SABORES MAIS PEDIDOS:");
            topSabores.forEach((sabor, count) -> 
                System.out.println("  > " + sabor.getNomeFormatado() + " (" + count + " vezes)")
            );
            
            // NOVO: TOP Bebidas
            Map<Bebida, Integer> topBebidas = contagemBebidas.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .collect(Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));
                    
            System.out.println("\nBEBIDAS MAIS PEDIDAS:");
            topBebidas.forEach((bebida, count) -> 
                System.out.println("  > " + bebida.getNomeFormatado() + " (" + count + " vezes)")
            );
            
            System.out.println("\nANÁLISE DE CONEXÕES (GRAFO DE CO-OCORRÊNCIA):");
            grafo.exibirConexoes();
            System.out.println("------------------------------------");
        }

        // Classe GrafoSabor (mantida)
        private static class GrafoSabor {
            private final Map<Sabor, Map<Sabor, Integer>> conexoes;

            public GrafoSabor() {
                this.conexoes = new HashMap<>();
                for (Sabor s : Sabor.values()) {
                    conexoes.put(s, new HashMap<>());
                }
            }

            public void adicionarSabores(List<Sabor> sabores) {
                for (int i = 0; i < sabores.size(); i++) {
                    Sabor s1 = sabores.get(i);
                    for (int j = i + 1; j < sabores.size(); j++) {
                        Sabor s2 = sabores.get(j);
                        
                        conexoes.get(s1).put(s2, conexoes.get(s1).getOrDefault(s2, 0) + 1);
                        conexoes.get(s2).put(s1, conexoes.get(s2).getOrDefault(s1, 0) + 1);
                    }
                }
            }

            public void exibirConexoes() {
                Set<String> paresImpressos = new HashSet<>();
                
                conexoes.forEach((s1, vizinhos) -> vizinhos.forEach((s2, count) -> {
                    if (count >= 1) { 
                        String key = s1.compareTo(s2) < 0 ? s1.name() + s2.name() : s2.name() + s1.name();
                        
                        if (!paresImpressos.contains(key)) {
                            System.out.printf("  - %s e %s: %d vezes\n", 
                                s1.getNomeFormatado(), s2.getNomeFormatado(), count);
                            paresImpressos.add(key);
                        }
                    }
                }));
            }
        }
    }

    // --- INTERFACE DE USUÁRIO (Menu Principal) ---

    private static final PizzariaService SERVICE = new PizzariaService();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        popularDadosIniciais();

        boolean continuar = true;
        while (continuar) {
            try {
                exibirMenuPrincipal();
                int opcao = lerInteiro(scanner);
                
                switch (opcao) {
                    case 1: fazerPedido(scanner); break;
                    case 2: menuAlterarPedido(scanner); break;
                    case 3: adicionarClienteMenu(scanner); break;
                    case 4: SERVICE.gerarRelatorio(); aguardarConfirmacao(scanner); break;
                    case 5: gerarListaClientes(scanner); break;
                    case 6: gerarListaPedidos(scanner); break;
                    case 9:
                        System.out.println("Sistema encerrado. Obrigado!");
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        aguardarConfirmacao(scanner);
                }
            } catch (PedidoNaoEncontradoException | IndexOutOfBoundsException | IllegalArgumentException e) {
                System.err.println("ERRO: " + e.getMessage());
                aguardarConfirmacao(scanner); 
            } catch (InputMismatchException e) {
                System.err.println("ERRO: Entrada inválida. Esperava um número.");
                scanner.nextLine(); 
                aguardarConfirmacao(scanner); 
            } catch (Exception e) {
                System.err.println("ERRO INESPERADO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace(); // Para debug em erro inesperado
                aguardarConfirmacao(scanner); 
            }
        }
        scanner.close();
    }
    
    // --- Métodos de Utilidade e Interface ---
    
    private static void aguardarConfirmacao(Scanner scanner) {
        System.out.println("\n--- Pressione ENTER para voltar ao Menu Principal ---");
        scanner.nextLine(); 
    }

    private static void popularDadosIniciais() {
        SERVICE.adicionarCliente(new Cliente("Ana Silva", "Rua das Flores, 10", "98888-1111", "ana@ex.com"));
        SERVICE.adicionarCliente(new Cliente("Bruno Costa", "Av. Central, 50", "97777-2222", "bruno@ex.com"));

        Cliente c1 = SERVICE.getListaClientes().get(0);
        List<Pizza> p1 = new ArrayList<>();
        p1.add(new Pizza(Arrays.asList(Sabor.PEPPERONI, Sabor.MUSSARELA), Pizza.TamanhoPizza.GRANDE));
        p1.add(new Pizza(Arrays.asList(Sabor.QUATRO_QUEIJOS), Pizza.TamanhoPizza.BROTO));
        List<Bebida> b1 = new ArrayList<>();
        b1.add(Bebida.COCA_COLA_2L);
        SERVICE.criarPedido(c1, p1, b1, 3.5);

        Cliente c2 = SERVICE.getListaClientes().get(1);
        List<Pizza> p2 = new ArrayList<>();
        p2.add(new Pizza(Arrays.asList(Sabor.FRANGO_CATUPIRY, Sabor.MUSSARELA), Pizza.TamanhoPizza.GIGA));
        p2.add(new Pizza(Arrays.asList(Sabor.PEPPERONI, Sabor.CALABRESA), Pizza.TamanhoPizza.GRANDE));
        p2.add(new Pizza(Arrays.asList(Sabor.MARGUERITA), Pizza.TamanhoPizza.GRANDE));
        List<Bebida> b2 = new ArrayList<>();
        b2.add(Bebida.GUARANA_2L);
        b2.add(Bebida.GUARANA_2L);
        b2.add(Bebida.FANTA_2L);
        SERVICE.criarPedido(c2, p2, b2, 8.0);
        
        System.out.println("--- Dados Iniciais Populares Carregados (2 Clientes, 2 Pedidos, com Bebidas) ---");
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n====================================");
        System.out.println("|      PIZZARIA - MENU PRINCIPAL   |");
        System.out.println("====================================");
        System.out.println("1. Fazer um Novo Pedido");
        System.out.println("2. Alterar Pedido Existente");
        System.out.println("3. Adicionar Cliente");
        System.out.println("4. Gerar Relatório de Vendas (Grafo)");
        System.out.println("5. Exibir Lista de Clientes");
        System.out.println("6. Exibir Lista de Pedidos");
        System.out.println("9. Sair");
        System.out.print("Sua Opção: ");
    }
    
    private static int lerInteiro(Scanner scanner) {
        int valor = scanner.nextInt();
        scanner.nextLine(); 
        return valor;
    }
    
    private static double lerDouble(Scanner scanner) {
        double valor = scanner.nextDouble();
        scanner.nextLine();
        return valor;
    }

    /**
     * NOVO: Procura cliente por nome e oferece a opção de cadastrar se não encontrado.
     */
    private static Cliente procurarOuCriarCliente(Scanner scanner) {
        while (true) {
            System.out.println("\n--- SELECIONAR CLIENTE ---");
            System.out.print("Digite o NOME do cliente: ");
            String nomeBusca = scanner.nextLine().trim();

            if (nomeBusca.isEmpty()) {
                System.out.println("O nome não pode ser vazio.");
                continue;
            }

            Optional<Cliente> clienteOpt = SERVICE.buscarClientePorNome(nomeBusca);

            if (clienteOpt.isPresent()) {
                Cliente clienteEncontrado = clienteOpt.get();
                System.out.println("✅ Cliente encontrado: " + clienteEncontrado.getNome());
                return clienteEncontrado;
            } else {
                System.out.println("Cliente não encontrado.");
                System.out.print("Deseja cadastrar um novo cliente com o nome '" + nomeBusca + "'? (1-Sim, 2-Não): ");
                if (lerInteiro(scanner) == 1) {
                    return adicionarClienteMenu(scanner, nomeBusca);
                } else {
                    System.out.println("Tentando novamente a busca...");
                }
            }
        }
    }
    
    private static List<Sabor> selecionarSabores(Scanner scanner, int quantiSabores) {
        List<Sabor> saboresSelect = new ArrayList<>();
        Sabor[] saboresDisponiveis = Sabor.values();

        System.out.println("\n--- CARDÁPIO ---");
        for (int j = 0; j < saboresDisponiveis.length; j++) {
            System.out.printf("%d - %s (R$ %.2f)\n", 
                (j + 1), saboresDisponiveis[j].getNomeFormatado(), saboresDisponiveis[j].getPrecoBase());
        }
        
        for (int i = 0; i < quantiSabores; i++) {
            System.out.printf("Selecione o %dº sabor: ", (i + 1));
            int opcao = lerInteiro(scanner);

            saboresSelect.add(Sabor.fromIndex(opcao - 1));
        }
        return saboresSelect;
    }

    private static Pizza.TamanhoPizza selecionarTamanho(Scanner scanner) {
        System.out.println("\n--- SELECIONAR TAMANHO ---");
        Pizza.TamanhoPizza[] tamanhos = Pizza.TamanhoPizza.values();
        for (int i = 0; i < tamanhos.length; i++) {
            System.out.printf("%d - %s\n", (i + 1), tamanhos[i].name());
        }
        System.out.print("Opção: ");
        int tamanhoIndex = lerInteiro(scanner);

        return Pizza.TamanhoPizza.getByIndex(tamanhoIndex - 1);
    }
    
    // NOVO: Fluxo para adicionar bebidas
    private static List<Bebida> adicionarBebidasAoPedido(Scanner scanner) {
        List<Bebida> bebidas = new ArrayList<>();
        Bebida[] bebidasDisponiveis = Bebida.values();
        boolean adicionarMais = true;

        System.out.println("\n--- SELECIONAR BEBIDAS (2L) ---");
        
        while (adicionarMais) {
            System.out.println("\nBebidas disponíveis:");
            for (int i = 0; i < bebidasDisponiveis.length; i++) {
                System.out.printf("%d - %s (R$ %.2f)\n", 
                    (i + 1), bebidasDisponiveis[i].getNomeFormatado(), bebidasDisponiveis[i].getPreco());
            }
            System.out.println("0 - Nenhuma/Próxima Etapa");
            System.out.print("Opção: ");
            
            int opcao = lerInteiro(scanner);

            if (opcao == 0) {
                adicionarMais = false;
            } else if (opcao > 0 && opcao <= bebidasDisponiveis.length) {
                Bebida bebida = Bebida.fromIndex(opcao - 1);
                bebidas.add(bebida);
                System.out.println("  > " + bebida.getNomeFormatado() + " adicionada.");
            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        }
        return bebidas;
    }

    private static void fazerPedido(Scanner scanner) {
        try {
            System.out.println("\n--- FAZER NOVO PEDIDO ---");
            Cliente cliente = procurarOuCriarCliente(scanner);
            
            List<Pizza> pizzas = new ArrayList<>();
            boolean adicionarMaisPizza = true;
            
            while (adicionarMaisPizza) {
                Pizza.TamanhoPizza tamanho = selecionarTamanho(scanner);

                int quantiSabores = 0;
                while (quantiSabores < 1 || quantiSabores > 4) {
                    System.out.print("Quantos sabores (1 a 4)? ");
                    quantiSabores = lerInteiro(scanner);
                }

                List<Sabor> saboresSelect = selecionarSabores(scanner, quantiSabores);
                Pizza pizza = new Pizza(saboresSelect, tamanho);
                pizzas.add(pizza);
                
                System.out.println("  > Pizza adicionada: " + pizza);
                
                System.out.print("Adicionar mais pizzas? (1-Sim, 2-Não): ");
                if(lerInteiro(scanner) != 1) adicionarMaisPizza = false;
            }
            
            List<Bebida> bebidas = adicionarBebidasAoPedido(scanner); // Adiciona bebidas

            System.out.print("\nDigite a distância para entrega em KM (ex: 5.5): ");
            double distanciaKm = lerDouble(scanner);

            Pedido pedido = SERVICE.criarPedido(cliente, pizzas, bebidas, distanciaKm); 
            
            System.out.println("\n✅ Pedido #" + pedido.getId() + " concluído para " + cliente.getNome() + "!");
            System.out.println(String.format("   Frete: R$ %.2f | Valor Total: R$ %.2f", pedido.getFrete(), pedido.getValorTotal()));
        } finally {
            aguardarConfirmacao(scanner);
        }
    }

    private static void menuAlterarPedido(Scanner scanner) {
        Pedido pedido = null;
        try {
            System.out.println("\n--- ALTERAR PEDIDO ---");
            System.out.print("Digite o ID do pedido: ");
            int id = lerInteiro(scanner);
            
            pedido = SERVICE.buscarPedido(id);
            
            System.out.println(">> Pedido encontrado para " + pedido.getCliente().getNome() + ":");
            pedido.getPizzas().forEach(p -> System.out.println("   > " + p));
            pedido.getBebidas().forEach(b -> System.out.println("   > Bebida: " + b.getNomeFormatado()));
            System.out.println(String.format("   Total atual: R$ %.2f", pedido.getValorTotal()));
            
            System.out.println("\nSelecione a Ação:");
            System.out.println("1 - Adicionar nova pizza");
            System.out.println("2 - Remover pizza");
            System.out.println("3 - Alterar sabor de uma pizza");
            System.out.println("4 - Adicionar bebida");
            System.out.println("5 - Remover bebida");
            System.out.print("Opção: ");
            int opcao = lerInteiro(scanner);
            
            Pedido pedidoAtualizado = null;

            switch (opcao) {
                case 1: pedidoAtualizado = adicionarPizza(scanner, pedido); break;
                case 2: pedidoAtualizado = removerPizza(scanner, pedido); break;
                case 3: pedidoAtualizado = alterarSaborPizza(scanner, pedido); break;
                case 4: pedidoAtualizado = adicionarBebida(scanner, pedido); break; // NOVO
                case 5: pedidoAtualizado = removerBebida(scanner, pedido); break;  // NOVO
                default: System.out.println("Opção inválida."); return;
            }
            
            if (pedidoAtualizado != null) {
                System.out.println("✅ Operação concluída!");
                System.out.println(String.format("   Novo Total: R$ %.2f", pedidoAtualizado.getValorTotal()));
            }
        } finally {
            aguardarConfirmacao(scanner);
        }
    }
    
    private static Pedido adicionarPizza(Scanner scanner, Pedido pedido) {
        System.out.println("\n--- ADICIONAR PIZZA ---");
        Pizza.TamanhoPizza tamanho = selecionarTamanho(scanner);
        int quantiSabores = 0;
        while (quantiSabores < 1 || quantiSabores > 4) {
            System.out.print("Quantos sabores (1 a 4)? ");
            quantiSabores = lerInteiro(scanner);
        }
        List<Sabor> saboresSelect = selecionarSabores(scanner, quantiSabores);
        Pizza novaPizza = new Pizza(saboresSelect, tamanho);
        
        System.out.println("Adicionando: " + novaPizza);
        return SERVICE.adicionarPizzaAoPedido(pedido, novaPizza);
    }
    
    // NOVO: Método para adicionar bebida em pedido existente
    private static Pedido adicionarBebida(Scanner scanner, Pedido pedido) {
        Bebida[] bebidasDisponiveis = Bebida.values();
        
        System.out.println("\n--- ADICIONAR BEBIDA ---");
        for (int i = 0; i < bebidasDisponiveis.length; i++) {
            System.out.printf("%d - %s (R$ %.2f)\n", 
                (i + 1), bebidasDisponiveis[i].getNomeFormatado(), bebidasDisponiveis[i].getPreco());
        }
        System.out.print("Opção: ");
        
        int opcao = lerInteiro(scanner);

        if (opcao > 0 && opcao <= bebidasDisponiveis.length) {
            Bebida bebida = Bebida.fromIndex(opcao - 1);
            System.out.println("Adicionando: " + bebida.getNomeFormatado());
            return SERVICE.adicionarBebidaAoPedido(pedido, bebida);
        } else {
            throw new IllegalArgumentException("Opção de bebida inválida.");
        }
    }
    
    private static Pedido removerPizza(Scanner scanner, Pedido pedido) {
        System.out.println("\n--- REMOVER PIZZA ---");
        if (pedido.getPizzas().isEmpty()) {
            throw new IllegalArgumentException("O pedido não possui pizzas.");
        }
        
        System.out.println("Pizzas no Pedido " + pedido.getId() + ":");
        for (int i = 0; i < pedido.getPizzas().size(); i++) {
            System.out.println((i + 1) + " - " + pedido.getPizzas().get(i));
        }
        
        System.out.print("Digite o número da pizza para remover: ");
        int indice = lerInteiro(scanner);
        
        return SERVICE.removerPizzaDoPedido(pedido, indice - 1);
    }
    
    // NOVO: Método para remover bebida
    private static Pedido removerBebida(Scanner scanner, Pedido pedido) {
        System.out.println("\n--- REMOVER BEBIDA ---");
        if (pedido.getBebidas().isEmpty()) {
            throw new IllegalArgumentException("O pedido não possui bebidas.");
        }
        
        System.out.println("Bebidas no Pedido " + pedido.getId() + ":");
        for (int i = 0; i < pedido.getBebidas().size(); i++) {
            System.out.println((i + 1) + " - " + pedido.getBebidas().get(i).getNomeFormatado() 
                               + String.format(" (R$ %.2f)", pedido.getBebidas().get(i).getPreco()));
        }
        
        System.out.print("Digite o número da bebida para remover: ");
        int indice = lerInteiro(scanner);
        
        return SERVICE.removerBebidaDoPedido(pedido, indice - 1);
    }
    
    private static Pedido alterarSaborPizza(Scanner scanner, Pedido pedido) {
        System.out.println("\n--- ALTERAR SABOR ---");
        if (pedido.getPizzas().isEmpty()) {
            throw new IllegalArgumentException("O pedido não possui pizzas.");
        }
        
        System.out.println("Pizzas no Pedido " + pedido.getId() + ":");
        for (int i = 0; i < pedido.getPizzas().size(); i++) {
            System.out.println((i + 1) + " - " + pedido.getPizzas().get(i));
        }
        
        System.out.print("Digite o número da pizza para alterar: ");
        int indicePizza = lerInteiro(scanner);
        
        Pizza pizzaAntiga = pedido.getPizzas().get(indicePizza - 1);
        int quantiSabores = pizzaAntiga.getSabores().size();

        System.out.printf("\nAlterando %s com %d sabores...\n", pizzaAntiga.getTamanho().name(), quantiSabores);
        List<Sabor> novosSabores = selecionarSabores(scanner, quantiSabores);
        
        return SERVICE.alterarSaborPizza(pedido, indicePizza - 1, novosSabores);
    }

    /**
     * Adiciona cliente, opcionalmente preenchendo o nome com o valor da busca.
     * Retorna o cliente criado.
     */
    private static Cliente adicionarClienteMenu(Scanner scanner) {
        return adicionarClienteMenu(scanner, "");
    }
    
    private static Cliente adicionarClienteMenu(Scanner scanner, String nomePreenchido) {
        System.out.println("\n--- NOVO CADASTRO DE CLIENTE ---");
        String nome;
        if (nomePreenchido.isEmpty()) {
            System.out.print("Nome: "); 
            nome = scanner.nextLine();
        } else {
            nome = nomePreenchido;
            System.out.println("Nome: " + nome + " (Pré-preenchido)");
        }
        
        System.out.print("Endereço: "); String endereco = scanner.nextLine();
        System.out.print("Telefone: "); String telefone = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();

        Cliente cliente = new Cliente(nome, endereco, telefone, email);
        SERVICE.adicionarCliente(cliente);
        System.out.println("✅ Cliente " + nome + " adicionado com sucesso!");
        
        // Se chamado do menu principal, pausa. Se chamado do fluxo de pedido, não pausa aqui.
        if (nomePreenchido.isEmpty()) {
            aguardarConfirmacao(scanner); 
        }
        return cliente;
    }

    private static void gerarListaClientes(Scanner scanner) {
        List<Cliente> clientes = SERVICE.getListaClientes();
        if (clientes.isEmpty()) {
            System.out.println("A lista de clientes está vazia.");
        } else {
            System.out.println("\n--- LISTA DE CLIENTES ---");
            for (int i = 0; i < clientes.size(); i++) {
                System.out.printf("%d. %s\n", (i + 1), clientes.get(i));
            }
        }
        aguardarConfirmacao(scanner);
    }
    
    private static void gerarListaPedidos(Scanner scanner) {
        List<Pedido> pedidos = SERVICE.getListaPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("A lista de pedidos está vazia.");
        } else {
            System.out.println("\n--- LISTA DE PEDIDOS REGISTRADOS ---");
            for (Pedido pedido : pedidos) {
                System.out.println("------------------------------------");
                System.out.printf("PEDIDO #%d | CLIENTE: %s | TOTAL: R$ %.2f (Frete: R$ %.2f)\n", 
                    pedido.getId(), pedido.getCliente().getNome(), pedido.getValorTotal(), pedido.getFrete());
                
                System.out.println("  PIZZAS:");
                if (pedido.getPizzas().isEmpty()) {
                    System.out.println("   (Nenhuma pizza neste pedido)");
                } else {
                    pedido.getPizzas().forEach(pizza -> System.out.println("   > " + pizza));
                }
                
                System.out.println("  BEBIDAS:");
                if (pedido.getBebidas().isEmpty()) {
                    System.out.println("   (Nenhuma bebida neste pedido)");
                } else {
                    pedido.getBebidas().forEach(bebida -> System.out.println("   > " + bebida.getNomeFormatado()));
                }
            }
            System.out.println("------------------------------------");
        }
        aguardarConfirmacao(scanner);
    }
}