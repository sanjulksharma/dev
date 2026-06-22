#!/usr/bin/env bash
# Compile every .java file under src/ into out/, with lib/*.jar on the classpath.
#
# Run a specific demo afterwards, e.g.:
#   ./compile.sh
#   java -cp "out:lib/*" lld.practice.fintech.splitswise.SplitswiseMain
#   java -cp "out:lib/*" lld.practice.infra.urlshortener.Application

set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SRC="$ROOT/src"
OUT="$ROOT/out"
LIB="$ROOT/lib"

rm -rf "$OUT"
mkdir -p "$OUT"

find "$SRC" -name '*.java' > "$OUT/.sources"
javac -d "$OUT" -sourcepath "$SRC" -cp "$LIB/*" @"$OUT/.sources"
rm "$OUT/.sources"

echo "Build OK → $OUT"
