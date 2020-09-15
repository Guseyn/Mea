package jreader;

@FunctionalInterface
public interface ContentModifier {
    String newContent(String oldContentContent);
}
