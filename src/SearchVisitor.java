import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;


public final class SearchVisitor implements ResultVisitor<List<String>> {
    private final Predicate<String> nameCriterion;
    private final long minSizeInBytes;
    private final List<String> matches = new ArrayList<>();
    private Element root;

    public SearchVisitor(Predicate<String> nameCriterion, long minSizeInBytes) {
        if (nameCriterion == null) {
            throw new IllegalArgumentException("nameCriterion must not be null");
        }
        if (minSizeInBytes < 0) {
            throw new IllegalArgumentException("minSizeInBytes must not be negative");
        }
        this.nameCriterion = nameCriterion;
        this.minSizeInBytes = minSizeInBytes;
    }

    public static SearchVisitor nameContains(String text, long minSizeInBytes) {
        String needle = text.toLowerCase(Locale.ROOT);
        return new SearchVisitor(name -> name.toLowerCase(Locale.ROOT).contains(needle), minSizeInBytes);
    }

    @Override
    public void begin(Element root) {
        this.root = root;
        matches.clear();
    }

    @Override
    public void visit(FileNode file) {
        if (file.getSizeInBytes() >= minSizeInBytes && nameCriterion.test(file.getName())) {
            matches.add(NodePaths.relativePath(file, root));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    @Override
    public List<String> result() {
        List<String> sorted = new ArrayList<>(matches);
        sorted.sort(String::compareTo);
        return List.copyOf(sorted);
    }
}
