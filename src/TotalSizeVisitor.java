public final class TotalSizeVisitor implements ResultVisitor<Long> {
    private long total;

    @Override
    public void begin(Element root) {
        total = 0;
    }

    @Override
    public void visit(FileNode file) {
        total = Math.addExact(total, file.getSizeInBytes());
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    @Override
    public Long result() {
        return total;
    }
}
