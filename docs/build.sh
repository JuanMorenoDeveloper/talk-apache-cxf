#!/bin/bash
pandoc -t slidy -s -o index.html index.md -V theme=white
