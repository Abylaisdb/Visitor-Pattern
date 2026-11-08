import java.util.ArrayDeque;
import java.util.Deque;

final class NodePaths {
    private NodePaths() {
    }

    static int depth(Element node, Element root) {
        return namesBelowRoot(node, root).size();
    }

    static String relativePath(Element node, Element root) {
        Deque<String> names = namesBelowRoot(node, root);
        return names.isEmpty() ? node.getName() : String.join("/", names);
    }

    private static Deque<String> namesBelowRoot(Element node, Element root) {
        Deque<String> names = new ArrayDeque<>();
        Element current = node;
        while (current != root) {
            names.addFirst(current.getName());
            current = current.getParent()
                    .orElseThrow(() -> new IllegalArgumentException("Node is not inside the given root"));
        }
        return names;
    }
}
