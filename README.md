# dev — personal design practice

Plain-Java repo collecting LLD/HLD practice. No Gradle/Maven — each file declares its package and is compiled with a tiny script.

## Layout

```
.
├── compile.sh            # compile every .java under src/ into out/
├── lib/                  # third-party jars (only reactor-core, used by hld/protocols/tcp)
└── src/
    ├── hld/
    │   └── protocols/{tcp,udp}/                   # network protocol playgrounds
    └── lld/
        ├── patterns/abstractfactory/              # GoF pattern reference impls
        └── practice/
            ├── fintech/
            │   └── splitswise/                    # Splitwise LLD (Strategy, Observer, Repository)
            ├── mobility/
            │   ├── uber/
            │   └── ticketbooking/
            └── infra/
                ├── urlshortener/
                ├── taskprocessor/                 # single-node task processor
                ├── distributedtaskqueue/          # distributed task queue
                └── notificationservice/           # multi-channel notifications (email/SMS/push/in-app)
```

Each LLD problem is a self-contained sub-package. Domain folders (`fintech`, `mobility`, `infra`) exist so the practice list stays grep-able as it grows.

## Build & run

```bash
./compile.sh

# Run any class with a main():
java -cp "out:lib/*" lld.practice.fintech.splitswise.SplitswiseMain
java -cp "out:lib/*" lld.practice.infra.urlshortener.Application
java -cp "out:lib/*" lld.practice.infra.taskprocessor.TaskProcessorSimulator
java -cp "out:lib/*" lld.practice.infra.notificationservice.NotificationMain
java -cp "out:lib/*" lld.patterns.abstractfactory.ApplicationClient
```

## Dependencies

The only third-party dependency is `reactor-core` (used in `hld/protocols/tcp/TCPClient`). The jars are checked into `lib/`:

- `reactor-core-3.5.14.jar`
- `reactive-streams-1.0.4.jar`

If you add a new library, drop the jar in `lib/` and rerun `compile.sh`.

## Per-problem READMEs

Problems with non-trivial design docs ship their own README inside the package, e.g. `src/lld/practice/fintech/splitswise/README.md` walks through the patterns and SOLID mapping used there.
