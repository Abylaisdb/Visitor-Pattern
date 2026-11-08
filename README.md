# Visitor Pattern — Advanced File System

**Assignment:** 06 — Visitor Pattern · Abylai Duisim (SE-2530)
**Stack:** Java 17, Maven, JUnit 5

An in-memory hierarchical file system with three independent operations implemented
through the Visitor pattern: total size, search and statistics.

---

## 1. How to run

All commands are executed from the project root (the folder with `pom.xml`).

```bash
# run all automated tests (no IDE or manual input needed)
mvn test

# run the deterministic demo (class Main)
mvn -q compile exec:java
```

Requirements: JDK 17+ and Maven 3.8+.

## 2. Project structure

```
pom.xml
README.md
src/                         production code
  Element.java               common element interface
  Node.java                  shared name / parent handling (sealed)
  FileNode.java              leaf element (name + non-negative size)
  DirectoryNode.java         composite element (named children)
  Visitor.java               visit(FileNode), visit(DirectoryNode)
  ResultVisitor.java         visitor with begin(root) / result()
  TreeWalker.java            the single traversal owner
  NodePaths.java             root-relative path and depth helper
  TotalSizeVisitor.java      operation 1
  SearchVisitor.java         operation 2
  StatisticsVisitor.java     operation 3 (added last)
  TreeIntegrityException.java
  Main.java                  demo with my own data
test/                        JUnit 5 tests
```

## 3. Design

### Object model (R1)
`Element` is the common interface. `FileNode` holds a name and a size in bytes
(size >= 0). `DirectoryNode` holds named children and can be nested arbitrarily deep.
Both extend the sealed class `Node`, which keeps the name and the parent reference.

### Double dispatch (R2)
1. The client calls `element.accept(visitor)`: the **runtime type of the element**
   selects the `accept` implementation.
2. `FileNode.accept` calls `visitor.visit(this)` and `DirectoryNode.accept` does the
   same: the **static type of `this`** selects the matching `visit` overload.

There is no `instanceof`, `getClass()`, reflection or cast anywhere in the code.

### Visitors
| Visitor | Result | Rule |
|---|---|---|
| `TotalSizeVisitor` (R3) | `Long` | sum of file sizes; directories add nothing; each file counted once |
| `SearchVisitor` (R4) | `List<String>` | file name matches the criterion **and** `size >= threshold`; root-relative paths with `/`, sorted lexicographically |
| `StatisticsVisitor` (R5) | `Statistics(fileCount, directoryCount, maxDepth)` | directory count includes the root if it is a directory; root depth = 0 |

The search name criterion is a `Predicate<String>`. `SearchVisitor.nameContains(text, min)`
is a convenience factory for a case-insensitive "contains" check.

### Traversal owner (R7)
The traversal is owned by **`TreeWalker`**, not by the visitors or the elements.

- order: pre-order (node first, then its children);
- children are always sorted by name, so the order is deterministic;
- every reachable node is visited exactly once per operation;
- empty and nested directories work without special cases.

*Why a walker:* visitors stay tiny (only per-node work), the descent logic exists in
one place, and a new operation never needs to re-implement traversal.

### Paths and maximum depth
`NodePaths` derives both values from the **parent chain** between a node and the chosen
root.

- depth = number of parent steps from the node up to the root (root = 0);
- path = names of the nodes below the root joined with `/`;
- the root can be any node, so paths are relative to the subtree that was passed in;
- if the root itself is a file, its path is just its name.

### Controlled state
Visitors are **reusable**. `TreeWalker.run(root, visitor)` calls `visitor.begin(root)`
before every traversal, which resets all accumulated state and stores the root.
`result()` returns an immutable value (or an immutable copy), so an earlier result is
not changed by a later run. The result never depends on run order.

> Always run visitors through `TreeWalker.run(...)`. Calling `accept` manually skips
> `begin(...)` and is only meant for low-level tests.

### Tree integrity (R6)
Enforced in the constructors and in `DirectoryNode.add`:

| Invalid change | Result |
|---|---|
| negative file size | `TreeIntegrityException` |
| null / blank name, name containing `/` | `TreeIntegrityException` |
| duplicate sibling name | `TreeIntegrityException` |
| adding a node that already has a parent | `TreeIntegrityException` |
| cycle (adding self or an ancestor) | `TreeIntegrityException` |
| adding `null` | `TreeIntegrityException` |

A node can be moved only after `remove(name)` released it. The child collection is
private; `getChildren()` returns an immutable sorted copy.

### Extension boundary (R8)
`StatisticsVisitor` was added **after** the `Visitor` contract and both elements
existed, and `FileNode`, `DirectoryNode` and their `accept` methods were not touched.

- **New operation** → one new visitor class. Elements, walker and other visitors stay unchanged.
- **New element type** (e.g. `SymlinkNode`) → new `visit(SymlinkNode)` in `Visitor`,
  a new `accept`, and an update of **every** existing visitor.

### Trade-off of a new element type
The Visitor pattern makes adding operations cheap and adding element types expensive:
the `Visitor` interface changes, so every implementation must be edited and
recompiled. It fits a stable element hierarchy with a growing set of operations.
If the element types change often, a polymorphic method on the elements would be
simpler.

## 4. Clean Code practices

| # | Practice | Where | Justification |
|---|---|---|---|
| 1 | Meaningful naming | `TotalSizeVisitor`, `NodePaths.relativePath`, `minSizeInBytes` | names state exactly what the class, method or field does and in which unit |
| 2 | Single Responsibility | `TreeWalker` only traverses, `NodePaths` only computes paths and depth, each visitor does one operation | each class has one reason to change |
| 3 | Small methods | `DirectoryNode.requireAddable`, `isSelfOrAncestor`, `SearchVisitor.visit` | every method does one thing and fits on a few lines |
| 4 | Encapsulation | private `children` map in `DirectoryNode`, `getChildren()` returns `List.copyOf` | invariants can be changed only through `add` / `remove` |
| 5 | No type-checking chains | `FileNode.accept` / `DirectoryNode.accept` | polymorphism and overloads replace `if / instanceof` branching |

## 5. Tests (JUnit 5)

22 tests in 5 classes, all passing.

| Check | What is verified | Test class |
|---|---|---|
| T1 | both element types reach the right `visit` overload; each node visited once, stable order | `VisitorDispatchTest` |
| T2 | size accumulation over several nesting levels, subtree root, file as root | `TotalSizeVisitorTest` |
| T3 | name and size conditions combined; threshold is inclusive (`>=`) | `SearchVisitorTest` |
| T4 | root-relative, `/`-separated, sorted paths; paths relative to a subtree | `SearchVisitorTest` |
| T5 | file count, directory count, max depth for a nested tree | `StatisticsVisitorTest` |
| T6 | empty directory, file as root, no-match search | `SearchVisitorTest`, `StatisticsVisitorTest`, `TotalSizeVisitorTest` |
| T7 | repeated runs and different roots do not leak or multiply state | `StateAndIntegrityTest` |
| T8 | invalid mutations rejected, valid deep nesting still works, children not writable | `StateAndIntegrityTest` |

Test tree used in the tests and in the demo:

```
root
├── a.txt            100
├── docs/
│   ├── b.txt        200
│   ├── report.pdf   5000
│   └── deep/
│       ├── c.txt    300
│       └── empty/
└── images/
    └── photo.jpg    4000
```

Expected values: total size 9600, 5 files, 5 directories, max depth 3.

## 6. Sample output (`mvn -q compile exec:java`)

```
Total size: 9600 bytes
Statistics: Statistics[fileCount=5, directoryCount=5, maxDepth=3]
Search '.txt', size >= 150:
  docs/b.txt
  docs/deep/c.txt
Search 'photo', size >= 10000 (no match):
```
