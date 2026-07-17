# Doxabeta Internship Management System — Incomplete Prototype

**Status:** Mid-point / work-in-progress build — for the interim presentation only.

## What this demonstrates
- Core OOP domain model: `Student`, `Mentor`
- A working console menu (add student, view all students, view mentors, exit)
- A **last-name sorting algorithm** (`StudentSorter`) implemented as **Bubble Sort** — O(n²), chosen for this stage because it's simple to explain and verify by hand against a small seeded dataset

## What is deliberately NOT included yet
- Reviews (mid / final)
- Daily hours log entries
- Cohort / college / project-level reporting
- Full input validation
- Persistence (data resets each run)

These are addressed in the **complete prototype**, including replacing Bubble Sort with Merge Sort once the dataset and feature set are larger.

## How to run

### In VS Code
1. Install the **Extension Pack for Java** (Microsoft) from the Extensions panel.
2. Open this folder in VS Code (`File > Open Folder`, select the `incomplete` folder — the one containing this README).
3. Open `src/doxabeta/Main.java`. A **Run** button will appear above `public static void main`. Click it.
   - A pre-configured `.vscode/launch.json` is included, so Run/Debug should work immediately with no extra setup.

### From the terminal
```bash
cd src
javac doxabeta/*.java -d ../out
java -cp ../out doxabeta.Main
```

## Project structure
```
src/doxabeta/
  Student.java        - student domain model
  Mentor.java          - mentor domain model
  StudentSorter.java   - bubble sort by last name
  Main.java            - console UI and sample data
```

## Requirements
Java 17+ (developed/tested on OpenJDK 21).
