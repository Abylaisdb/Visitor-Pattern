import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class DirectoryNode extends Node {
    private final Map<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public DirectoryNode add(Node child) {
        requireAddable(child);
        children.put(child.getName(), child);
        child.attachTo(this);
        return this;
    }

    public boolean remove(String childName) {
        Node removed = children.remove(childName);
        if (removed == null) {
            return false;
        }
        removed.detach();
        return true;
    }

    private void requireAddable(Node child) {
        if (child == null) {
            throw new TreeIntegrityException("Child must not be null");
        }
        if (child.hasParent()) {
            throw new TreeIntegrityException("Node already has a parent: " + child.getName());
        }
        if (isSelfOrAncestor(child)) {
            throw new TreeIntegrityException("Adding '" + child.getName() + "' would create a cycle");
        }
        if (children.containsKey(child.getName())) {
            throw new TreeIntegrityException("Duplicate sibling name: " + child.getName());
        }
    }

    private boolean isSelfOrAncestor(Node candidate) {
        for (DirectoryNode current = this; current != null; current = current.getParent().orElse(null)) {
            if (current == candidate) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Element> getChildren() {
        return List.copyOf(children.values());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
