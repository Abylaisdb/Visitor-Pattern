public interface ResultVisitor<R> extends Visitor {
    void begin(Element root);

    R result();
}
