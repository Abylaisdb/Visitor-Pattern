import java.util.Optional;

public abstract sealed class Node implements Element permits FileNode, DirectoryNode {
    private final String name;
    private DirectoryNode parent;

    protected Node(String name) {
        this.name = requireValidName(name);
    }

    private static String requireValidName(String name) {
        if (name == null || name.isBlank()) {
            throw new TreeIntegrityException("Name must not be null or blank");
        }
        if (name.contains("/")) {
            throw new TreeIntegrityException("Name must not contain '/': " + name);
        }
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<DirectoryNode> getParent() {
        return Optional.ofNullable(parent);
    }

    void attachTo(DirectoryNode newParent) {
        this.parent = newParent;
    }

    void detach() {
        this.parent = null;
    }

    boolean hasParent() {
        return parent != null;
    }
}
