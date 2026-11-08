public final class TreeWalker {
    private TreeWalker() {
    }

    public static <R> R run(Element root, ResultVisitor<R> visitor) {
        if (root == null || visitor == null) {
            throw new IllegalArgumentException("root and visitor must not be null");
        }
        visitor.begin(root);
        walk(root, visitor);
        return visitor.result();
    }

    public static void walk(Element root, Visitor visitor) {
        root.accept(visitor);
        for (Element child : root.getChildren()) {
            walk(child, visitor);
        }
    }
}
