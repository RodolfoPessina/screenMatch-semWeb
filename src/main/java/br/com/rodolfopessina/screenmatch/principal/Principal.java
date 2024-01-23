package br.com.rodolfopessina.screenmatch.principal;

import br.com.rodolfopessina.screenmatch.model.DadosEpisodios;
import br.com.rodolfopessina.screenmatch.model.DadosSerie;
import br.com.rodolfopessina.screenmatch.model.DadosTemporada;
import br.com.rodolfopessina.screenmatch.model.Episodio;
import br.com.rodolfopessina.screenmatch.service.ConsumoApi;
import br.com.rodolfopessina.screenmatch.service.ConvertDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final String ENDERECO = "https://www.omdbapi.com/?t=" ;
    private final String API_KEY = "&apikey=94a137eb";

    private ConsumoApi consumo = new ConsumoApi();

    private ConvertDados conversor = new ConvertDados();
    Scanner sc = new Scanner(System.in);

    public void exibirMenu(){
        System.out.println("digite uma serie: ");
        var nomeSerie = sc.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ","+") + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i < dadosSerie.totalTemporadas(); i++ ){
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ","+") + "&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, br.com.rodolfopessina.screenmatch.model.DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
        temporadas.forEach(System.out::println);

        List<DadosEpisodios> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\ntop 10 episodios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("n/a"))
                .sorted(Comparator.comparing(DadosEpisodios::dataLancamento).reversed())
                .map(e-> e.titulo().toUpperCase())
                .limit(10)
                .forEach(System.out::println);

        List<Episodio> episodio = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

            episodio.forEach(System.out::println);

        System.out.println("digite um trecho: ");
            var trechoTitulo = sc.nextLine();
        Optional<Episodio> episodioBuscado = episodio.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()){
            System.out.println("encontrado!");
            System.out.println("episodio" + episodioBuscado.get().getTemporada());
        } else {
            System.out.println("não encontrado");
        }

        System.out.println("digite a partir de que ano deseja ver os episodios: ");
        var ano = sc.nextLine();
        sc.nextLine();

        LocalDate dataBusca = LocalDate.of(Integer.parseInt(ano), 1 , 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodio.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episódio: " + e.getTitulo() +
                                " Data lançamento: " + e.getDataLancamento().format(formatador)
                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodio.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodio.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());


    }

}
