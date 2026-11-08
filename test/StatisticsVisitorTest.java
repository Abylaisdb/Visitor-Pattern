import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StatisticsVisitorTest {

    @Test
    void countsFilesDirectoriesAndMaxDepth() {
        StatisticsVisitor.Statistics stats = TreeWalker.run(FileSystemFixture.sampleTree(), new StatisticsVisitor());
        assertEquals(new StatisticsVisitor.Statistics(5, 5, 3), stats);
    }

    @Test
    void emptyDirectoryCountsItselfOnly() {
        assertEquals(new StatisticsVisitor.Statistics(0, 1, 0), TreeWalker.run(new DirectoryNode("e"), new StatisticsVisitor()));
    }

    @Test
    void fileRootHasDepthZero() {
        assertEquals(new StatisticsVisitor.Statistics(1, 0, 0), TreeWalker.run(new FileNode("f", 1), new StatisticsVisitor()));
    }
}
