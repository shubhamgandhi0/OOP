# MiniBank

MiniBank is a console banking application assembled from the practical exercises.
It uses packaged model, validation, repository, persistence, and service layers.

## Build and run with JDK tools

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory out | Out-Null
javac --release 17 -d out (Get-ChildItem src\main\java -Recurse -Filter *.java)
java -cp out com.minibank.MiniBank
```

## Build a runnable JAR

```powershell
jar --create --file minibank.jar --main-class com.minibank.MiniBank -C out .
java -jar minibank.jar
```

The application creates its `data` and `reports` directories when the corresponding
operations are used. Account state is saved on each change and loaded on startup.

## Test

The test source is under `src\test\java`. With Maven installed, run:

```powershell
mvn clean test package
```

The tests cover persistence, validation of failed withdrawals, transfer concurrency,
and command parsing.
