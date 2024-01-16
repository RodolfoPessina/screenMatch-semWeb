package br.com.rodolfopessina.screenmatch.service;

public interface iConvertDados {
    <T> T obterDados(String Json, Class<T> classe);
}
