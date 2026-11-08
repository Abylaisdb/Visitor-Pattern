import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class VisitorDispatchTest {

    private static final class RecordingVisitor implements Visitor {
        final List<String> calls = new ArrayList<>();

        @Override
        public void visit(FileNode file) {
            calls.add("file:" + file.getName());
        }

        @Override
        public void visit(DirectoryNode directory) {
            calls.add("dir:" + directory.getName());
        }
    }

    @Test
    void eachElementTypeReachesItsOwnOverload() {
        RecordingVisitor visitor = new RecordingVisitor();
        new FileNode("f.txt", 1).accept(visitor);
        new DirectoryNode("d").accept(visitor);
        assertEquals(List.of("file:f.txt", "dir:d"), visitor.calls);
    }

    @Test
    void walkerVisitsEveryNodeExactlyOnceInStableOrder() {
        RecordingVisitor visitor = new RecordingVisitor();
        TreeWalker.walk(FileSystemFixture.sampleTree(), visitor);
        assertEquals(List.of(
                "dir:root", "file:a.txt", "dir:docs", "file:b.txt", "dir:deep", "file:c.txt",
                "dir:empty", "file:report.pdf", "dir:images", "file:photo.jpg"), visitor.calls);
    }
}
