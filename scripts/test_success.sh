#!/bin/bash
echo "Test script started."
TEMP_FILE="test_output.txt"
echo "This is a test file." > $TEMP_FILE
cat $TEMP_FILE
rm $TEMP_FILE
echo "Test script finished successfully."
exit 0
