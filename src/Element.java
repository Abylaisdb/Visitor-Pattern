import java.util.List;
import java.util.Optional;
public interface Element {
    String getName();

    Optional<DirectoryNode> getParent();

    List<Element> getChildren();

    void accept(Visitor visitor);
}
