/**
 * root
 * ├─ a.txt (100)
 * ├─ docs/ ── b.txt (200), report.pdf (5000), deep/ ── c.txt (300), empty/
 * └─ images/ ── photo.jpg (4000)
 */
final class FileSystemFixture {
    private FileSystemFixture() {
    }

    static DirectoryNode sampleTree() {
        return Main.buildSampleTree();
    }
}
