#!/bin/sh
cd station-ws
mvn compile
cd ..

tmux new-session -c station-ws -d "mvn exec:java -Dws.i=1"
tmux split-window -c station-ws -h "mvn exec:java -Dws.i=2"
tmux split-window -c station-ws-cli -v "sleep 1; mvn compile install; cd ../binas-ws; mvn compile; mvn exec:java"
tmux select-pane -L
tmux split-window -c station-ws -v "mvn exec:java -Dws.i=3"
tmux select-pane -R;
tmux new-window 'mutt'
tmux -2 attach-session -d
