package ru.akvine.istochnik.services.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Dependency graph tests")
public class DependencyGraphTest {

    @Test
    @DisplayName("Nodes are split into levels - a node goes after everything it depends on")
    void nodes_are_split_into_levels() {
        Map<String, Set<String>> dependencies = new LinkedHashMap<>();
        dependencies.put("total", Set.of("net", "tax"));
        dependencies.put("tax", Set.of("net"));
        dependencies.put("net", Set.of("price", "qty"));
        dependencies.put("label", Set.of());

        assertThat(DependencyGraph.findCycle(dependencies)).isEmpty();
        assertThat(DependencyGraph.levels(dependencies))
                .containsExactly(List.of("net", "label"), List.of("tax"), List.of("total"));
    }

    @Test
    @DisplayName("Dependencies on nodes outside the graph are considered ready")
    void outside_dependencies_are_ready() {
        Map<String, Set<String>> dependencies = Map.of("total", Set.of("price", "qty"));

        assertThat(DependencyGraph.levels(dependencies)).containsExactly(List.of("total"));
    }

    @Test
    @DisplayName("Cycle is found and returned as a path")
    void cycle_is_found() {
        Map<String, Set<String>> dependencies = new LinkedHashMap<>();
        dependencies.put("independent", Set.of());
        dependencies.put("a", Set.of("b"));
        dependencies.put("b", Set.of("c"));
        dependencies.put("c", Set.of("a"));
        dependencies.put("dependent", Set.of("a"));

        assertThat(DependencyGraph.findCycle(dependencies)).containsExactly("a", "b", "c", "a");
        assertThatThrownBy(() -> DependencyGraph.levels(dependencies))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cycle");
    }

    @Test
    @DisplayName("Node that depends on itself is a cycle")
    void self_dependency_is_cycle() {
        Map<String, Set<String>> dependencies = Map.of("a", Set.of("a"));

        assertThat(DependencyGraph.findCycle(dependencies)).containsExactly("a", "a");
    }

    @Test
    @DisplayName("Empty graph has no levels")
    void empty_graph() {
        assertThat(DependencyGraph.levels(Map.<String, Set<String>>of())).isEmpty();
    }
}
