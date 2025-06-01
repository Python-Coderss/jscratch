#!/bin/bash
echo "This is an error message from error_script.sh, going to stderr." >&2
echo "This is a normal output line from error_script.sh, going to stdout."
exit 123
