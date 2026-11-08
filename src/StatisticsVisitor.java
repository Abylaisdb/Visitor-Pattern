public final class StatisticsVisitor implements ResultVisitor<StatisticsVisitor.Statistics> {

    public record Statistics(int fileCount, int directoryCount, int maxDepth) {
    }

    private int fileCount;
    private int directoryCount;
    private int maxDepth;
    private Element root;

    @Override
    public void begin(Element root) {
        this.root = root;
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        recordDepth(file);
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        recordDepth(directory);
    }

    private void recordDepth(Element node) {
        maxDepth = Math.max(maxDepth, NodePaths.depth(node, root));
    }

    @Override
    public Statistics result() {
        return new Statistics(fileCount, directoryCount, maxDepth);
    }
}
