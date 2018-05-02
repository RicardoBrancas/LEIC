#!/bin/sh
cd station-ws
mvn -q  compile
cd ..

tmux new-session -c station-ws -d "bash"
tmux send-keys mvn Space -q  Space exec:java Space -Dws.i=1 Enter
tmux split-window -c station-ws -h "bash"
tmux send-keys mvn Space -q  Space exec:java Space -Dws.i=2 Enter
tmux split-window -c binas-ws -v "bash"
tmux send-keys mvn Space -q  Space compile Enter mvn Space -q  Space exec:java Enter
tmux select-pane -L
tmux split-window -c station-ws -v "bash"
tmux send-keys mvn Space -q  Space exec:java Space -Dws.i=3 Enter
tmux select-pane -R;
tmux new-window 'mutt'
tmux -2 attach-session -d
