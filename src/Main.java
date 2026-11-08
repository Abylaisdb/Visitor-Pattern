public final class Main {
    public static void main(String[] args) {
        DirectoryNode root = buildSampleTree();

        System.out.println("Total size: " + TreeWalker.run(root, new TotalSizeVisitor()) + " bytes");
        System.out.println("Statistics: " + TreeWalker.run(root, new StatisticsVisitor()));

        System.out.println("Search '.txt', size >= 150:");
        TreeWalker.run(root, SearchVisitor.nameContains(".txt", 150))
                .forEach(path -> System.out.println("  " + path));

        System.out.println("Search 'photo', size >= 10000 (no match):");
        TreeWalker.run(root, SearchVisitor.nameContains("photo", 10_000))
                .forEach(path -> System.out.println("  " + path));
    }

    static DirectoryNode buildSampleTree() {
        DirectoryNode deep = new DirectoryNode("deep")
                .add(new FileNode("c.txt", 300))
                .add(new DirectoryNode("empty"));
        DirectoryNode docs = new DirectoryNode("docs")
                .add(new FileNode("b.txt", 200))
                .add(new FileNode("report.pdf", 5000))
                .add(deep);
        DirectoryNode images = new DirectoryNode("images")
                .add(new FileNode("photo.jpg", 4000));
        return new DirectoryNode("root")
                .add(new FileNode("a.txt", 100))
                .add(docs)
                .add(images);
    }
}
