#!/bin/bash
for file in *.java; do
  # Fix lines starting with . to add import
  sed -i 's/^\.[a-z]/import &/g' "$file"
  # Fix the import lines that got double-import
  sed -i 's/^import import /import /g' "$file"
  echo "Fixed $file"
done
