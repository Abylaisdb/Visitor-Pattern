import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TotalSizeVisitorTest {

    @Test
    void sumsFilesAcrossNestingLevels() {
        assertEquals(9600L, TreeWalker.run(FileSystemFixture.sampleTree(), new TotalSizeVisitor()));
    }

    @Test
    void sumsOnlyTheChosenSubtree() {
        DirectoryNode root = FileSystemFixture.sampleTree();
        DirectoryNode docs = (DirectoryNode) root.getChildren().get(1);
        assertEquals(5500L, TreeWalker.run(docs, new TotalSizeVisitor()));
    }

    @Test
    void fileAsRootContributesItsOwnSize() {
        assertEquals(42L, TreeWalker.run(new FileNode("x", 42), new TotalSizeVisitor()));
    }
}
