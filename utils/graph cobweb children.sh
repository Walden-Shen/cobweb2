tail +2 report.tsv | head -n 300 | gawk 'BEGIN { print "digraph G {"} $5 != -1 { printf "\"%s\" -> \"%s\"\n", $1, $5 } $4 != -1 { printf "\"%s\" -> \"%s\"\n", $1, $4 } END { print "}"} ' | dot -v -Tpng > test.png