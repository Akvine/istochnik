package ru.akvine.istochnik.services.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;

/**
 * Граф зависимостей: каждому узлу сопоставлены узлы, от которых он зависит.
 * Зависимость от узла, которого нет среди ключей, считается уже выполненной
 */
@UtilityClass
public class DependencyGraph {

    /**
     * Ищет цикл в графе
     *
     * @return узлы цикла в порядке обхода, первый узел повторён в конце; пустой список, если циклов нет
     */
    public <T> List<T> findCycle(Map<T, Set<T>> dependencies) {
        Set<T> checked = new HashSet<>();
        for (T node : dependencies.keySet()) {
            List<T> cycle = findCycle(node, dependencies, new LinkedHashSet<>(), checked);
            if (!cycle.isEmpty()) {
                return cycle;
            }
        }

        return List.of();
    }

    /**
     * Раскладывает узлы по уровням: узел зависит только от узлов предыдущих уровней,
     * поэтому узлы одного уровня можно обрабатывать одновременно
     *
     * @throws IllegalArgumentException если в графе есть цикл
     */
    public <T> List<List<T>> levels(Map<T, Set<T>> dependencies) {
        List<List<T>> levels = new ArrayList<>();
        Set<T> ready = new HashSet<>();
        Set<T> remaining = new LinkedHashSet<>(dependencies.keySet());

        while (!remaining.isEmpty()) {
            List<T> level = remaining.stream()
                    .filter(node -> dependencies.get(node).stream()
                            .allMatch(
                                    dependency -> ready.contains(dependency) || !dependencies.containsKey(dependency)))
                    .toList();
            if (level.isEmpty()) {
                throw new IllegalArgumentException("Dependencies contain a cycle: " + findCycle(dependencies));
            }

            levels.add(level);
            ready.addAll(level);
            level.forEach(remaining::remove);
        }

        return levels;
    }

    private <T> List<T> findCycle(T node, Map<T, Set<T>> dependencies, LinkedHashSet<T> path, Set<T> checked) {
        if (checked.contains(node)) {
            return List.of();
        }

        if (!path.add(node)) {
            // Узел уже есть в текущем пути: цикл - часть пути от первого вхождения узла
            List<T> visited = new ArrayList<>(path);
            List<T> cycle = new ArrayList<>(visited.subList(visited.indexOf(node), visited.size()));
            cycle.add(node);
            return cycle;
        }

        for (T dependency : dependencies.getOrDefault(node, Set.of())) {
            List<T> cycle = findCycle(dependency, dependencies, path, checked);
            if (!cycle.isEmpty()) {
                return cycle;
            }
        }

        path.remove(node);
        checked.add(node);
        return List.of();
    }
}
