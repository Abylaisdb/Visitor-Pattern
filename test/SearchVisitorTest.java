import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SearchVisitorTest {

    @Test
    void combinesNameAndMinimumSizeConditions() {
        List<String> found = TreeWalker.run(FileSystemFixture.sampleTree(),
                SearchVisitor.nameContains(".txt", 150));
        assertEquals(List.of("docs/b.txt", "docs/deep/c.txt"), found);
    }

    @Test
    void sizeThresholdIsInclusive() {
        List<String> found = TreeWalker.run(FileSystemFixture.sampleTree(),
                SearchVisitor.nameContains(".txt", 100));
        assertEquals(List.of("a.txt", "docs/b.txt", "docs/deep/c.txt"), found);
    }
    @Test
    void returnsSortedRootRelativePaths() {
        List<String> found = TreeWalker.run(FileSystemFixture.sampleTree(), SearchVisitor.nameContains("", 0));
        assertEquals(List.of("a.txt", "docs/b.txt", "docs/deep/c.txt", "docs/report.pdf", "images/photo.jpg"),
                found);
    }

    @Test
    void pathsAreRelativeToSubtreeRoot() {
        DirectoryNode docs = (DirectoryNode) FileSystemFixture.sampleTree().getChildren().get(1);
        assertEquals(List.of("b.txt", "deep/c.txt"),
                TreeWalker.run(docs, SearchVisitor.nameContains(".txt", 0)));
    }


    @Test
    void noMatchAndEmptyDirectoryGiveEmptyResult() {
        assertTrue(TreeWalker.run(FileSystemFixture.sampleTree(), SearchVisitor.nameContains("photo", 10_000))
                .isEmpty());
        assertTrue(TreeWalker.run(new DirectoryNode("empty"), SearchVisitor.nameContains("", 0)).isEmpty());
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> SearchVisitor.nameContains("a", -1));
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(null, 0));
    }
}
