import java.util.List;

public final class FileNode extends Node {
    private final long sizeInBytes;

    public FileNode(String name, long sizeInBytes) {
        super(name);
        if (sizeInBytes < 0) {
            throw new TreeIntegrityException("File size must not be negative: " + sizeInBytes);
        }
        this.sizeInBytes = sizeInBytes;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public List<Element> getChildren() {
        return List.of();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
