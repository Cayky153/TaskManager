package com.example.demo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ListaTarefasGenerics<T extends Tarefas> {

    private final List<T> elementos = new ArrayList<>();

    public void adicionar(T tarefa) {
        elementos.add(tarefa);
    }
    public void adicionarTodos(Collection<? extends T> origem) {
        elementos.addAll(origem);
    }
    public boolean removerPorId(Long id) {
        return elementos.removeIf(t -> t.getId().equals(id));
    }

    public void limpar() {
        elementos.clear();
    }
    public void ordenar(Comparator<? super T> criterio) {
        elementos.sort(criterio);
    }

    public List<T> listarTodos() {
        return new ArrayList<>(elementos);
    }

    public static int contar(List<?> lista) {
        return lista.size();
    }
}