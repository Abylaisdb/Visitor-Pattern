import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class StateAndIntegrityTest {

    @Test
    void repeatedRunsOfTheSameVisitorDoNotAccumulateState() {
        DirectoryNode tree = FileSystemFixture.sampleTree();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = SearchVisitor.nameContains(".txt", 0);
        StatisticsVisitor stats = new StatisticsVisitor();

        for (int i = 0; i < 3; i++) {
            assertEquals(9600L, TreeWalker.run(tree, size));
            assertEquals(List.of("a.txt", "docs/b.txt", "docs/deep/c.txt"), TreeWalker.run(tree, search));
            assertEquals(new StatisticsVisitor.Statistics(5, 5, 3), TreeWalker.run(tree, stats));
        }
    }

    @Test
    void resultDoesNotDependOnPreviousRoot() {
        TotalSizeVisitor size = new TotalSizeVisitor();
        TreeWalker.run(FileSystemFixture.sampleTree(), size);
        assertEquals(7L, TreeWalker.run(new FileNode("z", 7), size));
    }

    @Test
    void returnedResultIsNotAffectedByLaterRuns() {
        DirectoryNode tree = FileSystemFixture.sampleTree();
        SearchVisitor search = SearchVisitor.nameContains(".txt", 0);
        List<String> first = TreeWalker.run(tree, search);
        TreeWalker.run(new DirectoryNode("empty"), search);
        assertEquals(3, first.size());
    }

    @Test
    void rejectsNegativeSizeAndBadNames() {
        assertThrows(TreeIntegrityException.class, () -> new FileNode("a", -1));
        assertThrows(TreeIntegrityException.class, () -> new FileNode(" ", 1));
        assertThrows(TreeIntegrityException.class, () -> new DirectoryNode("a/b"));
        assertThrows(TreeIntegrityException.class, () -> new DirectoryNode(null));
    }

    @Test
    void rejectsDuplicateSiblingNames() {
        DirectoryNode dir = new DirectoryNode("d").add(new FileNode("x", 1));
        assertThrows(TreeIntegrityException.class, () -> dir.add(new DirectoryNode("x")));
    }

    @Test
    void rejectsSecondParentUntilNodeIsRemoved() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode file = new FileNode("f", 1);
        first.add(file);
        assertThrows(TreeIntegrityException.class, () -> second.add(file));
        assertTrue(first.remove("f"));
        second.add(file); // valid after removal
        assertEquals("second", file.getParent().orElseThrow().getName());
    }

    @Test
    void rejectsCyclesAndSelfInsertion() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        DirectoryNode c = new DirectoryNode("c");
        a.add(b);
        b.add(c);
        assertThrows(TreeIntegrityException.class, () -> a.add(a));
        assertThrows(TreeIntegrityException.class, () -> c.add(a));
        assertThrows(TreeIntegrityException.class, () -> b.add(a));
        assertThrows(TreeIntegrityException.class, () -> a.add(null));
    }

    @Test
    void validNestingWorksAndChildrenAreNotWritable() {
        DirectoryNode root = new DirectoryNode("r");
        DirectoryNode current = root;
        for (int i = 0; i < 50; i++) {
            DirectoryNode next = new DirectoryNode("d" + i);
            current.add(next);
            current = next;
        }
        current.add(new FileNode("leaf", 5));
        assertEquals(new StatisticsVisitor.Statistics(1, 51, 51), TreeWalker.run(root, new StatisticsVisitor()));
        assertThrows(UnsupportedOperationException.class, () -> root.getChildren().clear());
    }
}
