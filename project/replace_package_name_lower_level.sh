#!/bin/bash

# Function to perform search and replace in a directory
replace_in_directory() {
  local dir=$1
  echo "Processing directory: $dir"
  grep -rl 'com.example.myapplication' "$dir" 2>/dev/null | xargs sed -i 's/com\.example\.myapplication/no.uio.ifi.in2000.team8/g'
}

# Start from the current directory and traverse downwards
for directory in */; do
  replace_in_directory "$directory"
done

echo "Replacement complete."
